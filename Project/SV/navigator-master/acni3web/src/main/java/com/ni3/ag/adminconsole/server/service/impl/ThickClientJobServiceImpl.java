/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.TransferUtils;
import com.ni3.ag.adminconsole.server.dao.*;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.jobs.data.*;
import com.ni3.ag.adminconsole.server.lifecycle.ExternalDataImporter;
import com.ni3.ag.adminconsole.server.service.TableLocker;
import com.ni3.ag.adminconsole.server.service.util.FileNameValidator;
import com.ni3.ag.adminconsole.shared.jobs.OfflineJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseVersionService;
import com.ni3.ag.adminconsole.shared.service.def.ThickClientJobService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

public class ThickClientJobServiceImpl implements ThickClientJobService{
	private final static Logger log = Logger.getLogger(ThickClientJobServiceImpl.class);

	private static final String[] TABLE_NAMES_TO_LOCK = new String[] { "cis_edges", "cis_nodes", "cis_objects",
	        "cis_favorites", "cis_favorites_folder", "sys_delta_header", "sys_delta_user", };

	private static final long BACKUP_TIMEOUT = 20 * 60 * 1000;
	private OfflineJobDAO offlineJobDAO;
	private ACRoutingDataSource dataSource;
	private ExternalDataImporter dataImporter;
	private DataExtractor dataExtractor;
	private Cleaner dataCleaner;
	private DataValidator dataValidator;
	private DatabaseVersionService databaseVersionService;
	private ModuleDAO moduleDAO;
	private TransferUtils transferUtils;
	private DeltaHeaderDAO deltaHeaderDAO;
	private ObjectDefinitionDAO objectDefinitionDAO;
	private UserDAO userDAO;

	private TableLocker locker;

	private String tmpDBName;
	private String tmpDBHost;
	private int tmpDBPort;
	private String tmpDBUser;
	private String tmpDBPass;
	private String pgDump;
	private String tempDataSource;
	private volatile boolean processCompleted;
	private volatile int processResult;

	private ExtractStorage dataStorage;
	private ExtractStorage userDataStorage;
	private ExtractStorage geoExtractStorage;

	public void setDataStorage(ExtractStorage dataStorage){
		this.dataStorage = dataStorage;
	}

	public void setUserDataStorage(ExtractStorage userDataStorage){
		this.userDataStorage = userDataStorage;
	}

	public void setGeoExtractStorage(ExtractStorage geoExtractStorage){
		this.geoExtractStorage = geoExtractStorage;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public void setLocker(TableLocker locker){
		this.locker = locker;
	}

	public void setTransferUtils(TransferUtils transferUtils){
		this.transferUtils = transferUtils;
	}

	public void setModuleDAO(ModuleDAO moduleDAO){
		this.moduleDAO = moduleDAO;
	}

	public void setDatabaseVersionService(DatabaseVersionService databaseVersionService){
		this.databaseVersionService = databaseVersionService;
	}

	public void setTmpDBName(String tmpDBName){
		this.tmpDBName = tmpDBName;
	}

	public void setTmpDBHost(String tmpDBHost){
		this.tmpDBHost = tmpDBHost;
	}

	public void setTmpDBPort(int tmpDBPort){
		this.tmpDBPort = tmpDBPort;
	}

	public void setTmpDBUser(String tmpDBUser){
		this.tmpDBUser = tmpDBUser;
	}

	public void setTmpDBPass(String tmpDBPass){
		this.tmpDBPass = tmpDBPass;
	}

	public void setPgDump(String pgDump){
		this.pgDump = pgDump;
	}

	public void setTempDataSource(String tempDataSource){
		this.tempDataSource = tempDataSource;
	}

	public void setOfflineJobDAO(OfflineJobDAO offlineJobDAO){
		this.offlineJobDAO = offlineJobDAO;
	}

	public void setDataSource(ACRoutingDataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setDataImporter(ExternalDataImporter dataImporter){
		this.dataImporter = dataImporter;
	}

	public void setDataExtractor(DataExtractor dataExtractor){
		this.dataExtractor = dataExtractor;
	}

	public void setDataCleaner(Cleaner dataCleaner){
		this.dataCleaner = dataCleaner;
	}

	public void setDataValidator(DataValidator dataValidator){
		this.dataValidator = dataValidator;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public void setDeltaHeaderDAO(DeltaHeaderDAO deltaHeaderDAO){
		this.deltaHeaderDAO = deltaHeaderDAO;
	}

	@Override
	public List<OfflineJob> getOfflineJobs(){
		List<OfflineJob> allJobs = offlineJobDAO.getAllJobs();
		Collections.sort(allJobs);
		return allJobs;
	}

	@Override
	public List<User> getUsers(){
		return offlineJobDAO.getThickClientUsers();
	}

	@Override
	public List<Group> getGroupsWithOfflineUsers(){
		List<Group> groups = offlineJobDAO.getGroupsWithOfflineUsers();
		for (Group group : groups){
			for (User user : group.getUsers())
				Hibernate.initialize(user.getGroups());
		}
		return groups;
	}

	@Override
	public void applyOfflineJobs(List<OfflineJob> jobsToUpdate, List<OfflineJob> jobsToDelete) throws ACException{
		if (jobsToUpdate != null && !jobsToUpdate.isEmpty()){
			for (OfflineJob job : jobsToUpdate){
				if (job.getId() != null){
					OfflineJob current = offlineJobDAO.getOfflineJob(job.getId());
					if (current.getId() != null && !OfflineJobStatus.Scheduled.getValue().equals(current.getStatus())){

						if (OfflineJobStatus.Scheduled.getValue().equals(job.getStatus()))
							throw new ACException(TextID.MsgPleaseRefresh);
						else
							continue;
					}
				}
				offlineJobDAO.merge(job);
			}
		}

		if (jobsToDelete != null && !jobsToDelete.isEmpty()){
			for (OfflineJob job : jobsToDelete){
				job = offlineJobDAO.getOfflineJob(job.getId()); // reload job, cause it could be already changed
				offlineJobDAO.delete(job);
			}
		}
	}

	@Override
	public void processJobs() throws ACException{
		List<OfflineJob> jobs = offlineJobDAO.getScheduledExportJobs();
		if (jobs.isEmpty()){
			return;
		}
		if (!lockTables())
			return;
		log.debug("Offline client data extraction jobs found: " + jobs.size());
		for (OfflineJob job : jobs){
			processJob(job, true);
		}
	}

	private boolean lockTables(){
		return locker.lockTables(getTableListToLock());
	}

	private String[] getTableListToLock(){
		List<String> result = new ArrayList<String>();
		List<ObjectDefinition> objects = objectDefinitionDAO.getObjectDefinitions();
		for (ObjectDefinition od : objects)
			result.add(od.getTableName());
		Collections.addAll(result, TABLE_NAMES_TO_LOCK);
		return result.toArray(new String[result.size()]);
	}

	private boolean checkSysDeltaHeader(){
		log.info("Checking sys_delta_header table for unprocessed deltas for dbid: "
		        + ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId());
		Integer count = deltaHeaderDAO.getUnprocessedCount();
		log.debug("Unprocessed delta count: " + count);
		if (count == 0){
			log.debug("No unprocessed deltas in sys_delta_header - we can proceed extraction");
			return true;
		}
		log
		        .warn("Table sys_delta_header contains unprocessed records. Extraction cannot proceed - dump can make user data inconsistent");
		return false;
	}

	@Override
	public void processJob(OfflineJob job, boolean schedulerCall) throws ACException{
		if (!schedulerCall && !lockTables()){
			log.error("Cannot launch data extraction, other extraction is in progress");
			throw new ACException(TextID.MsgCannotExtractDataOtherExtraction);
		}
		OfflineJobStatus status = OfflineJobStatus.ErrorUnknownError;
		Connection c = null;
		try{
			c = dataSource.getConnection();
			c.setAutoCommit(true);
			if (!checkSysDeltaHeader()){
				status = OfflineJobStatus.ScheduledWaiting;
				if (schedulerCall)
					return;
				else
					throw new ACException(TextID.MsgCannotExtractDataDirtyDelta);
			}

			Group group = getGroup(job);
			List<User> users = getUsers(job);
			if (group == null || users.isEmpty()){
				log.error("Cannot find users for string: " + job.getUserIds());
				return;
			}

			setJobStatus(job, OfflineJobStatus.InProgress1, c);

			ThreadLocalStorage dsStorage = ThreadLocalStorage.getInstance();
			String masterDataSource = dsStorage.getCurrentDatabaseInstanceId();
			String modulesPath = getModulePathForInstance();
			log.debug("validating database");
			DiagnoseTaskResult result = dataValidator.validate();
			if (!DiagnoseTaskStatus.Ok.equals(result.getStatus())){
				log.error("Database integrity validation error. Datasource: " + masterDataSource + ", error: "
				        + result.getDescription());
				status = OfflineJobStatus.ErrorDatabaseIntegrity;
				throw new ACException(TextID.MsgDatabaseIntegrityError);
			}

			if (tempDataSource == null || tempDataSource.isEmpty()){
				log.error("user datasource not found or == null");
				status = OfflineJobStatus.ErrorNoTempDataSource;
				throw new ACException(TextID.MsgTempDatasourceNotConfigured);
			}
			log.debug("User datasource name: " + tempDataSource);
			String dbVersion = databaseVersionService.getActualVersion();
			dataSource.addDataSource(tempDataSource);
			InstanceDescriptor id = dataSource.getDatasourceDescriptors().get(tempDataSource);
			dsStorage.setCurrentDatabaseInstanceId(tempDataSource);
			boolean generate = dataSource.getGenerate(id);
			if (!generate && !dataSource.isDatasourceVersionMatchCurrent(id)){
				log.error("Database version of destination datasource does not match");
				status = OfflineJobStatus.ErrorDatabaseVersionIsInvalid;
				String version = dataSource.getSysIamVersion(id);
				throw new ACException(TextID.MsgDatabaseWrongVersion, new String[] { dataSource.getDatabaseVersion(),
				        version == null ? "--" : version });
			}

			dsStorage.setCurrentDatabaseInstanceId(masterDataSource);

			setJobStatus(job, OfflineJobStatus.InProgress2, c);

			log.debug("getting all data");

			dataStorage.clean();
			userDataStorage.clean();
			geoExtractStorage.clean();
			dataExtractor.getAllData(dataSource, dataStorage);
			dataExtractor.getGeoData(dataSource, geoExtractStorage);

			setJobStatus(job, OfflineJobStatus.InProgress3, c);

			Boolean b = job.getWithFirstDegreeObjects();
			b = b != null ? b : Boolean.FALSE;
			log.debug("getting all user data (with +1 = " + b + ")");
			dataExtractor.getUserAllData(dataSource, userDataStorage, group, b);

			dataExtractor.fillMaxUserDeltas(dataSource, job.getUserIds());

			setJobStatus(job, OfflineJobStatus.InProgress4, c);

			dsStorage.setCurrentDatabaseInstanceId(tempDataSource);
			log.debug("GENERATE: " + generate);
			if (generate)
				dataImporter.importExternalData(id.getDatabaseType());

			log.debug("call data clean");
			dataCleaner.cleanData();

			setJobStatus(job, OfflineJobStatus.InProgress5, c);

			log.debug("storing all data");
			dataExtractor.storeAllData(dataSource, dataStorage);
			dataStorage.clean();

			dataExtractor.storeAllGeoData(dataSource, geoExtractStorage);
			geoExtractStorage.clean();

			setJobStatus(job, OfflineJobStatus.InProgress6, c);

			log.debug("storing all user data");
			dataExtractor.storeUserAllData(dataSource, userDataStorage);
			userDataStorage.clean();
			setJobStatus(job, OfflineJobStatus.InProgress8, c);

			status = makeDatabaseDumps(users, dbVersion, masterDataSource, modulesPath);
		} catch (ACException e){
			throw e;
		} catch (Throwable e){
			log.error("unknown error", e);
			status = OfflineJobStatus.ErrorUnknownError;
			throw new ACException(TextID.MsgErrorExtractingThickClient, new String[] { e.getMessage() });
		} finally{
			setJobStatus(job, status, c);
			try{
				c.close();
			} catch (SQLException e){
				log.error("Cannot close connection", e);
			}
			log.debug("Finished, status = " + status.getLabel());
		}
	}

	private OfflineJobStatus makeDatabaseDumps(List<User> users, String dbVersion, String masterDataSource,
	        String modulesPath) throws Exception{
		OfflineJobStatus status = OfflineJobStatus.ErrorUnknownError;
		List<Module> modules = new ArrayList<Module>();

		for (User user : users){
			log.debug("Preparing database for user " + user.getUserName());
			dataExtractor.redirectSequences(user);
			dataExtractor.disableUsers(dataSource, user);

			log.debug("Creating database dump for user " + user.getUserName());
			status = makeTempDBDump(user, dbVersion, modules, modulesPath);
			if (!OfflineJobStatus.Ok.equals(status))
				throw new ACException(TextID.MsgErrorExtractingThickClient, new String[] { "Failed make DB dump" });
		}

		log.debug("Saving modules to master database");
		ThreadLocalStorage.getInstance().setCurrentDatabaseInstanceId(masterDataSource);
		moduleDAO.saveOrUpdateAll(modules);
		return status;
	}

	private List<User> getUsers(OfflineJob job){
		if (job.getUserIds() == null || job.getUserIds().isEmpty())
			return null;
		String[] idsStr = job.getUserIds().split(",");
		Integer[] ids = new Integer[idsStr.length];
		for (int i = 0; i < idsStr.length; i++){
			ids[i] = Integer.parseInt(idsStr[i]);
		}
		List<User> users = userDAO.getUsersByIds(ids);
		log.debug("found " + users.size() + " users for ids = " + job.getUserIds());
		return users;
	}

	private Group getGroup(OfflineJob job){
		if (job.getUserIds() == null || job.getUserIds().isEmpty())
			return null;
		String[] ids = job.getUserIds().split(",");
		User user = userDAO.getById(Integer.parseInt(ids[0]));
		return user.getGroups().get(0);
	}

	private OfflineJobStatus makeTempDBDump(User user, String dbVersion, List<Module> modules, String modulesPath)
	        throws IOException, InterruptedException, ACException{
		log.info("starting database dump");
		Integer maxUserDelta = dataExtractor.getMaxUserDelta(user);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String backupFile = user.getUserName() + "-" + dbVersion + "-" + sdf.format(now) + ".backup";
		backupFile = FileNameValidator.getNameWOSpecialChars(backupFile);
		String cmd = pgDump + " -F c --blobs -f" + backupFile + " --host=" + tmpDBHost + " --port=" + tmpDBPort + " -U" + tmpDBUser
		        + " --clean -v -w " + tmpDBName;
		log.debug("CWD: " + new File(".").getCanonicalPath());
		log.debug("cmd: " + cmd);
		String passFile = makePassFile();
		if (passFile == null)
			return OfflineJobStatus.ErrorCreateModule;

		final Process dumpProcess = Runtime.getRuntime().exec(cmd);
		log.debug("process started");
		log.debug("waiting till the end of process");
		long endTime = System.currentTimeMillis() + BACKUP_TIMEOUT;
		BufferedReader stdin = new BufferedReader(new InputStreamReader(dumpProcess.getInputStream()));
		BufferedReader stderr = new BufferedReader(new InputStreamReader(dumpProcess.getErrorStream()));

		processCompleted = false;
		processResult = -1;
		new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					processResult = dumpProcess.waitFor();
					log.debug("process completed");
				} catch (InterruptedException e){
					log.error("process interrupted: ", e);
					processResult = -1;
				}
				processCompleted = true;
			}
		}).start();
		while (System.currentTimeMillis() < endTime && !processCompleted){
			if (stdin.ready())
				log.debug("STDOUT: " + stdin.readLine());
			if (stderr.ready())
				log.debug("STDERR: " + stderr.readLine());
		}
		while (stdin.ready() || stderr.ready()){
			if (stdin.ready())
				log.debug("STDOUT: " + stdin.readLine());
			if (stderr.ready())
				log.debug("STDERR: " + stderr.readLine());
		}
		log.debug("Process completed: " + processCompleted);
		log.debug("Process result: " + processResult);
		if (!processCompleted){
			log.debug("Killing process");
			dumpProcess.destroy();
		}
		if (processResult != 0)
			return OfflineJobStatus.ErrorDumpDatabase;
		log.debug("dump process - success");
		log.debug("creating dump file zip");
		OfflineJobStatus stat = makeDumpZIP(backupFile);
		boolean delResult = new File(backupFile).delete();
		if(!delResult)
			log.debug("File " + backupFile + " does not exist");
		backupFile += ".zip";
		log.debug("dump zip result " + stat.name());
		if (!OfflineJobStatus.Ok.equals(stat))
			return OfflineJobStatus.ErrorCreateModule;
		log.debug("moving result to modules path");
		String newPath = moveZIPToModulePath(backupFile, modulesPath);
		File backupFileHandle = new File(backupFile);
		if (backupFileHandle.exists())
			if(!backupFileHandle.delete())
				log.debug("File " + backupFile + " does not exist");
		log.debug("new zip path: " + newPath);
		log.debug("moving result " + stat.name());
		if (newPath == null)
			return OfflineJobStatus.ErrorCreateModule;
		log.debug("make module");
		Module module = makeModule(newPath, dbVersion, maxUserDelta, user);
		modules.add(module);
		if (!OfflineJobStatus.Ok.equals(stat))
			return OfflineJobStatus.ErrorCreateModule;
		return OfflineJobStatus.Ok;
	}

	private String makePassFile() throws IOException{
		String path = System.getProperty("user.home");
		log.debug("home: " + path);
		String os = System.getProperty("os.name");
		log.debug("OS: " + os);
		if (path == null || os == null)
			return null;
		if (path.isEmpty() || os.isEmpty())
			return null;
		if (File.separatorChar != path.charAt(path.length() - 1))
			path += File.separator;
		if (os.toLowerCase().contains("win")){
			path = System.getenv("APPDATA");
			if (File.separatorChar != path.charAt(path.length() - 1))
				path += File.separator;
			path += "postgresql\\pgpass.conf";
		} else if (os.toLowerCase().contains("lin"))
			path += ".pgpass";
		else
			return null;
		log.debug("pgpass path: " + path);
		File f = new File(path);
		if (!f.exists())
			if (!f.createNewFile())
				return null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		String line;
		while (null != (line = reader.readLine())){
			String[] ss = line.split(":");
			if (tmpDBHost.equals(ss[0]) && Integer.toString(tmpDBPort).equals(ss[1]) && "*".equals(ss[2])
			        && tmpDBUser.equals(ss[3]) && tmpDBPass.equals(ss[4]))
				return path;
		}
		reader.close();
		StringBuilder sb = new StringBuilder();
		sb.append(tmpDBHost).append(':').append(tmpDBPort).append(":*:").append(tmpDBUser).append(':').append(tmpDBPass);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true)));
		writer.write(sb.toString());
		writer.newLine();
		writer.close();
		return f.getCanonicalPath();
	}

	private Module makeModule(String backupFile, String dbVersion, Integer maxUserDelta, User user){
		Module m = new Module();
		m.setName(Module.DB_DUMP);
		m.setPath(backupFile);
		m.setVersion(dbVersion + "-" + user.getUserName());
		StringBuilder sb = new StringBuilder();
		sb.append(Module.DB_DUMP_PARAM_MAX_USER_DELTA).append("=").append(maxUserDelta != null ? maxUserDelta : 0).append(
		        "\n");
		sb.append(Module.DB_DUMP_PARAM_USER_ID).append("=").append(user.getId()).append("\n");
		m.setParams(sb.toString());
		return m;
	}

	private String moveZIPToModulePath(String backupFile, String modulesPath) throws ACException{
		log.debug("module path " + modulesPath);
		if (modulesPath == null)
			return null;
		if (modulesPath.isEmpty())
			return null;
		transferUtils.uploadFile(modulesPath, backupFile);

		return backupFile;
	}

	private String getModulePathForInstance(){
		InstanceDescriptor desc = dataSource.getCurrentInstanceDescriptor();
		return desc.getModulePath();
	}

	private OfflineJobStatus makeDumpZIP(String backupFile) throws IOException{
		FileInputStream infs = new FileInputStream(backupFile);
		BufferedInputStream bis = new BufferedInputStream(infs);
		FileOutputStream outfs = new FileOutputStream(backupFile + ".zip");
		ZipOutputStream zOut = new ZipOutputStream(outfs);
		BufferedOutputStream bos = new BufferedOutputStream(zOut);

		zOut.putNextEntry(new ZipEntry(backupFile));
		byte[] buf = new byte[5 * 1024];
		while (bis.available() > 0){
			int count = bis.read(buf);
			bos.write(buf, 0, count);
		}
		bos.flush();
		zOut.closeEntry();

		infs.close();
		bis.close();
		bos.close();
		zOut.close();
		outfs.close();
		return OfflineJobStatus.Ok;
	}

	public void setJobStatus(OfflineJob job, OfflineJobStatus status, Connection c){
		Statement st = null;
		try{
			String sql = "update sys_offline_job set status = " + status.getValue();
			if (OfflineJobStatus.InProgress1.equals(status)){
				sql += ", timestart = now()";
			} else if (!OfflineJobStatus.ScheduledWaiting.equals(status)){
				sql += ", timeend = now()";
			}
			sql += " where id = " + job.getId();
			log.debug("Execute: " + sql);
			st = c.createStatement();
			st.executeUpdate(sql);
		} catch (Throwable th){
			log.error("Error set job status", th);
		} finally{
			if (st != null)
				try{
					st.close();
				} catch (SQLException e){
					log.error("Error closing statement", e);
				}
		}
	}

}
