package com.github.richardflee.astroimagej.catalog_ui;

import java.util.Vector;

@FunctionalInterface
public interface SimpleListener {
	public void updateTable(Vector<Vector<Object>> vectors);

}
