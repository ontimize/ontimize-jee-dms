package com.ontimize.jee.desktopclient.dms.upload.scanner;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.ontimize.jee.desktopclient.dms.upload.disk.LocalDiskDmsUploadable;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.capture.UAcquisitionDataField;

public class ScannerUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {
	private UAcquisitionDataField	acquisitionDataField;

	public ScannerUploadableSelectionActionListener() throws Exception {
		super();
	}

	public ScannerUploadableSelectionActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public ScannerUploadableSelectionActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public ScannerUploadableSelectionActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {
		if (this.acquisitionDataField == null) {
			Hashtable<String, Object> params = new Hashtable<>();
			params.put("opengenerated", "false");
			params.put("closeOnGenerate", "true");
			try {
				this.acquisitionDataField = new UAcquisitionDataField(params);
			} catch (Exception error) {
				throw new DmsException(error);
			}
		}
		List<File> resFile = this.acquisitionDataField.showAcquireDialog(this.getButton(), "dms.scanner_acquisition", this.getResourceBundle());
		if ((resFile != null) && (resFile.size() > 0)) {
			List<Path> res = FileTools.toPath(resFile);
			String fileName = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.fileNameInput"));
			String description = JOptionPane.showInputDialog(ApplicationManager.getTranslation("dms.descriptioninput"));
			LocalDiskDmsUploadable uploadable = new LocalDiskDmsUploadable(res.get(0), description);
			if (!fileName.endsWith(".pdf")) {
				fileName = fileName + ".pdf";
			}
			uploadable.setName(fileName);
			return uploadable;
		}
		return null;
	}
}
