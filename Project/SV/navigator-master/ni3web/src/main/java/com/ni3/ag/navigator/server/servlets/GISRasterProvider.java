package com.ni3.ag.navigator.server.servlets;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.GISUtil.GISRasterLayer;
import com.ni3.ag.navigator.server.util.ServerSettings;

//TODO : Is this even used?
@SuppressWarnings("serial")
public class GISRasterProvider extends Ni3Servlet{
	private static final Logger log = Logger.getLogger(GISRasterProvider.class);

	private static final String PROPERTY_FILE_NAME = "/Ni3Web.properties";

	public GISRasterProvider(){
		super();
		log.info("Ni3 Application server - GIS Raster server V3.3 build 0001");

		Properties prop = loadPropertyFile();

		String path = prop.getProperty("GISRasterPath");

		if (path == null || path.length() == 0){
			log.error("Parameter GISRasterPath not set");
		} else
			init(path);
	}

	public void destroy(){
		super.destroy();
	}

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
	}

	public ArrayList<GISRasterLayer> layers;
	int maxResultSize;
	char ResultBuffer;

	long x1, y1, x2, y2;
	ByteBuffer buffer;

	private void init(String controlFilePath){
		layers = new ArrayList<GISRasterLayer>();

		String mapID, version, layerPath;
		GISRasterLayer layer;
		int fileID = 0;

		x1 = Integer.MAX_VALUE;
		x2 = Integer.MIN_VALUE;
		y1 = Integer.MAX_VALUE;
		y2 = Integer.MIN_VALUE;

		try{
			BufferedReader input = new BufferedReader(new FileReader(new File(controlFilePath)));
			try{
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null){
					StringTokenizer tok = new StringTokenizer(line, "\t");
					mapID = tok.nextToken();
					if ("BREAK".equals(mapID))
						break;
					version = tok.nextToken();
					layerPath = tok.nextToken();

					log.info("Layer path: " + layerPath);

					layer = new GISRasterLayer();
					layer.layerID = fileID;

					try{
						layer.load(layerPath, Integer.valueOf(version));
						layer.mapID = Integer.valueOf(mapID);

						x1 = Math.min(layer.X, x1);
						x2 = Math.max(layer.X + layer.W, x2);
						y1 = Math.min(layer.Y, y1);
						y2 = Math.max(layer.Y + layer.H, y2);

						fileID++;

						layers.add(layer);
					} catch (Exception e){
						log.error(e);
					}
				}
				input.close();
			} finally{
				input.close();
			}
		} catch (IOException ex){
			log.error("Can not load configuration file " + controlFilePath);
		}

		maxResultSize = 1024 * 1024;

		buffer = ByteBuffer.allocate(maxResultSize);
	}

	void getArea(Rectangle filter[], long mapID, long zoom, HttpServletResponse response){
		StringBuilder ret = new StringBuilder();

		for (GISRasterLayer layer : layers){
			if (layer.scale == zoom && layer.mapID == mapID){
				ret.append(layer.getTileList(filter));
			}
		}

		if (ret.length() == 0){
			ret.append("-\n");
		}

		try{
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
		} catch (Exception e){
			log.error(e.getMessage(), e);
		}

		PrintWriter out;
		try{
			out = response.getWriter();
			out.println(ret.toString());
		} catch (IOException e){
			log.error(e.getMessage(), e);
		}
	}

	void GetRaster(int rasterID, int tileID, HttpServletResponse response){
		buffer.flip();

		if (rasterID < layers.size()){
			try{
				int len = layers.get(rasterID).getRaster(buffer.array(), tileID);

				response.setContentType("image/png");
				response.setCharacterEncoding("UTF-8");
				response.getOutputStream().write(buffer.array(), 0, len);
				response.flushBuffer();
			} catch (Exception e){
				log.error(e);
			}
		}
	}

	private static Properties loadPropertyFile(){
		Properties prop = new Properties();
		try{
			prop.load(ServerSettings.class.getResourceAsStream(PROPERTY_FILE_NAME));
			log.info("Loading datasource parameters from " + PROPERTY_FILE_NAME);
		} catch (IOException e){
			log.error("IOException, could not load property file " + PROPERTY_FILE_NAME, e);
		}
		return prop;
	}

	@Override
	protected UserActivityType getActivityType(){
		// not used
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		// not used
		return null;
	}
}
