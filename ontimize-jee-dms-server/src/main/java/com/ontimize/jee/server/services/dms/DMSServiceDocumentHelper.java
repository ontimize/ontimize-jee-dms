package com.ontimize.jee.server.services.dms;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.dao.IDMSCategoryDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentPropertyDao;
import com.ontimize.jee.server.services.dms.dao.IDMSRelatedDocumentDao;

/**
 * The Class DMSServiceDocumentHelper.
 */
@Component
@Lazy(value = true)
public class DMSServiceDocumentHelper {

	@Autowired
	DefaultOntimizeDaoHelper			daoHelper;

	/** The document dao. */
	@Autowired
	protected IDMSDocumentDao			documentDao;

	/** The related document dao. */
	@Autowired
	protected IDMSRelatedDocumentDao	relatedDocumentDao;

	/** The document property dao. */
	@Autowired
	protected IDMSDocumentPropertyDao	documentPropertyDao;
	/** The document file dao. */
	@Autowired
	protected IDMSDocumentFileDao		documentFileDao;
	@Autowired
	protected IDMSCategoryDao			categoryDao;

	/** The service file helper. */
	@Autowired
	protected DMSServiceFileHelper		serviceFileHelper;

	/**
	 * Document query.
	 *
	 * @param attributes
	 *            the attributes
	 * @param criteria
	 *            the criteria
	 * @return the entity result
	 */
	public EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) {
		return this.daoHelper.query(this.documentDao, criteria, attributes);
	}

	/**
	 * Document insert.
	 *
	 * @param av
	 *            the av
	 * @return the object
	 */
	public DocumentIdentifier documentInsert(Map<?, ?> av) {
		// TODO cubir la información del usuario actual
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// UserInformation userInfo = (UserInformation) authentication.getPrincipal();
		// userInfo.getUsername();
		if (av.get(DMSNaming.DOCUMENT_OWNER_ID) == null) {
			((Map<Object, Object>) av).put(DMSNaming.DOCUMENT_OWNER_ID, Integer.valueOf(1));
		}
		EntityResult res = this.daoHelper.insert(this.documentDao, av);

		DocumentIdentifier result = new DocumentIdentifier(res.get(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT));
		return result;
	}

	/**
	 * Document update.
	 *
	 * @param documentId
	 *            the document id
	 * @param attributesValues
	 *            the attributes values
	 */
	public void documentUpdate(Object documentId, Map<?, ?> attributesValues) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		this.daoHelper.update(this.documentDao, attributesValues, kv);
	}

	/**
	 * Document delete.
	 *
	 * @param documentId
	 *            the document id
	 */
	public void documentDelete(Object documentId) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		// borramos las propiedades
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		this.documentPropertyDao.unsafeDelete(kv);
		// borramos las relaciones
		kv = new HashMap<>();
		kv.put(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER, documentId);
		this.relatedDocumentDao.unsafeDelete(kv);
		kv = new HashMap<>();
		kv.put(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD, documentId);
		this.relatedDocumentDao.unsafeDelete(kv);
		kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		this.categoryDao.unsafeDelete(kv);

		// borramos los ficheros
		EntityResult resFiles = this.documentGetFiles(documentId, new HashMap<>(), Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE }));
		List<?> vDocumentId = (List<?>) resFiles.get(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		List<Path> toDelete = new ArrayList<>();
		if (vDocumentId != null) {
			for (Object fileId : vDocumentId) {

				EntityResult res = this.serviceFileHelper.fileGetVersions(fileId, new HashMap<>(),
						Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION }));
				Vector<?> fileVersionIds = (Vector<?>) res.get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);

				// borramos las versiones, sin borrar los ficheros
				List<Path> toDeletePartial = this.serviceFileHelper.deleteFileVersionsWithoutDeleteFiles(fileId, fileVersionIds);

				// borramos el fichero
				Map<String, Object> kvFile = new HashMap<>();
				kvFile.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
				this.daoHelper.delete(this.documentFileDao, kvFile);

				toDelete.addAll(toDeletePartial);
			}
		}
		// Borramos el documento
		kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		this.daoHelper.delete(this.documentDao, kv);

		// si todo fue bien y no se va a hacer rollback, borramos
		for (Path path : toDelete) {
			FileTools.deleteQuitely(path);
		}
	}

	/**
	 * Document add properties.
	 *
	 * @param documentId
	 *            the document id
	 * @param properties
	 *            the properties
	 */
	public void documentAddProperties(Object documentId, Map<String, String> properties) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		CheckingTools.failIfNull(properties, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);

		Map<String, Object>[] batch = new HashMap[properties.size()];
		int i = 0;
		for (Entry<String, String> entry : properties.entrySet()) {
			Map<String, Object> av = new HashMap<>();
			av.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
			av.put(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY, entry.getKey());
			av.put(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE, entry.getValue());
			batch[i++] = av;
		}
		this.documentPropertyDao.insertBatch(batch);
	}

	/**
	 * Document delete properties.
	 *
	 * @param documentId
	 *            the document id
	 * @param propertyKeys
	 *            the property keys
	 */
	public void documentDeleteProperties(Object documentId, List<String> propertyKeys) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		CheckingTools.failIfNull(propertyKeys, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);
		if (propertyKeys.size() == 0) {
			return;
		}

		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		kv.put(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY, new SearchValue(SearchValue.IN, propertyKeys));
		this.documentPropertyDao.unsafeDelete(kv);
	}

	/**
	 * Document get property.
	 *
	 * @param documentId
	 *            the document id
	 * @param propertyKey
	 *            the property key
	 * @return the string
	 */
	public String documentGetProperty(Object documentId, String propertyKey) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		CheckingTools.failIfNull(propertyKey, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		kv.put(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY, propertyKey);
		EntityResult rs = this.daoHelper
				.query(this.documentPropertyDao,
						kv,
						Arrays.asList(new String[] { DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY, DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY, DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE }));
		if (rs.calculateRecordNumber() == 0) {
			return null;
		}
		return (String) rs.getRecordValues(0).get(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE);
	}

	/**
	 * Document get properties.
	 *
	 * @param documentId
	 *            the document id
	 * @param kv
	 *            the kv
	 * @return the map
	 */
	public Map<String, String> documentGetProperties(Object documentId, Map<?, ?> kv) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		((Map<Object, Object>) kv).put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		EntityResult rs = this.daoHelper
				.query(this.documentPropertyDao,
						kv,
						Arrays.asList(new String[] { DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY, DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY, DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE }));
		Map<String, String> res = new HashMap<>();
		int nregs = rs.calculateRecordNumber();
		for (int i = 0; i < nregs; i++) {
			Map<?, ?> record = rs.getRecordValues(i);
			res.put((String) record.get(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY), (String) record.get(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE));
		}
		return res;
	}

	/**
	 * Document get files.
	 *
	 * @param documentId
	 *            the document id
	 * @param kv
	 *            the kv
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 */
	public EntityResult documentGetFiles(Object documentId, Map<?, ?> kv, List<?> attributes) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		((Map<Object, Object>) kv).put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		return this.daoHelper.query(this.documentFileDao, kv, attributes);
	}

	/**
	 * Document get all files.
	 *
	 * @param documentId
	 *            the document id
	 * @param kv
	 *            the kv
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	public EntityResult documentGetAllFiles(Object documentId, Map<?, ?> kv, List<?> attributes) throws OntimizeJEERuntimeException {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		((Map<Object, Object>) kv).put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		return this.daoHelper.query(this.documentFileDao, kv, attributes, "allfiles");
	}

	/**
	 * Gets the related document.
	 *
	 * @param documentId
	 *            the document id
	 * @return the related document
	 */
	public EntityResult getRelatedDocument(Object documentId) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER, documentId);
		return this.daoHelper
				.query(this.relatedDocumentDao,
						kv,
						Arrays.asList(new String[] { DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD, DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER, DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT }));
	}

	/**
	 * Sets the related documents.
	 *
	 * @param masterDocumentId
	 *            the master document id
	 * @param childDocumentId
	 *            the child document id
	 */
	public void setRelatedDocuments(Object masterDocumentId, Object childDocumentId) {
		CheckingTools.failIfNull(masterDocumentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		CheckingTools.failIfNull(childDocumentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		Map<String, Object> av = new HashMap<>();
		av.put(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER, masterDocumentId);
		av.put(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD, childDocumentId);
		this.daoHelper.insert(this.relatedDocumentDao, av);
	}
}
