package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.model.File;
import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.CardPanel;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.desktopclient.dms.upload.cloud.CloudDrivePanel;
import com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudFileSelectionListener;
import com.utilmize.client.fim.UBasicFIM;

public class IMGoogleDrive extends UBasicFIM implements ICloudFileSelectionListener<File> {

	private static final Logger				logger	= LoggerFactory.getLogger(IMGoogleDrive.class);
	@FormComponent(attr = "WEB")
	private LoginIntoGoogleDriveComponent	moduleWeb;
	@FormComponent(attr = "cardpanel")
	private CardPanel						cp;
	@FormComponent(attr = "remotepanel")
	private CloudDrivePanel<File>			remotePanel;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.remotePanel.setSelectionListener(this);
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
		try {
			this.moduleWeb.showLoginScreen();
			this.showCardPanel("login");
		} catch (Exception e) {
			IMGoogleDrive.logger.error(null, e);
		}
	}

	public void showRemoteDir() throws Exception {
		this.remotePanel.startNavigation();
		this.cp.show("remotedir");
	}

	public void showCardPanel(String panel) {
		this.cp.show(panel);
	}

	@Override
	public void onFileSelected(File file) {
		if (file != null) {
			this.managedForm.setDataFieldValue("CLOUD_FILE", file);
		}
		this.managedForm.getJDialog().setVisible(false);
	}

}