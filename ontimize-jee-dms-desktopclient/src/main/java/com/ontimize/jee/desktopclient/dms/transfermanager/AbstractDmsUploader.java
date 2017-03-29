package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.util.ProgressInputStream;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

public abstract class AbstractDmsUploader<T extends AbstractDmsUploadable> extends AbstractDmsTransferer<T> {
	private static final Logger	logger	= LoggerFactory.getLogger(AbstractDmsUploader.class);

	@Override
	protected void doTransfer(T transferable) throws DmsException {
		transferable.setStatus(Status.DOWNLOADING);
		InputStream is = this.doDownloadFromSource(transferable);
		transferable.setStatus(Status.UPLOADING);
		this.doUploadToDms(is, transferable);
	}

	protected abstract InputStream doDownloadFromSource(T transferable) throws DmsException;

	//@formatter:off
	/**
	 *  Depending on values received in transferable DocumentIdentifier we will decide between:
	 * 	 · (A) Insert  complete Document + File + Version    ---> When no identifier data
	 * 	 · (B) Insert  File + Version    ---> When identifier only has document identifier
	 * 	 · (C) Insert  only new  Version    ---> When identifier has document and  file ids
	 * 	 Note: has no sense to send version identifier
	 * 	 Finally information will be notified in own transferable document identifier object
	 * @param is
	 * @param transferable
	 * @throws DmsException
	 */
	//@formatter:on
	protected void doUploadToDms(InputStream is, T transferable) throws DmsException {
		try {
			DocumentIdentifier sourceDocIdF = transferable.getDocumentIdentifier();
			if (sourceDocIdF == null) {
				sourceDocIdF = new DocumentIdentifier();
			}

			IDMSService dmsService = BeansFactory.getBean(IDMSService.class);
			if (sourceDocIdF.getDocumentId() == null) {
				// Case (A)
				Map<String, Object> av = new HashMap<>();
				av.put(DMSNaming.DOCUMENT_DOCUMENT_NAME, transferable.getName());
				DocumentIdentifier newDocIdf = dmsService.documentInsert(av);

				sourceDocIdF.setDocumentId(newDocIdf.getDocumentId());
			}

			if (sourceDocIdF.getFileId() == null) {
				// Case (B)
				Map<String, Object> attrs = this.getAVFromTransferable(transferable);
				DocumentIdentifier newFileInfo = dmsService.fileInsert(sourceDocIdF.getDocumentId(), attrs, new ProgressInputStream(is, transferable,
						transferable.getSize()));

				// Update source document identifier
				sourceDocIdF.setFileId(newFileInfo.getFileId());
				sourceDocIdF.setVersionId(newFileInfo.getVersionId());
			} else {
				Map<String, Object> attrs = this.getAVFromTransferable(transferable);
				DocumentIdentifier newFileInfo = dmsService.fileUpdate(sourceDocIdF.getFileId(), attrs, new ProgressInputStream(is, transferable,
						transferable.getSize()));

				// Update source document identifier
				sourceDocIdF.setVersionId(newFileInfo.getVersionId());
			}
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
				AbstractDmsUploader.logger.error(null, ex);
			}
		}
	}

	private Map<String, Object> getAVFromTransferable(T transferable) {
		Map<String, Object> attrs = new HashMap<String, Object>();
		MapTools.safePut(attrs, DMSNaming.DOCUMENT_FILE_NAME, transferable.getName());
		MapTools.safePut(attrs, DMSNaming.CATEGORY_ID_CATEGORY, transferable.getCategoryId());
		MapTools.safePut(attrs, DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION, transferable.getDescription());
		return attrs;
	}

}
