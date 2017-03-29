package com.ontimize.jee.desktopclient.dms.upload.web;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import com.ontimize.jee.desktopclient.dms.upload.IMMultipleFiles;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class ShowWebPanelActionListener extends AbstractActionListenerButton {

	public ShowWebPanelActionListener() throws Exception {
		super();
	}

	public ShowWebPanelActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public ShowWebPanelActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public ShowWebPanelActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getForm().deleteDataField("URL");
		this.getForm().deleteDataField("URL_DESCRIPTION");
		((IMMultipleFiles) this.getInteractionManager()).showCardPanel("webpanel");

	}
}
