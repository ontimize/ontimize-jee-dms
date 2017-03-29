package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class HomeActionListener extends AbstractActionListenerButton {

	public HomeActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		((IMMultipleFiles) this.getForm().getInteractionManager()).showCardPanel("mainpanel");
	}

}
