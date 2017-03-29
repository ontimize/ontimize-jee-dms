package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class CancelActionListener extends AbstractActionListenerButton {

	public CancelActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		this.closeDialog();
	}
}
