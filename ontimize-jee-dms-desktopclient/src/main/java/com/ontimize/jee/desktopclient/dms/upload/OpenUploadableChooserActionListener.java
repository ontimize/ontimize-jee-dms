package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.container.CardPanel;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.ontimize.jee.desktopclient.dms.viewer.DocumentationTable;
import com.ontimize.util.ParseUtils;
import com.utilmize.client.UClientApplication;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class OpenUploadableChooserActionListener extends AbstractActionListenerButton {

	private static final Logger	logger			= LoggerFactory.getLogger(OpenUploadableChooserActionListener.class);

	public static final String	TRANSFERABLE	= "TRANSFERABLE";
	protected static Dimension	PREFERRED_SIZE	= new Dimension(350, 350);
	protected Form				formDialog		= null;
	protected String			documentationTableAttr;
	DocumentationTable			table			= null;

	public OpenUploadableChooserActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	public OpenUploadableChooserActionListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(null, formComponent, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.documentationTableAttr = ParseUtils.getString((String) params.get("documentationtable"), null);
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
		final DocumentationTable table = this.getDocumentationTable();
		if (table != null) {
			table.addPropertyChangeListener("enable", new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (!(boolean) evt.getNewValue()) {
						OpenUploadableChooserActionListener.this.getButton().setEnabled(false);
					}
				}
			});
		}
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		try {
			AbstractDmsUploadable uploadable = this.showSelectionDialog(this.getForm());
			if (uploadable != null) {
				this.upload(uploadable);
			}
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, OpenUploadableChooserActionListener.logger);
		}
	}

	protected void upload(AbstractDmsUploadable uploadable) throws DmsException {
		Serializable idDocument = (Serializable) this.getForm().getDataFieldValue(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		Serializable idFile = (Serializable) this.getForm().getDataFieldValue(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		Serializable idVersion = (Serializable) this.getForm().getDataFieldValue(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		Serializable idCategory = (Serializable) this.getForm().getDataFieldValue(DMSNaming.CATEGORY_ID_CATEGORY);
		final DocumentationTable table = this.getDocumentationTable();
		if (table != null) {
			idDocument = table.getCurrentIdDocument();
			idCategory = table.getCurrentIdCategory();
		}
		if (idDocument == null) {
			throw new DmsException(DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		}
		DocumentIdentifier docIdf = new DocumentIdentifier(idDocument, idFile, idVersion);
		uploadable.setDocumentIdentifier(docIdf);
		uploadable.setCategoryId(idCategory);
		if (table != null) {
			uploadable.addObserver(new Observer() {

				@Override
				public void update(Observable observable, Object arg) {
					CheckingTools.failIf(!(observable instanceof AbstractDmsUploadable), "Observable not instanceof AbstractDmsUploadable");
					AbstractDmsUploadable uploadable = (AbstractDmsUploadable) observable;
					if (uploadable.getStatus().equals(Status.COMPLETED) && (table.getCurrentIdDocument() != null) && table.getCurrentIdDocument()
					        .equals(uploadable.getDocumentIdentifier()
					                .getDocumentId()) && ((table.getCurrentIdCategory() == null) || table.getCurrentIdCategory().equals(uploadable.getCategoryId()))) {
						table.refreshInThread(0);
					}
				}
			});
		}
		DmsTransfererManagerFactory.getInstance().transfer(uploadable);
	}

	protected DocumentationTable getDocumentationTable() {
		if (this.table == null) {
			this.table = this.findDocumentationTable(this.documentationTableAttr);
		}
		return this.table;
	}

	public DocumentationTable findDocumentationTable(String attr) {
		if (attr != null) {
			for (Object component : this.getForm().getComponentList()) {
				if ((component instanceof DocumentationTable) && ((DocumentationTable) component).getAttribute().toString().equals(attr)) {
					return (DocumentationTable) component;
				}
			}
		}
		return null;
	}

	protected AbstractDmsUploadable showSelectionDialog(Form parentForm) {
		if (this.formDialog == null) {
			this.formDialog = UClientApplication.getCurrentActiveForm().getFormManager().getFormCopy("ontimize-dms-forms/formMultipleFiles.form", IMMultipleFiles.class.getName());
			this.formDialog.putInModalDialog(this.formDialog.getFormTitle(), parentForm);
			this.formDialog.getJDialog().setModal(true);
			this.formDialog.getJDialog().setResizable(false);
			this.formDialog.getJDialog().setSize(OpenUploadableChooserActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setPreferredSize(OpenUploadableChooserActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setMaximumSize(OpenUploadableChooserActionListener.PREFERRED_SIZE);
		}
		this.formDialog.deleteDataFields();
		((CardPanel) this.formDialog.getElementReference("cardpanel")).show("mainpanel");
		this.formDialog.setDataFieldValue(OpenUploadableChooserActionListener.TRANSFERABLE, null);
		this.formDialog.getJDialog().setVisible(true);
		return (AbstractDmsUploadable) this.formDialog.getDataFieldValue(OpenUploadableChooserActionListener.TRANSFERABLE);
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		super.interactionManagerModeChanged(interactionmanagermodeevent);
		if (!this.getDocumentationTable().isEnabled()) {
			this.getButton().setEnabled(false);
		}
	}
}