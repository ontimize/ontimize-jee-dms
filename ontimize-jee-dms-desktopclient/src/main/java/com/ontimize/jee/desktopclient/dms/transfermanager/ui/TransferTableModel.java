package com.ontimize.jee.desktopclient.dms.transfermanager.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.ITransferQueueListener;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.TransferQueueChangedEvent;

/**
 * This class manages the download table's data.
 *
 */
public class TransferTableModel extends AbstractTableModel implements Observer, ITransferQueueListener {

	private static final long					serialVersionUID	= 2376138748556807794L;

	public static final String					COLUMN_SIZE			= "dms.SIZE";
	public static final String					COLUMN_SYNC_STATUS	= "dms.SYNC_STATUS";
	public static final String					COLUMN_PROGRESS		= "dms.PROGRESS";
	public static final String					COLUMN_FILENAME		= "dms.FILENAME";

	// These are the names for the table's columns.
	private static final String[]				columnNames			= { TransferTableModel.COLUMN_FILENAME, TransferTableModel.COLUMN_SIZE, TransferTableModel.COLUMN_PROGRESS, TransferTableModel.COLUMN_SYNC_STATUS };

	// These are the classes for each column's values.
	private static final Class<?>[]				columnClasses		= { String.class, Number.class, Number.class, Status.class };
	private final List<AbstractDmsTransferable>	transfersList;

	public TransferTableModel() {
		super();
		this.transfersList = new ArrayList<AbstractDmsTransferable>();
		DmsTransfererManagerFactory.getInstance().addTransferQueueListener(this);
	}

	/**
	 * Remove a download from the list.
	 */
	public void removeRow(int row) {
		if (row >= 0) {
			this.transfersList.remove(row);
			// Fire table row deletion notification to table.
			this.fireTableRowsDeleted(row, row);
		}
	}

	/**
	 * Get table's column count.
	 */
	@Override
	public int getColumnCount() {
		return TransferTableModel.columnNames.length;
	}

	/**
	 * Get a column's name.
	 */
	@Override
	public String getColumnName(int col) {
		return TransferTableModel.columnNames[col];
	}

	/**
	 * Get a column's class.
	 */
	@Override
	public Class getColumnClass(int col) {
		return TransferTableModel.columnClasses[col];
	}

	/**
	 * Get table's row count.
	 */
	@Override
	public int getRowCount() {
		return this.transfersList.size();
	}

	/**
	 * Get value for a specific row and column combination.
	 */
	@Override
	public Object getValueAt(int row, int col) {
		// Get download from download list
		AbstractDmsTransferable transferable = this.transfersList.get(row);

		switch (col) {
			case 0: // Filename
				return transferable.getName();
			case 1: // Size
				return transferable.getSize();
			case 2: // Progress
				return transferable.getProgress();
			case 3: // Sync Status
				return transferable.getStatus();
		}
		return "";
	}

	/**
	 * Update is called when a Download notifies its observers of any changes
	 */
	@Override
	public void update(Observable o, Object arg) {
		int index = this.transfersList.indexOf(o);
		// Fire table row update notification to table.
		this.fireTableRowsUpdated(index, index);
	}

	@Override
	public void onTransferQueueChanged(TransferQueueChangedEvent transferEvent) {
		if (transferEvent.getAddedTransferable() != null) {
			// Register to be notified when the download changes.
			this.transfersList.add(0, transferEvent.getAddedTransferable());
			transferEvent.getAddedTransferable().addObserver(this);
			// Fire table row insertion notification to table.
			this.fireTableRowsInserted(this.getRowCount() - 1, this.getRowCount() - 1);
		}
	}

	public AbstractDmsTransferable getRow(int index) {
		if ((index >= 0) && (index < this.transfersList.size())) {
			return this.transfersList.get(index);
		}
		return null;
	}
}