package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.util.TranslatedTableCellRenderer;
import com.utilmize.client.gui.tasks.USwingWorker;

public class CloudDriveTable<T extends Serializable> extends JTable {
	private static final long						serialVersionUID	= 1L;
	private static final Logger						logger				= LoggerFactory.getLogger(CloudDriveTable.class);
	private final ICloudFileSelectionListener<T>	selectionListener;
	private final Stack<String>						navigationStack		= new Stack<String>();
	private String									currentFolderId;
	private final ICloudManager<T>					cloudManager;

	public CloudDriveTable(ICloudFileSelectionListener<T> selectionListener, ICloudManager<T> cloudManager) {
		super(new CloudDriveTableModel<T>());
		this.cloudManager = cloudManager;
		this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableCellRenderer tableRenderer = cloudManager.getTableRenderer();
		for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {
			this.getColumnModel().getColumn(i).setCellRenderer(tableRenderer);
		}
		this.getTableHeader().setDefaultRenderer(new TranslatedTableCellRenderer(this.getTableHeader().getDefaultRenderer()));
		this.addMouseListener(new NavigationMouseListener());
		this.selectionListener = selectionListener;
	}

	private void fireSelectionEvent(T selectedFile) {
		if (this.selectionListener != null) {
			this.selectionListener.onFileSelected(selectedFile);
		}
	}

	public CloudDriveTableModel<T> getRemoteDriveModel() {
		return (CloudDriveTableModel<T>) this.getModel();
	}

	public T getSelectedFile() {
		return this.getRemoteDriveModel().getFileAtRow(this.getSelectedRow());
	}

	public void startNavigation() throws DmsException {
		this.navigationStack.clear();
		this.browseFolder(this.cloudManager.getRootFolderId());
	}

	private void browseFolder(final String folderId) {
		new USwingWorker<List<T>, Void>() {

			@Override
			protected List<T> doInBackground() throws Exception {
				return CloudDriveTable.this.cloudManager.retrieveFilesInFolder(folderId);
			}

			@Override
			protected void done() {
				try {
					List<T> files = this.uget();
					if (!CloudDriveTable.this.cloudManager.getRootFolderId().equals(folderId)) {
						T backFile = CloudDriveTable.this.cloudManager.getBackFile();
						files.add(0, backFile);
					}
					CloudDriveTable.this.getRemoteDriveModel().setDataList(files);
					CloudDriveTable.this.currentFolderId = folderId;
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, CloudDriveTable.logger);
				}
			};

		}.executeOperation(this);
	}

	public void onFileSelected() {
		T selectedFile = this.getSelectedFile();
		if (CloudDriveTable.this.cloudManager.getBackFile().equals(selectedFile)) {
			this.browseFolder(this.navigationStack.pop());
		} else if (this.cloudManager.isFolder(selectedFile)) {
			this.navigationStack.push(this.currentFolderId);
			this.browseFolder(this.cloudManager.getFolderId(selectedFile));
		} else {
			this.fireSelectionEvent(selectedFile);
		}
	}

	private class NavigationMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent event) {
			if ((event.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(event)) {
				CloudDriveTable.this.onFileSelected();
			}
		}
	}
}
