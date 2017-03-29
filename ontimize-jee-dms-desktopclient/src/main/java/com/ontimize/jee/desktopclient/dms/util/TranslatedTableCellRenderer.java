package com.ontimize.jee.desktopclient.dms.util;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ontimize.gui.ApplicationManager;

/**
 * The Class TranslatedTableCellRenderer.
 */
public class TranslatedTableCellRenderer implements TableCellRenderer {

	/** The original renderer. */
	private TableCellRenderer	originalRenderer;

	/**
	 * Instantiates a new translated table cell renderer.
	 *
	 * @param originalRenderer
	 *            the original renderer
	 */
	public TranslatedTableCellRenderer(TableCellRenderer originalRenderer) {
		super();
		this.originalRenderer = originalRenderer;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof String) {
			value = ApplicationManager.getTranslation((String) value);
		}
		return originalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
