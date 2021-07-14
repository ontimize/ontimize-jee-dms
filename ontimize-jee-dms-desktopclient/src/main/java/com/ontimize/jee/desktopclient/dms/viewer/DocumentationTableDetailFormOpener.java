package com.ontimize.jee.desktopclient.dms.viewer;

import java.awt.Desktop;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Hashtable;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;

public class DocumentationTableDetailFormOpener {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationTableDetailFormOpener.class);

	public DocumentationTableDetailFormOpener(Hashtable<String, Object> av) {
	}

	public boolean openDetailForm(final Table table, final int row) {
		new SwingWorker<Path, Void>() {

			@Override
			protected Path doInBackground() throws Exception {
				Hashtable<String, Object> data = table.getRowData(row);
				return DmsTransfererManagerFactory.getInstance().obtainDmsFileVersion(
						(Serializable) data.get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION));
			}

			@Override
			protected void done() {
				try {
					Path file = this.get();
					Desktop.getDesktop().open(file.toFile());
				} catch (Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex,
							DocumentationTableDetailFormOpener.LOGGER);

				}
			}
		}.execute();
		return true;
	}

	public boolean openInsertForm(Table table) {
		return false;
	}
}
