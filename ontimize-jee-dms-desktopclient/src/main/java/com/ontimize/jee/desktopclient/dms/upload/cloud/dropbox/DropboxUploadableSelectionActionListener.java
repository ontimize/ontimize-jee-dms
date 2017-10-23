package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import com.dropbox.core.DbxEntry;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.utilmize.client.UClientApplication;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class DropboxUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	protected static Dimension	PREFERRED_SIZE	= new Dimension(500, 600);
	protected Form				formDialog;

	public DropboxUploadableSelectionActionListener() throws Exception {
		super();
	}

	public DropboxUploadableSelectionActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public DropboxUploadableSelectionActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public DropboxUploadableSelectionActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {

		if (this.formDialog == null) {
			this.formDialog = UClientApplication.getCurrentActiveForm().getFormManager().getFormCopy("ontimize-dms-forms/formDropbox.form", IMDropbox.class.getName());
			this.formDialog.putInModalDialog(this.formDialog.getFormTitle(), this.getForm());
			this.formDialog.getJDialog().setSize(DropboxUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setPreferredSize(DropboxUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setMaximumSize(DropboxUploadableSelectionActionListener.PREFERRED_SIZE);
		}
		this.formDialog.deleteDataField("CLOUD_FILE");
		this.formDialog.getJDialog().setVisible(true);
		DbxEntry file = (DbxEntry) this.formDialog.getDataFieldValue("CLOUD_FILE");
		if (file != null) {
			try {
				return new DropboxDmsUploadable(file, null, file.name, file.asFile().numBytes);
			} catch (MalformedURLException error) {
				throw new DmsException(error);
			}
		}
		return null;
	}

}
