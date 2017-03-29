package com.ontimize.jee.desktopclient.dms.transfermanager;

import com.ontimize.jee.common.services.dms.DocumentIdentifier;

public class AbstractDmsUploadable extends AbstractDmsTransferable {

	/**
	 * The document identifier.
	 */
	private DocumentIdentifier	documentIdentifier;

	/** The category id. */
	private Object			categoryId;

	/** The description. */
	private final String	description;

	public AbstractDmsUploadable(String description, String name, Long size) {
		super(name, size);
		this.description = description;
		this.documentIdentifier = new DocumentIdentifier();
	}

	/**
	 * Sets the document identifier
	 *
	 * @param documentIdentifier
	 */
	public void setDocumentIdentifier(DocumentIdentifier documentIdentifier) {
		this.documentIdentifier = documentIdentifier;
	}

	/**
	 * Sets the category id.
	 *
	 * @param categoryId
	 *            the new category id
	 */
	public void setCategoryId(Object categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * Gets the document unique identifier.
	 *
	 * @return the document identifier
	 */
	public DocumentIdentifier getDocumentIdentifier() {
		return this.documentIdentifier;
	}

	/**
	 * Gets the category id.
	 *
	 * @return the category id
	 */
	public Object getCategoryId() {
		return this.categoryId;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
}
