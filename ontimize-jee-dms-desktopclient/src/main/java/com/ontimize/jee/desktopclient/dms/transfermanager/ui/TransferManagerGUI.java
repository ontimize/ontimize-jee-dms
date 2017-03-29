package com.ontimize.jee.desktopclient.dms.transfermanager.ui;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.ITransferQueueListener;
import com.ontimize.jee.desktopclient.dms.transfermanager.events.TransferQueueChangedEvent;
import com.utilmize.tools.WindowUtils;

public class TransferManagerGUI extends javax.swing.JFrame implements ITransferQueueListener {
	private static final long			serialVersionUID	= -5169995535825046706L;

	// The unique instance of this class
	private static TransferManagerGUI	sInstance			= null;

	/**
	 * Get an instance of this class
	 *
	 * @return the instance of this class
	 */
	public static TransferManagerGUI getInstance() {
		if (TransferManagerGUI.sInstance == null) {
			TransferManagerGUI.sInstance = new TransferManagerGUI();
			DmsTransfererManagerFactory.getInstance().addTransferQueueListener(TransferManagerGUI.sInstance);
		}
		return TransferManagerGUI.sInstance;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane	jScrollPane1;
	// private javax.swing.JButton jbnCancel;
	private javax.swing.JButton		jbnExit;
	private javax.swing.JButton		jbnPause;
	private javax.swing.JButton		jbnRemove;
	private javax.swing.JButton		jbnResume;
	private TransferTable			transferTable;

	// End of variables declaration//GEN-END:variables

	/** Creates new form TransferManagerGUI */
	public TransferManagerGUI() {
		super(ApplicationManager.getTranslation("dms.transfermanager"));
		this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		this.setIconImage(ImageManager.getIcon("ontimize-dms-images/download_manager_22x22.png").getImage());
		this.initComponents();
		this.initialize();
	}

	private void initialize() {
		// Set up table

		// Allow only one row at a time to be selected.
		this.transferTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.transferTable.addMouseListener(new MouseListenerOpenFile());

		this.transferTable.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				TransferManagerGUI.this.updateButtons();
			}
		});
		this.transferTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				TransferManagerGUI.this.updateButtons();
			}
		});
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		this.jScrollPane1 = new javax.swing.JScrollPane();
		this.transferTable = new TransferTable();
		this.jbnPause = new javax.swing.JButton();
		this.jbnRemove = new javax.swing.JButton();
		// this.jbnCancel = new javax.swing.JButton();
		this.jbnExit = new javax.swing.JButton();
		this.jbnResume = new javax.swing.JButton();

		this.setTitle(ApplicationManager.getTranslation("dms.DOWNLOAD_MANAGER_TITLE"));
		this.setResizable(true);

		this.jScrollPane1.setViewportView(this.transferTable);

		this.jbnPause.setText(ApplicationManager.getTranslation(("dms.PAUSE")));
		this.jbnPause.setEnabled(false);
		this.jbnPause.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TransferManagerGUI.this.jbnPauseActionPerformed(evt);
			}
		});

		this.jbnRemove.setText(ApplicationManager.getTranslation(("dms.REMOVE")));
		this.jbnRemove.setEnabled(false);
		this.jbnRemove.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TransferManagerGUI.this.jbnRemoveActionPerformed(evt);
			}
		});

		// this.jbnCancel.setText(ApplicationManager.getTranslation(("dms.CANCEL")));
		// this.jbnCancel.setEnabled(false);
		// this.jbnCancel.addActionListener(new java.awt.event.ActionListener() {
		// @Override
		// public void actionPerformed(java.awt.event.ActionEvent evt) {
		// TransferManagerGUI.this.jbnCancelActionPerformed(evt);
		// }
		// });

		this.jbnExit.setText(ApplicationManager.getTranslation(("dms.EXIT")));
		this.jbnExit.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TransferManagerGUI.this.jbnExitActionPerformed(evt);
			}
		});

		this.jbnResume.setText(ApplicationManager.getTranslation(("dms.RESUME")));
		this.jbnResume.setEnabled(false);
		this.jbnResume.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TransferManagerGUI.this.jbnResumeActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
				// .addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												layout.createSequentialGroup()
														.addGap(18, 18, 18)
														.addComponent(this.jbnPause, javax.swing.GroupLayout.PREFERRED_SIZE, 95,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(this.jbnResume, javax.swing.GroupLayout.PREFERRED_SIZE, 95,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														// .addGap(18, 18, 18)
														// .addComponent(this.jbnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 95,
														// javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(this.jbnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 95,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
														.addComponent(this.jbnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 95,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup())
										.addComponent(this.jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
		// .addContainerGap()
				));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { /* this.jbnCancel, */this.jbnExit, this.jbnPause, this.jbnRemove, this.jbnResume });

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						// .addContainerGap()
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(this.jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(this.jbnPause)
										.addComponent(this.jbnResume)./* addComponent(this.jbnCancel). */addComponent(this.jbnRemove)
										.addComponent(this.jbnExit)).addContainerGap()));

		this.pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jbnPauseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jbnPauseActionPerformed
		this.getSelectedRow().pause();
		this.updateButtons();
	}// GEN-LAST:event_jbnPauseActionPerformed

	private void jbnResumeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jbnResumeActionPerformed
		this.getSelectedRow().resume();
		this.updateButtons();
	}// GEN-LAST:event_jbnResumeActionPerformed

	private void jbnCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jbnCancelActionPerformed
		this.getSelectedRow().cancel();
		this.updateButtons();
	}// GEN-LAST:event_jbnCancelActionPerformed

	private void jbnRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jbnRemoveActionPerformed
		int index = this.transferTable.getSelectedRow();
		this.transferTable.getTransferModel().removeRow(index);
		this.updateButtons();
	}// GEN-LAST:event_jbnRemoveActionPerformed

	private void jbnExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jbnExitActionPerformed
		this.setVisible(false);
	}// GEN-LAST:event_jbnExitActionPerformed

	protected AbstractDmsTransferable getSelectedRow() {
		int selectedRow = this.transferTable.getSelectedRow();
		if (selectedRow >= 0) {
			return this.transferTable.getTransferModel().getRow(selectedRow);
		}
		return null;
	}

	/**
	 * Update buttons' state
	 */
	private void updateButtons() {
		AbstractDmsTransferable transferable = this.getSelectedRow();
		if (transferable != null) {
			Status status = transferable.getStatus();
			switch (status) {
				case UPLOADING:
				case DOWNLOADING:
					this.jbnPause.setEnabled(true);
					this.jbnResume.setEnabled(false);
					// this.jbnCancel.setEnabled(true);
					this.jbnRemove.setEnabled(false);
					break;
				case PAUSED:
					this.jbnPause.setEnabled(false);
					this.jbnResume.setEnabled(true);
					// this.jbnCancel.setEnabled(true);
					this.jbnRemove.setEnabled(false);
					break;
				case ERROR:
					this.jbnPause.setEnabled(false);
					this.jbnResume.setEnabled(true);
					// this.jbnCancel.setEnabled(false);
					this.jbnRemove.setEnabled(true);
					break;
				case COMPLETED:
					this.jbnPause.setEnabled(false);
					this.jbnResume.setEnabled(false);
					// this.jbnCancel.setEnabled(false);
					this.jbnRemove.setEnabled(true);
					break;
				case CANCELLED:
					this.jbnPause.setEnabled(false);
					this.jbnResume.setEnabled(false);
					// this.jbnCancel.setEnabled(false);
					this.jbnRemove.setEnabled(true);
					break;
				case ON_PREPARE:
					break;
				default:
					break;
			}
		} else {
			// No download is selected in table.
			this.jbnPause.setEnabled(false);
			this.jbnResume.setEnabled(false);
			// this.jbnCancel.setEnabled(false);
			this.jbnRemove.setEnabled(false);
		}
	}

	@Override
	public void onTransferQueueChanged(TransferQueueChangedEvent transferEvent) {
		if (transferEvent.getAddedTransferable() != null) {
			this.showWindow();
		}
	}

	class MouseListenerOpenFile extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent me) {
			if (me.getClickCount() == 2) {
				// TransferManagerGUI.this.jbnSelectActionPerformed(new ActionEvent(me.getSource(), me.getID(), null));
			}
		}
	}

	public void showWindow() {
		if (!TransferManagerGUI.getInstance().isVisible()) {
			Rectangle screenBounds = WindowUtils.getScreenBounds(WindowUtils.getActiveWindow());
			Dimension size = TransferManagerGUI.getInstance().getSize();
			TransferManagerGUI.getInstance().setLocation(screenBounds.x + (screenBounds.width - size.width),
					(screenBounds.height - size.height) + screenBounds.y);
			TransferManagerGUI.getInstance().setVisible(true);
			TransferManagerGUI.getInstance().setAlwaysOnTop(true);
		}
		TransferManagerGUI.getInstance().toFront();
	}
}