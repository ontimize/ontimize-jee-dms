package com.ontimize.jee.server.services.dms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;

/**
 * The Class DMSCreationHelper.
 */
@Component
@Lazy(true)
public class DMSCreationHelper {

	/** The dms service. */
	@Autowired
	private IDMSService	dmsService;

	/**
	 * Instantiates a new DMS creation helper.
	 */
	public DMSCreationHelper() {
		super();
	}

	/**
	 * Creates the document.
	 *
	 * @param docName
	 *            the doc name
	 * @param ownerId
	 *            the owner id
	 * @param description
	 *            the description
	 * @param keyworkds
	 *            the keyworkds
	 * @param categories
	 *            the categories
	 * @return the object
	 */
	public DocumentIdentifier createDocument(String docName, Object ownerId, String description, String keyworkds, String... categories) {
		Map<String,Object> av = new HashMap<>();
		av.put(DMSNaming.DOCUMENT_DOCUMENT_NAME, docName == null ? "docname" : docName);
		av.put(DMSNaming.DOCUMENT_OWNER_ID,ownerId);
		av.put(DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION,description);
		av.put(DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS,keyworkds);
		DocumentIdentifier idDocument = this.dmsService.documentInsert(av);
		if (categories != null) {
			for (String catName : categories) {
				this.dmsService.categoryInsert(idDocument.getDocumentId(), catName, null, null);
			}
		}
		return idDocument;
	}

	/**
	 * Creates the document.
	 *
	 * @param docName
	 *            the doc name
	 * @return the object
	 */
	public DocumentIdentifier createDocument(String docName) {
		Map<String, Object> av = new HashMap<>();
		av.put(DMSNaming.DOCUMENT_DOCUMENT_NAME, docName == null ? "docname" : docName);
		return this.dmsService.documentInsert(av);
	}

}
