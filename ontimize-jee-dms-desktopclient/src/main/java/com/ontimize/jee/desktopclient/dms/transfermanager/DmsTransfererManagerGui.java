package com.ontimize.jee.desktopclient.dms.transfermanager;

import com.ontimize.jee.desktopclient.dms.transfermanager.ui.TransferManagerGUI;

/**
 *
 * This class implements all funcionality to manage downloaders. It saves a list with all downloaders and methods to know status and list all of them.
 *
 */
public class DmsTransfererManagerGui extends DmsTransfererManagerDefault {

	/** Protected constructor */
	protected DmsTransfererManagerGui() {
		super();
	}

	public void init() {
		super.init();
		// Start GUI
		TransferManagerGUI.getInstance();
	}
}
