package com.ni3.ag.adminconsole.server.mapextraction;

import com.ni3.ag.adminconsole.server.mapextraction.packer.MapPacker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.Ni3FTPHelper;
import com.ni3.ag.adminconsole.server.TransferUtils;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class MapExtractor{

	private static final Logger log = Logger.getLogger(MapExtractor.class);

	private static final String GIS_SERVER_RASTERS_FILE = "GisServerRasters.txt";
	private final String localTempDirectory = Ni3FTPHelper.FTP_BASE_LOCAL_DIRECTORY;

	private TransferUtils transferUtils;

	private DataSource dataSource;
	private Ni3FTPHelper ftpHelper = null;
	private MapJobStatusUpdater statusUpdater;

	public void setTransferUtils(TransferUtils transferUtils){
		this.transferUtils = transferUtils;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setStatusUpdater(MapJobStatusUpdater statusUpdater){
		this.statusUpdater = statusUpdater;
	}

	private void createRasters(int scale, RasterCache rc, Map<Integer, List<String>> fileMap) throws ACException{
		if (fileMap.containsKey(scale)){
			List<String> filePaths = fileMap.get(scale);
			for (String filePath : filePaths){
				rc.createRaster(scale, filePath);
			}
		}
	}

	private void processMaps(MapJob job){
		setJobStatus(job, MapJobStatus.ProcessingMaps);
		BigDecimal x1 = job.getX1();
		BigDecimal x2 = job.getX2();
		BigDecimal y1 = job.getY1();
		BigDecimal y2 = job.getY2();
		User user = job.getUser();
		String scale = job.getScale();
		String[] scales = scale.split(",");
		RasterCache rc = new RasterCache(user.getUserName(), x1.doubleValue(), x2.doubleValue(), y1.doubleValue(), y2
		        .doubleValue());
		String mapPath = getMapPath();
		Map<Integer, List<String>> fileMap;
		try{
			if (ftpHelper != null)
				fileMap = parseConfigFileFTP(mapPath, ftpHelper, scales);
			else{
				if (!mapPath.endsWith(File.separator))
					mapPath += File.separator;
				fileMap = parseConfigFile(mapPath);
				deleteDirectory(mapPath + user.getUserName());
			}
			for (String s : scales){
				int scl = Integer.parseInt(s);
				createRasters(scl, rc, fileMap);
			}
		} catch (ACException e){
			log.error(e.getMessage(), e);
			setJobStatus(job, MapJobStatus.ErrorProcessingMaps);
		}
	}

	public void extractMap(MapJob job){
		User user = job.getUser();

		String mapPath = getMapPath();
		processMaps(job);
		if (MapJobStatus.isError(job.getStatus()))
			return;

		setJobStatus(job, MapJobStatus.CopyingToMapPath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String backupFile = user.getUserName() + "-" + sdf.format(new Date()) + ".tar.bz2";
		String localMapsDir = mapPath;

		boolean isFTP = mapPath.toLowerCase().startsWith("ftp://");
		if (isFTP){
			localMapsDir = localTempDirectory;
			ftpHelper.uploadUserDirectory(user.getUserName());
		}
		if (!localMapsDir.endsWith(File.separator))
			localMapsDir += File.separator;
		localMapsDir += user.getUserName();

		try{
			setJobStatus(job, MapJobStatus.Compressing);
			makeZIPDump(localTempDirectory, backupFile, localMapsDir);
			setJobStatus(job, MapJobStatus.CopyingToModulesPath);
			moveZIPToModulePath(localTempDirectory, backupFile);
		} catch (IOException e){
			setJobStatus(job, MapJobStatus.ErrorCopyingToModulesPath);
			log.error(e.getMessage(), e);
		} catch (ACException e){
			setJobStatus(job, MapJobStatus.ErrorCopyingToModulesPath);
			log.error(e.getMessage(), e);
		}

		cleanTempDirectory();

		if (!MapJobStatus.isError(job.getStatus()))
			setJobStatus(job, MapJobStatus.Ok);
	}

	private Map<Integer, List<String>> parseConfigFileFTP(String mapPath, Ni3FTPHelper ftp, String[] scales)
	        throws ACException{

		boolean exists = ftp.fileExists("/" + GIS_SERVER_RASTERS_FILE);
		if (!exists)
			throw new ACException(TextID.MsgFileNotFound, new String[] { GIS_SERVER_RASTERS_FILE });

		String localGISServerRastersFile = localTempDirectory + GIS_SERVER_RASTERS_FILE;
		ftp.getAndStoreFile(localGISServerRastersFile, GIS_SERVER_RASTERS_FILE);

		Scanner scanner = null;
		Map<Integer, List<String>> fileMap = new HashMap<Integer, List<String>>();
		try{
			scanner = new Scanner(new File(localGISServerRastersFile), "UTF-8");

			while (scanner.hasNextLine()){
				processLine(scanner.nextLine(), fileMap, localTempDirectory, true);
			}
		} catch (IOException e){
			log.error("File not found: " + mapPath);
			throw new ACException(TextID.MsgFileNotFound, new String[] { localGISServerRastersFile });
		} finally{
			if(scanner != null)
				scanner.close();
		}
		Set<Integer> keys = fileMap.keySet();
		for (Integer scale : keys)
		{
			if (hasScale(scale, scales))
			{
				List<String> localMapFiles = fileMap.get(scale);
				copyFilesFromFTP(scale, ftp, localMapFiles);
			}
		}

		return fileMap;
	}

	boolean hasScale(Integer scale, String[] scales){
		boolean result = false;
		for (String sc : scales){
			if (scale != null && scale.toString().equals(sc)){
				result = true;
				break;
			}
		}
		return result;
	}

	private void copyFilesFromFTP(Integer scale, Ni3FTPHelper ftp, List<String> localMapFiles){
		for (String mapFile : localMapFiles){
			String remotePrefix = mapFile.replaceAll("./temp", "");
			String remoteFileName = remotePrefix + "-" + scale + ".idx";
			String localFileName = mapFile + "-" + scale + ".idx";
			boolean retrieveOk = ftp.getAndStoreFile(localFileName, remoteFileName);

			for (int counter = 1; retrieveOk; counter++){
				localFileName = mapFile + "-" + scale + "-" + counter + ".dat";
				remoteFileName = remotePrefix + "-" + scale + "-" + counter + ".dat";
				retrieveOk = ftp.getAndStoreFile(localFileName, remoteFileName);
				if(!retrieveOk)
				{
					log.error("Error getting file from FTP `" + remoteFileName + "` -> `" + "`" + localFileName + "`");
					continue;
				}

				localFileName = mapFile + "-" + counter + ".idx";
				remoteFileName = remotePrefix + "-" + counter + ".idx";
				retrieveOk = ftp.getAndStoreFile(localFileName, remoteFileName);
			}
		}
	}

	private Map<Integer, List<String>> parseConfigFile(String mapPath) throws ACException{
		File file = new java.io.File(mapPath);
		if (!file.exists()){
			log.error("File not found: " + mapPath);
			throw new ACException(TextID.MsgFileNotFound, new String[] { mapPath });
		} else if (file.isDirectory()){
			if (!mapPath.endsWith(File.separator))
				mapPath += File.separator;
			mapPath += GIS_SERVER_RASTERS_FILE;
			file = new File(mapPath);
			if (!file.exists()){
				log.error("File not found: " + mapPath);
				throw new ACException(TextID.MsgFileNotFound, new String[] { mapPath });
			}
		}
		String baseDir = file.getParentFile().getAbsolutePath();

		try{
			Map<Integer, List<String>> fileMap = new HashMap<Integer, List<String>>();
			Scanner scanner = new Scanner(file, "UTF-8");
			while (scanner.hasNextLine()){
				processLine(scanner.nextLine(), fileMap, baseDir, false);
			}
			return fileMap;
		} catch (FileNotFoundException e){
			log.error("File not found: " + mapPath);
			throw new ACException(TextID.MsgFileNotFound, new String[] { mapPath });
		}
	}

	void processLine(String line, Map<Integer, List<String>> fileMap, String baseDir){
		processLine(line, fileMap, baseDir, false);
	}

	void processLine(String line, Map<Integer, List<String>> fileMap, String baseDir, boolean isFTP){
		if (line == null || line.isEmpty()){
			return;
		}
		String[] tokens = line.split("\t");
		String fileName = tokens[tokens.length - 1];
		if (fileName == null || fileName.isEmpty()){
			return;
		}
		fileName = fileName.trim();
		int index = fileName.length() - 1;
		while (Character.isDigit(fileName.charAt(index))){
			index--;
		}

		if (index < fileName.length() - 1){
			String prefix = fileName.substring(0, index);
			if (prefix.startsWith(".")){
				prefix = prefix.substring(1);
			}
			if (prefix.startsWith(File.separator)){
				prefix = prefix.substring(1);
			}
			Integer scale = Integer.parseInt(fileName.substring(index + 1));
			List<String> files;
			if (fileMap.containsKey(scale)){
				files = fileMap.get(scale);
			} else{
				files = new ArrayList<String>();
				fileMap.put(scale, files);
			}
			if (isFTP)
				files.add(baseDir + prefix);
			else
				files.add(baseDir + File.separator + prefix);
		}
	}

	private String getMapPath(){
		String dbid = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		ACRoutingDataSource acds = (ACRoutingDataSource) dataSource;
		Map<String, InstanceDescriptor> instanceMap = acds.getDatasourceDescriptors();
		InstanceDescriptor id = instanceMap.get(dbid);
		return id.getMapPath();
	}

	private void cleanTempDirectory(){
		if (log.isDebugEnabled())
			log.debug("Deleting temporary files");
		File tempDirectory = new File(localTempDirectory);
		deleteDirectory(tempDirectory);
	}

	private void deleteDirectory(String directory){
		File fDir = new File(directory);
		if (fDir.exists())
			deleteDirectory(fDir);
	}

	private boolean deleteDirectory(File path){
		if (path.exists()){
			File[] files = path.listFiles();
			for (File file : files)
			{
				if (file.isDirectory())
				{
					deleteDirectory(file);
				} else
				{
					if (!file.delete())
						log.warn("Cannot delete temporary file: " + file.getName());
				}
			}
		}
		return (path.delete());
	}

	public void setJobStatus(MapJob job, MapJobStatus status){
		statusUpdater.setJobStatus(job, status);
	}

	private void makeZIPDump(String tempDirectory, String zipName, String dirName) throws IOException{
		new MapPacker().packResultMaps(tempDirectory, zipName, dirName);
	}

	private void moveZIPToModulePath(String tempDirectory, String fileName) throws ACException, IOException{
		final InstanceDescriptor desc = ((ACRoutingDataSource) dataSource).getCurrentInstanceDescriptor();
		final String modulesPath = desc.getModulePath();
		log.debug("module path " + modulesPath);
		if (modulesPath == null || modulesPath.isEmpty())
			throw new ACException(TextID.MsgCantMoveZIPToModulesPath);
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(tempDirectory + fileName);
			transferUtils.uploadFile(modulesPath, fis, fileName);
		} finally{
			try{
				if (fis != null)
					fis.close();
			} catch (IOException e){
				log.error("Error closing FileInputStream", e);
			}
		}

	}

	public void init(){
		String mapPath = getMapPath();
		boolean isFTP = mapPath.toLowerCase().startsWith("ftp://");
		if (isFTP){
			ftpHelper = new Ni3FTPHelper(mapPath);
			try{
				ftpHelper.connect();
			} catch (IOException e1){
				log.error("FTP server refused connection");
				ftpHelper = null;
			}
		}
	}

	public void dispose(){
		if (ftpHelper != null){
			ftpHelper.disconnect();
			ftpHelper = null;
		}
	}
}