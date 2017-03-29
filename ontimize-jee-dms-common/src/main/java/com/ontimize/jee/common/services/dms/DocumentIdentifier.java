package com.ontimize.jee.common.services.dms;

import java.io.Serializable;

/**
 * This class must identify by unique way a file: Document + File + Version
 */
public class DocumentIdentifier implements Serializable {
	/**
	 * ID_DMS_DOC
	 */
	protected Object	documentId;

	/**
	 * ID_DMS_DOC_FILE
	 */
	protected Object	fileId;

	/**
	 * ID_DMS_DOC_FILE_VERSION
	 */
	protected Object	versionId;

	public DocumentIdentifier() {
		this(null);
	}

	public DocumentIdentifier(Object documentId) {
		this(documentId, null);
	}

	public DocumentIdentifier(Object documentId, Object fileId) {
		this(documentId, fileId, null);
	}

	public DocumentIdentifier(Object documentId, Object fileId, Object versionId) {
		this.documentId = documentId;
		this.fileId = fileId;
		this.versionId = versionId;
	}

	public Object getDocumentId() {
		return this.documentId;
	}

	public void setDocumentId(Object documentId) {
		this.documentId = documentId;
	}

	public Object getFileId() {
		return this.fileId;
	}

	public void setFileId(Object fileId) {
		this.fileId = fileId;
	}

	public Object getVersionId() {
		return this.versionId;
	}

	public void setVersionId(Object versionId) {
		this.versionId = versionId;
	}
}
