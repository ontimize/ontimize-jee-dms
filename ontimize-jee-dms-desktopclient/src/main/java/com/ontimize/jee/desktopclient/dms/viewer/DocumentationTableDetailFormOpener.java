package com.ontimize.jee.desktopclient.dms.viewer;

import java.awt.Desktop;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.utilmize.client.gui.field.table.IDetailFormOpener;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.tasks.USwingWorker;

public class DocumentationTableDetailFormOpener implements IDetailFormOpener {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationTableDetailFormOpener.class);

	public DocumentationTableDetailFormOpener(Hashtable<String, Object> av) {}

	@Override
	public boolean openDetailForm(final UTable table, final int row) {
		new USwingWorker<Path, Void>() {

			@Override
			protected Path doInBackground() throws Exception {
				Hashtable<String, Object> data = table.getRowData(row);
				return DmsTransfererManagerFactory.getInstance().obtainDmsFileVersion((Serializable) data.get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION));
			}

			@Override
			protected void done() {
				try {
					Path file = this.uget();
					Desktop.getDesktop().open(file.toFile());
				} catch (Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, DocumentationTableDetailFormOpener.LOGGER);

				}
			}
		}.execute();
		return true;
	}

	@Override
	public boolean openInsertForm(UTable table) {
		return false;
	}
}
