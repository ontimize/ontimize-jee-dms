package com.ontimize.jee.server.services.dms;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.server.configuration.OntimizeConfiguration;

/**
 * The Ontimize standard DMS implementation. Splitted implementation over helpers.
 */
public class OntimizeDMSEngine implements IDMSService {

	/** The Constant ACTIVE. */
	public static final String			ACTIVE		= "Y";
	/** The Constant INACTIVE. */
	public static final String			INACTIVE	= "N";

	/** The ontimize configuration. */
	@Autowired
	protected OntimizeConfiguration		ontimizeConfiguration;

	/** The file helper. */
	@Autowired
	protected DMSServiceFileHelper		fileHelper;

	/** The document helper. */
	@Autowired
	protected DMSServiceDocumentHelper	documentHelper;

	/** The category helper. */
	@Autowired
	protected DMSServiceCategoryHelper	categoryHelper;

	/** The documents base path. */
	protected Path						documentsBasePath;

	public OntimizeDMSEngine() {
		super();
	}

	/**
	 * Gets the documents base path.
	 *
	 * @return the documents base path
	 */
	public Path getDocumentsBasePath() {
		CheckingTools.failIfNull(this.documentsBasePath, "DMS documents base path is not configured.");
		return this.documentsBasePath;
	}

	/**
	 * Sets the documents base path.
	 *
	 * @param documentsBasePath
	 *            the documents base path
	 */
	public void setDocumentsBasePath(String documentsBasePath) {
		this.documentsBasePath = Paths.get(documentsBasePath);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContent(java.lang .Object)
	 */
	@Override
	public InputStream fileGetContent(Object fileId) {
		return this.fileHelper.fileGetContent(fileId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
	 */
	@Override
	public InputStream fileGetContentOfVersion(Object fileVersionId) {
		return this.fileHelper.fileGetContentOfVersion(fileVersionId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileInsert(java.util.Map, java.io.InputStream)
	 */
	@Override
	public DocumentIdentifier fileInsert(Object documentId, Map<?, ?> av, InputStream is) {
		return this.fileHelper.fileInsert(documentId, av, is);
	}

	/**
	 * Ensures to deprecate current ACTIVE version of file (if exists) and return next DEFAULT AUTOMATIC version.
	 *
	 * @param fileId
	 *            the file id
	 * @return the current file version and deprecate
	 */
	public Number getCurrentFileVersionAndDeprecate(Object fileId) {
		return this.fileHelper.getCurrentFileVersionAndDeprecate(fileId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object , java.util.Map, java.io.InputStream)
	 */
	@Override
	public DocumentIdentifier fileUpdate(Object fileId, Map<?, ?> attributesValues, InputStream is) {
		return this.fileHelper.fileUpdate(fileId, attributesValues, is);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
	 */
	@Override
	public EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) {
		return this.fileHelper.fileQuery(criteria, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
	 */
	@Override
	public void fileDelete(Object fileId) {
		this.fileHelper.fileDelete(fileId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileVersionQuery(java.lang.Object, java.util.List)
	 */
	@Override
	public EntityResult fileVersionQuery(Object fileVersionId, List<?> attributes) throws OntimizeJEERuntimeException {
		return this.fileHelper.fileVersionQuery(fileVersionId, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetVersions(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult fileGetVersions(Object fileId, Map<?, ?> kv, List<?> attributes) {
		return this.fileHelper.fileGetVersions(fileId, kv, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileRecoverPreviousVersion(java.lang.Object)
	 */
	@Override
	public void fileRecoverPreviousVersion(Object fileId, boolean acceptNotPreviousVersion) throws OntimizeJEERuntimeException {
		this.fileHelper.fileRecoverPreviousVersion(fileId, acceptNotPreviousVersion);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentQuery(java.util.List, java.util.Map)
	 */
	@Override
	public EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) {
		return this.documentHelper.documentQuery(attributes, criteria);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentInsert(java.util.Map)
	 */
	@Override
	public DocumentIdentifier documentInsert(Map<?, ?> av) {
		return this.documentHelper.documentInsert(av);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentUpdate(java.lang.Object, java.util.Map)
	 */
	@Override
	public void documentUpdate(Object documentId, Map<?, ?> attributesValues) {
		this.documentHelper.documentUpdate(documentId, attributesValues);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDelete(java.lang.Object)
	 */
	@Override
	public void documentDelete(Object documentId) {
		this.documentHelper.documentDelete(documentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentAddProperties(java.lang.Object, java.util.Map)
	 */
	@Override
	public void documentAddProperties(Object documentId, Map<String, String> properties) {
		this.documentHelper.documentAddProperties(documentId, properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDeleteProperties(java.lang.Object, java.util.List)
	 */
	@Override
	public void documentDeleteProperties(Object documentId, List<String> propertyKeys) {
		this.documentHelper.documentDeleteProperties(documentId, propertyKeys);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public String documentGetProperty(Object documentId, String propertyKey) {
		return this.documentHelper.documentGetProperty(documentId, propertyKey);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperties(java.lang.Object, java.util.Map)
	 */
	@Override
	public Map<String, String> documentGetProperties(Object documentId, Map<?, ?> kv) {
		return this.documentHelper.documentGetProperties(documentId, kv);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult documentGetFiles(Object documentId, Map<?, ?> kv, List<?> attributes) {
		return this.documentHelper.documentGetFiles(documentId, kv, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetAllFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult documentGetAllFiles(Object documentId, Map<?, ?> kv, List<?> attributes) throws OntimizeJEERuntimeException {
		return this.documentHelper.documentGetAllFiles(documentId, kv, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#getRelatedDocument(java.lang.Object)
	 */
	@Override
	public EntityResult getRelatedDocument(Object documentId) {
		return this.documentHelper.getRelatedDocument(documentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#setRelatedDocuments(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setRelatedDocuments(Object masterDocumentId, Object childDocumentId) {
		this.documentHelper.setRelatedDocuments(masterDocumentId, childDocumentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryGetForDocument(java.lang.Object, java.util.List)
	 */
	@Override
	public DMSCategory categoryGetForDocument(Object documentId, List<?> attributes) throws OntimizeJEERuntimeException {
		return this.categoryHelper.categoryGetForDocument(documentId, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryInsert(java.lang.String, java.lang.Object, java.util.Map)
	 */
	@Override
	public Object categoryInsert(Object documentId, String name, Object parentCategoryId, Map<?, ?> otherData) throws OntimizeJEERuntimeException {
		return this.categoryHelper.categoryInsert(documentId, name, parentCategoryId, otherData);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryUpdate(java.lang.Object, java.util.Map)
	 */
	@Override
	public void categoryUpdate(Object categoryId, Map<?, ?> av) throws OntimizeJEERuntimeException {
		this.categoryHelper.categoryUpdate(categoryId, av);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryDelete(java.lang.Object)
	 */
	@Override
	public void categoryDelete(Object idCategory) throws OntimizeJEERuntimeException {
		this.categoryHelper.categoryDelete(idCategory);
	}

	@Override
	public void moveFilesToCategory(Object idCategory, List<Object> idFiles) throws OntimizeJEERuntimeException {
		this.fileHelper.moveFilesToCategory(idCategory, idFiles);
	}
}
