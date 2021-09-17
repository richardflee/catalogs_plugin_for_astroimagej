package com.github.richardflee.astroimagej.catalogs;

import com.github.richardflee.astroimagej.enums.CatalogsEnum;

/**
 * Creates an instance of catalog type selected in the catalog ui dialog.
 * <p>
 * currently only VSP catalog is the only implementation
 * <p/> 
 */
public class CatalogFactory {

	/**
	 * Manages selection of on-line astronomical database
	 * 
	 * @param catalogType type of on-line database to create
	 * @return catalog object of selected database type
	 */
	public static AstroCatalog createCatalog(CatalogsEnum catalogType) {
		AstroCatalog catalog = null;
		if (catalogType == CatalogsEnum.VSP) {
			catalog = new VspCatalog();
		} else {
			catalog = new ApassCatalog();
		}
		return catalog;
	}
}
