package com.github.richardflee.astroimagej.catalogs;


import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.richardflee.astroimagej.enums.SimbadEnum;
import com.github.richardflee.astroimagej.exceptions.SimbadNotFoundException;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;
import com.github.richardflee.astroimagej.utils.CatalogUrls;


public class SimbadCatalog {
	private DocumentBuilder builder = null;
	private final String NO_DATA = "*****";
	private String statusMessage = null;

	/**
	 * Configure XPath to parse xml.  Note: Ignores xml namespace.
	 */
	public SimbadCatalog() {
		// Assemble XPath objects ..
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			// ignore xml/VOTable namespaces
			factory.setNamespaceAware(false);
			this.builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			String statusMessage = "ERROR: Error compiling SIMBAD query | ";
			statusMessage += e1.getMessage();
			setStatusMessage(statusMessage);
		}
	}
	
	/**
	 * Runs a sequence of SIMBAD queries to download coordinate and magnitude data
	 * for the user specified target name
	 * <p>
	 * Each parameter is an individual query with a 250 ms buffer delay between
	 * successive queries.
	 * </p>
	 * 
	 * @param query catalog query object containing  user input name index to SIMBAD database
	 * @return result encapsulates SimbadID, coordinates and available magnitudes
	 *         
	 * @throws SimbadNotFoundException throws exception if user input name is not 
	 *  		identified in the Simbad database
	 */
	public SimbadResult runQuery(CatalogQuery query) throws SimbadNotFoundException {
		
		// run Simbad query
		SimbadResult simbadResult = new SimbadResult(query.getObjectId());
		
		// search database for user input object name
		// throws SimbadNotFoundException if no match found
		String data = downloadSimbadItem(query, SimbadEnum.USER_TARGET_NAME);
		simbadResult.setSimbadId(data);
		
		// no checks on coordinate data, assumed good
		// object J2000 RA converted deg -> hrs
		data = downloadSimbadItem(query, SimbadEnum.RA_HR);
		simbadResult.setSimbadRaHr(Double.parseDouble(data) / 15.0);
		
		// object J2000 Dec in deg
		data = downloadSimbadItem(query, SimbadEnum.DEC_DEG);
		simbadResult.setSimbadDecDeg(Double.parseDouble(data));
		
		// object magnitude for filters B, V, Rc and Ic.
		// return null if no magnitude data for this filter
		data = downloadSimbadItem(query, SimbadEnum.MAG_B);
		Double num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadResult.setMagB(num);
		
		data = downloadSimbadItem(query, SimbadEnum.MAG_V);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadResult.setMagV(num);
		
		data = downloadSimbadItem(query, SimbadEnum.MAG_R);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadResult.setMagR(num);
		
		data = downloadSimbadItem(query, SimbadEnum.MAG_I);
		num = (data.equals(NO_DATA)) ? null : Double.parseDouble(data);
		simbadResult.setMagI(num);
		
		String statusMessage = String.format("Simbad query successful for ObjectID: %s",  query.getObjectId());
		setStatusMessage(statusMessage);
		
		return simbadResult;
	}

	/*
	 * Queries the SIMBAD database for single SimbadDataType data item. Applies 250
	 * ms delay after query returns to buffer successive queries
	 * 
	 * <p>Refer reference above for details Xpath xml parser</p>
	 * 
	 * @param dataType query data type, SimbadId, coordinates or filter magnitudes	 
	 * @return text data value 
	 * @throws SimbadNotFoundException thrown if specified object name is not found
	 * in SIMBAD database
	 */
	private String downloadSimbadItem(CatalogQuery query, SimbadEnum paramType) 
			throws SimbadNotFoundException {
		NodeList nodes = null;
		String result = null;
		
		// compile SIMBAD url for current SinbadDataType
		String url = CatalogUrls.urlBuilder(query, paramType);
		
		// Create Xpath and run xml query for dataType-specified item
		try {
			Document doc = builder.parse(url);
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//TD/text()");
			nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			// buffer successive queries
			Thread.sleep(250);
		} catch (SAXException | IOException | XPathExpressionException | InterruptedException e1) {
			String statusMessage = "ERROR: Error running SIMBAD query | ";
			statusMessage += e1.getMessage();
			setStatusMessage(statusMessage);
		}
		
		// node item 0 is SimbadId name. 
		// Query objectId: throw SimbadNotFoundException if input does not match SIMBAD records
		if (paramType == SimbadEnum.USER_TARGET_NAME) {
			try {
				result = nodes.item(0).getNodeValue();
			} catch (NullPointerException npe) {
				String statusMessage =  
						String.format("No match found for ObjectID %s in the SIMBAD database", query.getObjectId());
				setStatusMessage(statusMessage);
				throw new SimbadNotFoundException(null);
			}
			// other data is node item 1. Null check manages missing magnitude data  
		} else {
			result = (nodes.getLength() == 1) ? NO_DATA : nodes.item(1).getNodeValue();
		}
		return result;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}	
	
	
}
