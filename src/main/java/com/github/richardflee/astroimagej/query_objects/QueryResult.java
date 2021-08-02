package com.github.richardflee.astroimagej.query_objects;



import java.util.ArrayList;

/**
 * Encapsulates the results of a coordinate-based query on the on-line database
 * in an list of type FieldObject
 */
import java.util.List;

public class QueryResult {

	// object data
	//private CatalogQuery query = null;
	private List<FieldObject> fieldObjects = new ArrayList<>();

	
	/**
	 * Copy with CatalogQuery copy constructor
	 * @param query
	 */
	public QueryResult(CatalogQuery query) {
	//	this.query = new CatalogQuery(query);
	}
	
//	public FieldObject getTargetObject() {		
//		FieldObject fo = new FieldObject();
//		fo.setObjectId(query.getObjectId());
//		fo.setRaHr(query.getRaHr());
//		fo.setDecDeg(fo.getDecDeg());
//		return fo;
//	}
	

	public List<FieldObject> getFieldObjects() {
		return fieldObjects;
	}

	public void setFieldObject(FieldObject fieldObject) {
		fieldObjects.add(fieldObject);
	}	
	

	@Override
	public String toString() {
		return "QueryResult [fieldObjects=" + fieldObjects + "]";
	}
	
	public static void main(String[] args) {
		
		CatalogQuery query = new CatalogQuery();		
		QueryResult result = new QueryResult(query);
		
//		FieldObject target = result.getTargetObject();
//		System.out.println(String.format("Default Target object: %s", target.toString()));
		
		
	}
}

