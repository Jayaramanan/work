/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.server.Ni3FTPHelper;
import com.ni3.ag.adminconsole.server.dao.IconDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.shared.service.def.NodeMetaphorService;
import com.ni3.ag.adminconsole.util.ValidationUtils;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

public class NodeMetaphorServiceImpl implements NodeMetaphorService{

	private final static Logger log = Logger.getLogger(NodeMetaphorServiceImpl.class);

	private ACRoutingDataSource dataSource;
	private ObjectDefinitionDAO objectDefinitionDAO;
	private SchemaDAO schemaDAO;
	private IconDAO iconDAO;
	private ACValidationRule metaphorIconDeleteRule;

	public void setDataSource(ACRoutingDataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setMetaphorIconDeleteRule(ACValidationRule metaphorIconDeleteRule){
		this.metaphorIconDeleteRule = metaphorIconDeleteRule;
	}

	public List<Schema> getSchemasWithObjects(){
		List<Schema> schemasWithObjects = schemaDAO.getSchemasWithNodesAndInMetaphor();
		for (Schema schema : schemasWithObjects){
			initSchema(schema);
		}
		return schemasWithObjects;
	}

	private void initSchema(Schema schema){
		List<ObjectDefinition> objectDefinitions = schema.getObjectDefinitions();
		Hibernate.initialize(objectDefinitions);
		for (ObjectDefinition od : objectDefinitions){
			initObjectDefinition(od);
		}
	}

	private void initObjectDefinition(ObjectDefinition od){
		for (ObjectAttribute oa : od.getObjectAttributes()){
			Hibernate.initialize(oa.getPredefinedAttributes());
		}

		initMetaphors(od);
	}

	private void initMetaphors(ObjectDefinition od){
		for (Metaphor m : od.getMetaphors()){
			Hibernate.initialize(m.getIcon());
			Hibernate.initialize(m.getMetaphorData());
			for(MetaphorData md : m.getMetaphorData()){
				Hibernate.initialize(md);
				Hibernate.initialize(md.getAttribute());
				Hibernate.initialize(md.getData());
			}
		}
	}

	@Override
	public void updateObject(ObjectDefinition object){
		objectDefinitionDAO.saveOrUpdate(object);
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setIconDAO(IconDAO iconDAO){
		this.iconDAO = iconDAO;
	}

	@Override
	public List<Icon> getAllIcons(){
		return iconDAO.loadAll();
	}

	@Override
	public Integer addNewIcon(Icon icon, boolean uploadToDocroot) throws ACException{
		if (!ValidationUtils.isIconNameValid(icon.getIconName())){
			throw new ACException(TextID.MsgProhibitedSymbolsInIconName);
		}

		if (uploadToDocroot){
			uploadToDocroot(icon);
		}

		return iconDAO.save(icon);
	}

	/**
	 * @param icon
	 * @throws ACException
	 */
	private void uploadToDocroot(Icon icon) throws ACException{
		final String docrootPath = dataSource.getCurrentInstanceDescriptor().getDocrootPath();
		if (isFTPProtocol(docrootPath)){
			Ni3FTPHelper ftpHelper = new Ni3FTPHelper(docrootPath);
			try{
				ftpHelper.connect();
			} catch (IOException e1){
				log.error("FTP server refused connection", e1);
				throw new ACException(TextID.MsgFTPServerRefusedConnection, new String[] { docrootPath });
			}
			ftpHelper.uploadFile(icon.getIconName(), icon.getIcon());
			ftpHelper.disconnect();
		} else
			try{
				copyFileToLocalDir(docrootPath, icon);
			} catch (IOException e1){
				log.error("failed to store file on disk", e1);
				throw new ACException(TextID.MsgFailedToCreateOrSaveFile);
			}
	}

	private static boolean isFTPProtocol(String url){
		return url != null && url.toLowerCase().startsWith("ftp://");
	}

	private void copyFileToLocalDir(String path, Icon icon) throws IOException{
		if (path == null)
			return;
		if (!path.endsWith(File.separator))
			path += File.separator;
		File f = new File(path + icon.getIconName());
		if (!f.createNewFile())
			throw new IOException("Error creating target file");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(f);
			fos.write(icon.getIcon());
			fos.flush();
			fos.close();
		} finally{
			if (fos != null)
				fos.close();
		}
	}

	@Override
	public void deleteIcons(List<Icon> iconsToDelete) throws ACException{
		NodeMetaphorModel model = new NodeMetaphorModel();
		model.setIcons(iconsToDelete);

		if (!metaphorIconDeleteRule.performCheck(model)){
			throw new ACException(metaphorIconDeleteRule.getErrorEntries());
		}

		iconDAO.deleteAll(iconsToDelete);
	}

	@Override
	public ObjectDefinition reloadObject(Integer id){
		ObjectDefinition od = objectDefinitionDAO.getObjectDefinitionWithInMetaphor(id);
		initObjectDefinition(od);
		return od;
	}

}
