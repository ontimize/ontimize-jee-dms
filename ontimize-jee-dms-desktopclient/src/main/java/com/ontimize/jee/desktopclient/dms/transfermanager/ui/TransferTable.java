package com.ontimize.jee.desktopclient.dms.transfermanager.ui;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.ObjectCellRenderer;
import com.ontimize.jee.desktopclient.dms.util.TranslatedTableCellRenderer;
import com.utilmize.client.gui.field.table.render.UXmlByteSizeCellRenderer;

public class TransferTable extends JTable {

	private static final Logger	logger				= LoggerFactory.getLogger(TransferTable.class);

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public TransferTable() {
		super(new TransferTableModel());

		// Set up ProgressBar as renderer for progress column.
		ProgressRenderer progressRenderer = new ProgressRenderer();
		// this.jtbDownload.setDefaultRenderer(JProgressBar.class, renderer);

		this.setDefaultRenderer(Object.class, new ObjectCellRenderer());

		this.getColumn(TransferTableModel.COLUMN_PROGRESS).setCellRenderer(progressRenderer);
		this.getColumn(TransferTableModel.COLUMN_SYNC_STATUS).setCellRenderer(new IconToolTipRenderer());
		try {
			this.getColumn(TransferTableModel.COLUMN_SIZE).setCellRenderer(new UXmlByteSizeCellRenderer());
		} catch (Throwable ex) {
			TransferTable.logger.error(null, ex);
		}
		TableCellRenderer headerRenderer = this.getTableHeader().getDefaultRenderer();
		this.getTableHeader().setDefaultRenderer(new TranslatedTableCellRenderer(headerRenderer));

		// Set table's row height large enough to fit JProgressBar.
		this.setRowHeight((int) progressRenderer.getPreferredSize().getHeight());
	}

	public TransferTableModel getTransferModel() {
		return (TransferTableModel) this.getModel();
	}

}
