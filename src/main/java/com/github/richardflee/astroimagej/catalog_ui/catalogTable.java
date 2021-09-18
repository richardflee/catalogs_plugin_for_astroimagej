package com.github.richardflee.astroimagej.catalog_ui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.github.richardflee.astroimagej.enums.ColumnsEnum;

/**
 * This class comprises a Java Swing JTable with CatalogTableModel data model. 
 * 
 * <p> The first row consists of target object data; subsequent rows comprise a series of 
 * reference  object data. Selected reference rows can be copied to an radec file for 
 * importing apertures into aij.</p>
 * 
 * <p>The first column shows the projected astroimagej aperture number, T01 (target) then C02, C03 .. for selected
 * reference rows. If the user deselects  a reference row, the aperture number automatically clears and higher
 * aperture numbers adjust to maintain numerical sequence, e.g. T01, C02, [blank], C03 ...where [blank] denotes
 *  a de-selected row</p>
 */
public class CatalogTable {

	private JTable table;
	
	// table format constants
	private final int ROW_HT_INCREMENT = 6;
	private final int PREFERRED_WIDTH = 800;
	private int HEADER_HT = 24;

	/**
	 * Creates new catalog table, applying table and header formatting. 
	 * 
	 * @param catalogUi reference to main catalog UI
	 * @param catalogTableModel 
	 */
	public CatalogTable(CatalogUI catalogUi, CatalogTableModel catalogTableModel) {
		
		// connects JTable to data model
		table = new JTable(catalogTableModel);
		
		// configure header tooltips and formatting
		ToolTipHeader header = 
				new ToolTipHeader(table.getColumnModel(), CatalogTableModel.toolTips);
		table.setTableHeader(header);
		header.setDefaultRenderer(new HeaderRenderer());
		
		// configure table cell formatting
		tableCellsFormatter(table, PREFERRED_WIDTH);
		table.getColumnModel().getColumn(0).setCellRenderer(new ApColumnRenderer());
		
		// embeds table in catalog UI scroll pane
		JScrollPane spane = catalogUi.tableScrollPane;
		table.setFillsViewportHeight(true);
		spane.setViewportView(table);
		
		// finally set header size (ignores width = 0)
		spane.getColumnHeader().setPreferredSize(new Dimension(0, HEADER_HT));
	}
	
	/*
	 * Implementation code to set a tooltip text to each column of JTableHeader
	 *  ref: https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#headertooltip
	 */
	class ToolTipHeader extends JTableHeader {
		
		private static final long serialVersionUID = 1L;
		String[] toolTips;

		public ToolTipHeader(TableColumnModel model, String[] toolTips) {
			super(model);
			this.toolTips = toolTips;
		}

		@Override
		public String getToolTipText(MouseEvent e) {
			int col = columnAtPoint(e.getPoint());
			int modelCol = getTable().convertColumnIndexToModel(col);
			return toolTips[modelCol];
		}
	}
	
	/**
	 * Sets row height and column widths; applies text cell renderer
	 * 
	 * @param table table model
	 * @param tablePreferredWidth scales overall table width 
	 */
	private void tableCellsFormatter(JTable table, int tablePreferredWidth) {
		// add increment to row height (may vary with OS?) 
		table.setRowHeight(table.getRowHeight() + ROW_HT_INCREMENT);
		
		// prepares text cell renderer (excludes Use cell boolean type) 
		DefaultTableCellRenderer cr0 = new DefaultTableCellRenderer();
		cr0.setHorizontalAlignment(SwingConstants.CENTER);

		for (int idx = 0; idx < table.getColumnModel().getColumnCount(); idx++) {
			int preferredWidth = tablePreferredWidth * ColumnsEnum.getEnum(idx).getWidth()
					/ ColumnsEnum.getTotalWidth();
			table.getColumnModel().getColumn(idx).setPreferredWidth(preferredWidth);

			if (idx != ColumnsEnum.USE_COL.getIndex()) {
				table.getColumnModel().getColumn(idx).setCellRenderer(cr0);
			}
		}
	}
	
	/* 
	 * This class encapsulates header text and cell formatting 
	 */
	private class HeaderRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public HeaderRenderer() {
			setFont(new Font("Consolas", Font.BOLD, 12));
			setForeground(Color.BLACK);
			setBorder(BorderFactory.createEtchedBorder());
		}

		@Override
		public boolean isOpaque() {
			return true;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			setText(value.toString());
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}
	

	/* 
	 * This class dedicated to setting aperture column GREEN (raget object) or 
	 * RED (reference objects) ..
	 */
	private class ApColumnRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {
			Component cellComponent = super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			
			// sets top (target) ApId cell = green, other ApIds = red + horiz text align
			Color color = (row == 0) ? Color.GREEN : Color.RED;
			cellComponent.setForeground(color);
			setHorizontalAlignment(JLabel.CENTER);
			return cellComponent;
		}
	}
	


	public static void main(String[] args) {

	}

}
