package com.github.richardflee.astroimagej.catalogs;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.utils.CatalogUrls;

/**
 * Queries the AAVSO Variable Star Plotter (VSP) database for field star based
 * on photometry data user-specified parameters. <p> The search region is
 * centred on RA and DEC coordinates and covers a square fov. </p> <p> The query
 * response returns field star records with photometry data for the specified
 * magnitude band (B, V, Rc or Ic).
 * 
 * </p> <p> Example url:
 * https://app.aavso.org/vsp/api/chart/?format=json&fov=30.0&maglimit=14.5&ra=97.63665&dec=29.67230;
 * Refer getUrl() method for details</p> 
 * 
 * <p> Note that currently VSP API is not
 * documented on-line (2021-05)</p> <p> Json format response,
 * [root]/[photometry]/[fieldstar[1] ...fieldstar[n] where each field star
 * comprises coordinate data and an array of wave-band magnitude data.</p>
 */
public class VspCatalog implements AstroCatalog {

	private String statusMessage = null;
	private final String VSP_CONNECTION_ERROR = "ERROR: Error in VSP internet connection";

	private ObjectMapper objectMapper = null;
	
	// create Jackson object mapper to decode json response to vsp query
	public VspCatalog() {
		objectMapper = new ObjectMapper();
	}

	/**
	 * Runs the VSP database query with url compiled from user-input parameters and
	 * decodes json response to extract photometry data. <p> Utilises Jackson api to
	 * 'tree-walk' json node structure. </p>
	 * @param query
	 *     CatalogQuery object encapsulating VSP database query parameters
	 * @return result VSP database QueryResult type response comprising an array of
	 * FieldObjects matching user-input query parameters
	 */
	@Override
	public List<FieldObject> runQuery(CatalogQuery query) {

		List<FieldObject> fieldObjects = new ArrayList<>();

		String magBand = query.getMagBand();

		// reference to json root node
		JsonNode root = null;
		try {
			root = objectMapper.readTree(new URL(CatalogUrls.urlBuilder(query)));

			// photometry node: parent node for field object nodes
			JsonNode foNodes = root.findPath("photometry");
			for (JsonNode foNode : foNodes) {
				// create a new FieldObject, and add object coordinates + auid
				FieldObject fo = new FieldObject();
				fo.setObjectId(foNode.findPath("auid").asText());
				fo.setRaHr(AstroCoords.raHmsToRaHr(foNode.path("ra").asText()));
				fo.setDecDeg(AstroCoords.decDmsToDecDeg(foNode.path("dec").asText()));

				// if node matching magBand is found then add mag & magErr
				// to fieldObject (fo) and append to result list
				JsonNode bNode = importMagData(foNode, magBand);
				if (bNode != null) {
					fo.setMag(bNode.path("mag").asDouble());
					fo.setMagErr(bNode.path("error").asDouble());
					fieldObjects.add(fo);
				}
			}
			String statusMessage = String.format("Downloaded %d VSP records", fieldObjects.size());
			setStatusMessage(statusMessage);
		} catch (IOException e) {
			setStatusMessage(VSP_CONNECTION_ERROR);
		}
		return fieldObjects;
	}
	
	@Override
	public String getStatusMessage() {
		return this.statusMessage;
	}

	/**
	 * Extracts chart uri for current CatalogQuery parameters, orientated N-E = up-left
	 * 
	 * <p>Extranseous text is removed in QueryResult chart uri setter</p>
	 * 
	 * @param query curent CatalogQuery parameters
	 * @return full uri text string extracted from VSP response
	 */
	public String downloadVspChart(CatalogQuery query) {
		String chartUri = null;
		JsonNode root = null;
		query.setCatalogType(CatalogsEnum.VSP);
		try {
			root = objectMapper.readTree(new URL(CatalogUrls.urlBuilder(query)));
			// full uri may include extraneous text
			chartUri = root.findPath("image_uri").asText();
		} catch (IOException e) {
			setStatusMessage(VSP_CONNECTION_ERROR);
		}
		return chartUri;
	}


	/*
	 * Searches current field object for magBand photometry data
	 * @param foNode field object node
	 * @param magBand selected mag band (B, V, Rc, Ic)
	 * @return node JsonNode pointing to selected magnitude data (if found), null
	 * otherwise
	 */
	private JsonNode importMagData(JsonNode foNode, String magBand) {
		// loop through bands nodes, return node matching magBand otherwise null
		JsonNode node = null;
		JsonNode bandNodes = foNode.findPath("bands");
		for (JsonNode bandNode : bandNodes) {
			String currentBand = bandNode.path("band").asText();
			if (currentBand.equalsIgnoreCase(magBand)) {
				node = bandNode;
				break;
			}
		}
		return node;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public static void main(String[] args) {

		CatalogQuery query = new CatalogQuery();
		query.setCatalogType(CatalogsEnum.VSP);

		QueryResult result = new QueryResult(query, null);

		AstroCatalog catalog = CatalogFactory.createCatalog(query.getCatalogType());
		List<FieldObject> fieldObjects = catalog.runQuery(query);
		result.appendFieldObjects(fieldObjects);

		VspCatalog vspChart = new VspCatalog();
		String chartUri = vspChart.downloadVspChart(query);
		
		System.out.println(chartUri);
		result.setChartUri(chartUri);
	}
}
