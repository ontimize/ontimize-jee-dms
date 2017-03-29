package com.ontimize.jee.desktopclient.dms.upload.disk;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.attachment.EJFile;
import com.ontimize.gui.preferences.ApplicationPreferences;

public class EJFileSaveLastDirectory extends EJFile {
	protected static final String	KEY_LAST_DIRECTORY	= "lastDirectoryJFileChooser";
	protected static File			lastDirectory		= null;

	public EJFileSaveLastDirectory() {
		super();
		ApplicationPreferences preferences = ApplicationManager.getApplication().getPreferences();
		if (preferences.getPreference(null, EJFileSaveLastDirectory.KEY_LAST_DIRECTORY) != null) {
			EJFileSaveLastDirectory.lastDirectory = new File(preferences.getPreference(null, EJFileSaveLastDirectory.KEY_LAST_DIRECTORY));
		}
	}

	@Override
	public int showOpenDialog(Component parent) throws HeadlessException {
		if (EJFileSaveLastDirectory.lastDirectory != null) {
			this.setCurrentDirectory(EJFileSaveLastDirectory.lastDirectory);
		}
		int returnValue = super.showOpenDialog(parent);
		if ((returnValue != JFileChooser.ERROR_OPTION) && (this.getSelectedFile() != null) && (!this.getSelectedFile().getPath().isEmpty())) {
			ApplicationManager.getApplication().getPreferences()
					.setPreference(null, EJFileSaveLastDirectory.KEY_LAST_DIRECTORY, this.getSelectedFile().getParent());
			EJFileSaveLastDirectory.lastDirectory = new File(this.getSelectedFile().getParent());
		}
		return returnValue;
	}

}
