package com.ontimize.jee.desktopclient.dms.taskmanager;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.ObjectCellRenderer;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.utilmize.client.gui.field.table.render.UXmlByteSizeCellRenderer;

/**
 * The Class DescriptionTableCellRenderer.
 */
public class DescriptionTableCellRenderer implements TableCellRenderer {

	private static final Logger				logger	= LoggerFactory.getLogger(DescriptionTableCellRenderer.class);

	/** The size renderer. */
	private final UXmlByteSizeCellRenderer	sizeRenderer;

	/** The default renderer. */
	private final ObjectCellRenderer		defaultRenderer;

	/**
	 * Instantiates a new description table cell renderer.
	 */
	public DescriptionTableCellRenderer() {
		super();
		try {
			this.sizeRenderer = new UXmlByteSizeCellRenderer();
		} catch (Exception error) {
			throw new OntimizeJEERuntimeException(error);
		}
		this.defaultRenderer = new ObjectCellRenderer();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Number) {
			return this.sizeRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		return this.defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}