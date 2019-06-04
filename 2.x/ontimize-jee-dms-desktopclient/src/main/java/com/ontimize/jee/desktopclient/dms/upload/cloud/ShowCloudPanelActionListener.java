package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import com.ontimize.jee.desktopclient.dms.upload.IMMultipleFiles;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class ShowCloudPanelActionListener extends AbstractActionListenerButton {

	public ShowCloudPanelActionListener() throws Exception {
		super();
	}

	public ShowCloudPanelActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public ShowCloudPanelActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public ShowCloudPanelActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		((IMMultipleFiles) this.getInteractionManager()).showCardPanel("hostingpanel");
	}
}
