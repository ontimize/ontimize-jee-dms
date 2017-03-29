package com.ontimize.jee.server.services.dms;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.server.spring.namespace.OntimizeDMSConfiguration;

/**
 * The DMS service generic facade. This will delegate to configured engine : OntimizeDMSEngine ...
 */
@Service("DMSService")
@Lazy(value = true)
public class DMSServiceImpl implements IDMSService, ApplicationContextAware {

	/** The implementation. */
	private IDMSService	engine;

	/**
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	protected IDMSService getEngine() {
		CheckingTools.failIfNull(this.engine, "Not engine defined for dms.");
		return this.engine;
	}

	/**
	 * Sets the engine.
	 *
	 * @param engine
	 *            the engine
	 */
	public void setEngine(IDMSService engine) {
		this.engine = engine;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.setEngine(applicationContext.getBean(OntimizeDMSConfiguration.class).getDmsConfiguration().getEngine());
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContent(java.lang .Object)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public InputStream fileGetContent(Object fileId) {
		return this.getEngine().fileGetContent(fileId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public InputStream fileGetContentOfVersion(Object fileVersionId) {
		return this.getEngine().fileGetContentOfVersion(fileVersionId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileInsert(java.util.Map, java.io.InputStream)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public DocumentIdentifier fileInsert(Object documentId, Map<?, ?> av, InputStream is) {
		return this.getEngine().fileInsert(documentId, av, is);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object , java.util.Map, java.io.InputStream)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public DocumentIdentifier fileUpdate(Object fileId, Map<?, ?> attributesValues, InputStream is) {
		return this.getEngine().fileUpdate(fileId, attributesValues, is);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) {
		return this.getEngine().fileQuery(criteria, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void fileDelete(Object fileId) {
		this.getEngine().fileDelete(fileId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileVersionQuery(java.lang.Object, java.util.List)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public EntityResult fileVersionQuery(Object fileVersionId, List<?> attributes) throws OntimizeJEERuntimeException {
		return this.getEngine().fileVersionQuery(fileVersionId, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetVersions(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public EntityResult fileGetVersions(Object fileId, Map<?, ?> kv, List<?> attributes) {
		return this.getEngine().fileGetVersions(fileId, kv, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileRecoverPreviousVersion(java.lang.Object)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public void fileRecoverPreviousVersion(Object fileId, boolean acceptNotPreviousVersion) throws OntimizeJEERuntimeException {
		this.getEngine().fileRecoverPreviousVersion(fileId, acceptNotPreviousVersion);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentQuery(java.util.List, java.util.Map)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) {
		return this.getEngine().documentQuery(attributes, criteria);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentInsert(java.util.Map)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public DocumentIdentifier documentInsert(Map<?, ?> av) {
		return this.getEngine().documentInsert(av);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentUpdate(java.lang.Object, java.util.Map)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void documentUpdate(Object documentId, Map<?, ?> attributesValues) {
		this.getEngine().documentUpdate(documentId, attributesValues);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDelete(java.lang.Object)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void documentDelete(Object documentId) {
		this.getEngine().documentDelete(documentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentAddProperties(java.lang.Object, java.util.Map)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void documentAddProperties(Object documentId, Map<String, String> properties) {
		this.getEngine().documentAddProperties(documentId, properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDeleteProperties(java.lang.Object, java.util.List)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void documentDeleteProperties(Object documentId, List<String> propertyKeys) {
		this.getEngine().documentDeleteProperties(documentId, propertyKeys);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperty(java.lang.Object, java.lang.String)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public String documentGetProperty(Object documentId, String propertyKey) {
		return this.getEngine().documentGetProperty(documentId, propertyKey);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperties(java.lang.Object, java.util.Map)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public Map<String, String> documentGetProperties(Object documentId, Map<?, ?> kv) {
		return this.getEngine().documentGetProperties(documentId, kv);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public EntityResult documentGetFiles(Object documentId, Map<?, ?> kv, List<?> attributes) {
		return this.getEngine().documentGetFiles(documentId, kv, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetAllFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public EntityResult documentGetAllFiles(Object documentId, Map<?, ?> kv, List<?> attributes) throws OntimizeJEERuntimeException {
		return this.getEngine().documentGetAllFiles(documentId, kv, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#getRelatedDocument(java.lang.Object)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public EntityResult getRelatedDocument(Object documentId) {
		return this.getEngine().getRelatedDocument(documentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#setRelatedDocuments(java.lang.Object, java.lang.Object)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void setRelatedDocuments(Object masterDocumentId, Object childDocumentId) {
		this.getEngine().setRelatedDocuments(masterDocumentId, childDocumentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryGetForDocument(java.lang.Object, java.util.List)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Override
	public DMSCategory categoryGetForDocument(Object documentId, List<?> attributes) throws OntimizeJEERuntimeException {
		return this.getEngine().categoryGetForDocument(documentId, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryInsert(java.lang.String, java.lang.Object, java.util.Map)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Object categoryInsert(Object documentId,String name, Object parentCategoryId, Map<?, ?> otherData) throws OntimizeJEERuntimeException {
		return this.getEngine().categoryInsert(documentId, name, parentCategoryId, otherData);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryUpdate(java.lang.Object, java.util.Map)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void categoryUpdate(Object categoryId, Map<?, ?> av) throws OntimizeJEERuntimeException {
		this.getEngine().categoryUpdate(categoryId, av);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryDelete(java.lang.Object)
	 */
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void categoryDelete(Object idCategory) throws OntimizeJEERuntimeException {
		this.getEngine().categoryDelete(idCategory);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public void moveFilesToCategory(Object idCategory, List<Object> idFiles) throws OntimizeJEERuntimeException {
		this.getEngine().moveFilesToCategory(idCategory, idFiles);
	}
}
