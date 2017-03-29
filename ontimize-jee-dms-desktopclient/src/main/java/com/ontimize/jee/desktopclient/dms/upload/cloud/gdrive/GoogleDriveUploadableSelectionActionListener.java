package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.model.File;
import com.ontimize.gui.Form;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.utilmize.client.UClientApplication;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class GoogleDriveUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {
	private static final Logger	logger			= LoggerFactory.getLogger(GoogleDriveUploadableSelectionActionListener.class);
	protected static Dimension	PREFERRED_SIZE	= new Dimension(500, 600);
	protected Form				formDialog;

	public GoogleDriveUploadableSelectionActionListener() throws Exception {
		super();
	}

	public GoogleDriveUploadableSelectionActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public GoogleDriveUploadableSelectionActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public GoogleDriveUploadableSelectionActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws Exception {

		if (this.formDialog == null) {
			this.formDialog = UClientApplication.getCurrentActiveForm().getFormManager()
					.getFormCopy("ontimize-dms-forms/formGoogleDrive.form", IMGoogleDrive.class.getName());
			this.formDialog.putInModalDialog(this.formDialog.getFormTitle(), this.getForm());
			this.formDialog.getJDialog().setSize(GoogleDriveUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setPreferredSize(GoogleDriveUploadableSelectionActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setMaximumSize(GoogleDriveUploadableSelectionActionListener.PREFERRED_SIZE);
		}
		this.formDialog.deleteDataField("CLOUD_FILE");
		this.formDialog.getJDialog().setVisible(true);
		File file = (File) this.formDialog.getDataFieldValue("CLOUD_FILE");
		if (file != null) {
			return new GoogleDriveDmsUploadable(file, file.getDescription(), file.getTitle(), file.getFileSize());
		}
		return null;
	}

}
