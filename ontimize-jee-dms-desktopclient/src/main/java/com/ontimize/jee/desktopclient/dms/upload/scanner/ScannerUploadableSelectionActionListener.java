package com.ontimize.jee.desktopclient.dms.upload.scanner;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.ontimize.jee.desktopclient.dms.upload.disk.LocalDiskDmsUploadable;


public class ScannerUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {

	//private UAcquisitionDataField acquisitionDataField;

	public ScannerUploadableSelectionActionListener(Button button) throws Exception {
		super(button);
	}


	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {
//		if (this.acquisitionDataField == null) {
//			Hashtable<String, Object> params = new Hashtable<>();
//			params.put("opengenerated", "false");
//			params.put("closeOnGenerate", "true");
//			try {
//				this.acquisitionDataField = new UAcquisitionDataField(params);
//			} catch (Exception error) {
//				throw new DmsException(error);
//			}
//		}
//		List<File> resFile = this.acquisitionDataField.showAcquireDialog(this.getButton(), "dms.scanner_acquisition", this.getResourceBundle());
//		if ((resFile != null) && !resFile.isEmpty()) {
//			List<Path> res = FileTools.toPath(resFile);
//			String fileName = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.fileNameInput"));
//			String description = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.descriptioninput"));
//			LocalDiskDmsUploadable uploadable = new LocalDiskDmsUploadable(res.get(0), description);
//			if (!fileName.endsWith(".pdf")) {
//				fileName = fileName + ".pdf";
//			}
//			uploadable.setName(fileName);
//			return uploadable;
//		}
		return null;
	}
	
	public ResourceBundle getResourceBundle() {
		ResourceBundle resourceBundle = this.button.getParentForm().getResourceBundle();
		if (resourceBundle == null) {
			return this.button.getParentForm().getFormManager().getResourceBundle();
		}
		return ApplicationManager.getApplicationBundle();
	}
}
