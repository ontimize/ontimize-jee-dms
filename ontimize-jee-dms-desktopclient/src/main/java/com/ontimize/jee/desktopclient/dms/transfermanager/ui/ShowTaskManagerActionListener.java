package com.ontimize.jee.desktopclient.dms.transfermanager.ui;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.components.taskmanager.TaskManagerGUI;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.ITransferQueueListener;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.TransferQueueChangedEvent;
import com.ontimize.jee.desktopclient.dms.util.AnimateIconButton;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class ShowTaskManagerActionListener extends AbstractActionListenerButton implements ITransferQueueListener {

	private static final Logger		logger							= LoggerFactory.getLogger(ShowTaskManagerActionListener.class);

	protected static final String	ICO_DOWNLOAD_MANAGER_DEFAULT	= "ontimize-dms-images/download_manager_22x22.png";
	protected static final String	GIF_DOWNLOAD_MANAGER_PROGRESS	= "ontimize-dms-images/roller_22x22.gif";

	public ShowTaskManagerActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		DmsTransfererManagerFactory.getInstance().addTransferQueueListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			this.toggleDialog();
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, ShowTaskManagerActionListener.logger);
		}
	}

	public void toggleDialog() {
		TaskManagerGUI.getInstance().showWindow();
	}

	@Override
	public void onTransferQueueChanged(TransferQueueChangedEvent transferEvent) {
		if (transferEvent.hasPendingTransfers()) {
			AnimateIconButton.animateGIF(this.button, ShowTaskManagerActionListener.GIF_DOWNLOAD_MANAGER_PROGRESS);
		} else {
			AnimateIconButton.animate(this.button, ShowTaskManagerActionListener.ICO_DOWNLOAD_MANAGER_DEFAULT, null, 10, 500);
		}

	}
}
