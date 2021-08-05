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

public class CatalogTable {

	private JTable table;
	private CatalogTableModel myModel;
	
	private final int ROW_HT_INCREMENT = 6;
	private final int PREFERRED_WIDTH = 800;
	private static int HEADER_HT = 24;

	public CatalogTable(CatalogFormUI catalogUi) {

		CatalogFormUI mycatalog = catalogUi;
		JScrollPane spane = mycatalog.tableScrollPane;

		myModel = new CatalogTableModel();
		table = new JTable(myModel);

		catalogUi.setSimpleListener(myModel);
		
		ToolTipHeader header = 
				new ToolTipHeader(table.getColumnModel(), CatalogTableModel.toolTips);
		table.setTableHeader(header);
		header.setDefaultRenderer(new HeaderRenderer());
		
		tableCellsFormatter(table, PREFERRED_WIDTH);
		table.getColumnModel().getColumn(0).setCellRenderer(new ApColumnRenderer());

		table.setFillsViewportHeight(true);
		spane.setViewportView(table);
		
		spane.getColumnHeader().setPreferredSize(new Dimension(0, HEADER_HT));
	}
	
	// implementation code to set a tooltip text to each column of JTableHeader
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
	
	public static class HeaderRenderer extends JLabel implements TableCellRenderer {

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
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText(value.toString());
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}
	
	class ApColumnRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {
			Component cellComponent = super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			
			if (row == 0) {
				cellComponent.setForeground(Color.GREEN);
			} else {
				cellComponent.setForeground(Color.RED);
			}
			setHorizontalAlignment(JLabel.CENTER);
			return cellComponent;
		}

	}
	
	public void tableCellsFormatter(JTable table, int tablePreferredWidth) {

		table.setRowHeight(table.getRowHeight() + ROW_HT_INCREMENT);

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

	public static void main(String[] args) {

	}

}
