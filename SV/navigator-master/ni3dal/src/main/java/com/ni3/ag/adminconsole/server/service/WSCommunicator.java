/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public abstract class WSCommunicator{

	private final static Logger log = Logger.getLogger(WSCommunicator.class);

	private List<ErrorEntry> errors;

	protected List<ErrorEntry> getErrors(){
		return errors;
	}

	private ErrorEntry parseError(Element fail){
		ErrorEntry er = new ErrorEntry();
		NodeList nl = fail.getElementsByTagName("failId");
		Element failId = (Element) nl.item(0);
		er.setId(TextID.valueOf(failId.getTextContent()));
		nl = fail.getElementsByTagName("param");
		er.setErrors(parseErrorParams(nl));
		return er;
	}

	private List<String> parseErrorParams(NodeList nl){
		if (nl == null || nl.getLength() == 0)
			return null;

		List<String> strs = new ArrayList<String>();
		for (int j = 0; j < nl.getLength(); j++){
			Element param = (Element) nl.item(j);
			if (param.getAttribute("index").equals(Integer.toString(j))){
				strs.add(param.getTextContent());
			}
		}

		return strs;
	}

	private Document parseResponse(byte[] response){
		Document doc = null;
		try{
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse(new ByteArrayInputStream(response));
			NodeList importFaultNodes = doc.getElementsByTagName("fail");
			NodeList serviceFaultNodes = doc.getElementsByTagName("faultstring");

			for (int i = 0; i < importFaultNodes.getLength(); i++){
				Element fail = (Element) importFaultNodes.item(i);
				errors.add(parseError(fail));
			}

			for (int i = 0; i < serviceFaultNodes.getLength(); i++){
				String fault = serviceFaultNodes.item(i).getTextContent();
				errors.add(new ErrorEntry(TextID.MsgServiceError, new String[] { fault }));
			}
		} catch (ParserConfigurationException e){
			log.error(e);
		} catch (SAXException e){
			log.error(e);
		} catch (IOException e){
			log.error(e);
		}
		return doc;
	}

	protected Document call(String soapBody, String url){
		return call(soapBody, null, url);
	}

	protected Document call(String soapBody, String attachment, String url){
		errors = new ArrayList<ErrorEntry>();
		Document doc = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			// Create the connection
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();

			SOAPConnection conn = scf.createConnection();

			// Create message
			MessageFactory mf = MessageFactory.newInstance();
			SOAPMessage msg = mf.createMessage();
			MimeHeaders headers = msg.getMimeHeaders();
			headers.addHeader("SOAPAction", "");

			// Object for message parts
			SOAPPart sp = msg.getSOAPPart();

			String soapXML = "<?xml version=\"1.0\"?>\r\n";
			soapXML += "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">";
			soapXML += "<soap:Body>";
			soapXML += soapBody;
			soapXML += "</soap:Body>";
			soapXML += "</soap:Envelope>";

			Source prepMsg = new StreamSource(new ByteArrayInputStream(soapXML.getBytes("utf-8")));
			sp.setContent(prepMsg);

			if (attachment != null){
				// create an attachment using DataHandler class
				AttachmentPart att = msg.createAttachmentPart(new DataHandler(attachment, "text/plain"));
				// add attachment to message
				msg.addAttachmentPart(att);
			}

			// Save message
			msg.saveChanges();

			// Send
			SOAPMessage rp = conn.call(msg, url);

			// Create transformer
			Transformer tf = TransformerFactory.newInstance().newTransformer();

			// Get reply content
			Source sc = rp.getSOAPPart().getContent();
			StreamResult result = new StreamResult(baos);
			tf.transform(sc, result);
			// Close connection
			conn.close();

			// parse response
			byte[] importResponse = baos.toByteArray();
			doc = parseResponse(importResponse);
		} catch (IOException e){
			log.error(e);
		} catch (SOAPException e){
			String error = e.getMessage();
			if (error.indexOf("Bad response") > -1 && error.indexOf("404") > -1 && error.indexOf("Not Found") > -1)
				errors.add(new ErrorEntry(TextID.MsgAPIUnavailable, new String[] { url }));
			else
				errors.add(new ErrorEntry(TextID.MsgServiceError, new String[] { e.getMessage() }));
		} catch (TransformerException e){
			log.error(e);
		}
		if (errors.isEmpty())
			return doc;
		return null;
	}
}
