package com.ontimize.jee.server.services.dms;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileVersionDao;
import com.ontimize.jee.server.spring.namespace.OntimizeDMSConfiguration;

@Component
@Lazy(value = true)
public class DMSServiceFileHelper {

	@Autowired
	DefaultOntimizeDaoHelper				daoHelper;

	/** The Constant logger. */
	private static final Logger				logger				= LoggerFactory.getLogger(DMSServiceFileHelper.class);

	/** The path name id formatter. */
	private static DecimalFormat			pathNameIdFormatter	= new DecimalFormat("#");

	/** The ontimize configuration. */
	@Autowired
	protected OntimizeDMSConfiguration ontimizeDMSConfiguration;

	/** The document file version dao. */
	@Autowired
	protected IDMSDocumentFileVersionDao	documentFileVersionDao;
	/** The document file dao. */
	@Autowired
	protected IDMSDocumentFileDao			documentFileDao;

	/**
	 * File get content.
	 *
	 * @param fileId
	 *            the file id
	 * @return the input stream
	 */
	public InputStream fileGetContent(Object fileId) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		Map<String, Object> kv = new HashMap<String, Object>();
		kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		kv.put(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, OntimizeDMSEngine.ACTIVE);
		EntityResult res = this.daoHelper.query(this.documentFileVersionDao, kv, Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION }));
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
		Object versionId = res.getRecordValues(0).get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		return this.fileGetContentOfVersion(versionId);
	}

	/**
	 * File get content of version.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @return the input stream
	 */
	public InputStream fileGetContentOfVersion(Object fileVersionId) {
		Path file = this.getPhysicalFileFor(fileVersionId);
		try {
			return Files.newInputStream(file);
		} catch (IOException e) {
			throw new OntimizeJEERuntimeException(e);
		}
	}

	/**
	 * File insert.
	 *
	 * @param documentId
	 *            the document id
	 * @param av
	 *            the av
	 * @param is
	 *            the is
	 * @return the object
	 */
	public DocumentIdentifier fileInsert(Object documentId, Map<?, ?> av, InputStream is) {
		String fileName = (String) av.get(DMSNaming.DOCUMENT_FILE_NAME);
		CheckingTools.failIfNull(fileName, DMSNaming.ERROR_FILE_NAME_MANDATORY);
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);

		// insertamos en la tabla de ficheros
		Map<Object, Object> avFile = new HashMap<>();
		avFile.putAll(av);// Pass other columns (extended implementations)
		avFile.put(DMSNaming.DOCUMENT_FILE_NAME, fileName);
		avFile.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, documentId);
		EntityResult res = this.daoHelper.insert(this.documentFileDao, avFile);
		Object fileId = res.get(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		CheckingTools.failIfNull(fileName, DMSNaming.ERROR_ERROR_CREATING_FILE);

		// insertamos en las versiones
		Object fileVersionId = this.createNewVersionForFile(fileId, av, is);

		DocumentIdentifier result = new DocumentIdentifier(documentId, fileId, fileVersionId);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object , java.util.Map, java.io.InputStream)
	 */
	/**
	 * File update.
	 *
	 * @param fileId
	 *            the file id
	 * @param attributesValues
	 *            the attributes values
	 * @param is
	 *            the is
	 */
	public DocumentIdentifier fileUpdate(Object fileId, Map<?, ?> attributesValues, InputStream is) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);

		// si el inputstream es nulo actualizamos los campos
		if (is == null) {
			// Obtenemos el id de la versi�n actual
			Map<String, Object> kv = new HashMap<>();
			kv.put(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, OntimizeDMSEngine.ACTIVE);
			kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
			EntityResult er = this.daoHelper.query(this.documentFileVersionDao, kv, Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION }));
			CheckingTools.failIf(er.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
			Object currentVersionId = er.getRecordValues(0).get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
			this.updateCurrentVersionAttributes(fileId, currentVersionId, attributesValues);

			return new DocumentIdentifier(null, fileId, currentVersionId);// FIXME Consider to catch documentId
		} else {
			// En este caso hay que crear una nueva versi�n
			// Si viene el nombre del fichero lo actualizamos en la tabla de ficheros
			String fileName = (String) attributesValues.remove(DMSNaming.DOCUMENT_FILE_NAME);
			if (fileName != null) {
				Map<String, Object> kv = new HashMap<>();
				kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
				Map<String, Object> avUpdate = new HashMap<>();
				avUpdate.put(DMSNaming.DOCUMENT_FILE_NAME, fileName);
				this.daoHelper.update(this.documentFileDao, avUpdate, kv);
			}
			Object versionId = this.createNewVersionForFile(fileId, attributesValues, is);

			return new DocumentIdentifier(null, fileId, versionId);// FIXME Consider to catch documentId
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
	 */
	/**
	 * File query.
	 *
	 * @param criteria
	 *            the criteria
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 */
	public EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) {
		// TODO anadir filtro de dueno?
		return this.daoHelper.query(this.documentFileDao, criteria, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
	 */
	/**
	 * File delete.
	 *
	 * @param fileId
	 *            the file id
	 */
	public void fileDelete(Object fileId) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		EntityResult res = this.fileGetVersions(fileId, new HashMap<>(), Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION }));
		Vector<?> fileVersionIds = (Vector<?>) res.get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);

		// borramos las versiones, sin borrar los ficheros
		List<Path> toDelete = this.deleteFileVersionsWithoutDeleteFiles(fileId, fileVersionIds);

		// borramos el fichero
		Map<String, Object> kvFile = new HashMap<>();
		kvFile.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		this.daoHelper.delete(this.documentFileDao, kvFile);
		// si todo fue bien y no se va a hacer rollback, borramos
		for (Path file : toDelete) {
			FileTools.deleteQuitely(file);
		}
	}

	/**
	 * File version query.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	public EntityResult fileVersionQuery(Object fileVersionId, List<?> attributes) throws OntimizeJEERuntimeException {
		HashMap<String, Object> kv = new HashMap<String, Object>();
		kv.put(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, fileVersionId);
		return this.daoHelper.query(this.documentFileVersionDao, kv, attributes);
	}

	/**
	 * File get versions.
	 *
	 * @param fileId
	 *            the file id
	 * @param kv
	 *            the kv
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 */
	public EntityResult fileGetVersions(Object fileId, Map<?, ?> kv, List<?> attributes) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		((Map<Object, Object>) kv).put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		return this.daoHelper.query(this.documentFileVersionDao, kv, attributes);
	}

	/**
	 * File - revocer previous version
	 *
	 * @param fileId
	 *            the file id
	 * @param acceptNotPreviousVersion
	 * @return
	 */
	public void fileRecoverPreviousVersion(Object fileId, boolean acceptNotPreviousVersion) {
		EntityResult availableVersions = this.fileGetVersions(fileId, new HashMap(), Arrays.asList(
				new String[] { DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, DMSNaming.DOCUMENT_FILE_VERSION_VERSION, DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE }));
		if ((!acceptNotPreviousVersion && (availableVersions == null)) || (availableVersions.calculateRecordNumber() < 2)) {
			throw new OntimizeJEERuntimeException("E_NOT_AVAILABLE_VERSIONS_TO_RECOVER");
		}

		// Detect current version and previous
		Vector vId = (Vector) availableVersions.get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		Vector vActive = (Vector) availableVersions.get(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE);
		Vector<Number> vVersion = (Vector<Number>) availableVersions.get(DMSNaming.DOCUMENT_FILE_VERSION_VERSION);
		int currentVersionIdx = vActive.indexOf(OntimizeDMSEngine.ACTIVE);
		if (currentVersionIdx < 0) {
			throw new OntimizeJEERuntimeException("E_NOT_CURRENT_ACTIVE_VERSION");
		}

		long currentVersion = vVersion.get(currentVersionIdx).longValue();
		// Look for max previous version of current (usually currentVersion-1)
		int previousVersionIdx = -1;
		long previousVersion = 0;
		int idx = 0;
		for (Number version : vVersion) {
			if ((version.longValue() > previousVersion) && (version.longValue() < currentVersion)) {
				previousVersion = version.longValue();
				previousVersionIdx = idx;
			}
			idx++;
		}
		if ((previousVersionIdx < 0) && !acceptNotPreviousVersion) {
			throw new OntimizeJEERuntimeException("E_INVALID_PREVIOUS_VERSION");
		}

		if (previousVersionIdx >= 0) {
			// Mark previous version as current active
			HashMap kv = new HashMap<>();
			kv.put(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, vId.get(previousVersionIdx));
			Map<String, Object> avUpdate = new HashMap<>();
			avUpdate.put(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, OntimizeDMSEngine.ACTIVE);
			this.daoHelper.update(this.documentFileVersionDao, avUpdate, kv);
		}

		// Delete last version
		Path file = this.deleteFileVersionWithoutDeleteFile(fileId, vId.get(currentVersionIdx));
		FileTools.deleteQuitely(file);
	}

	/* ############## */
	/* HELPER METHODS */
	/* ############## */

	/**
	 * Obtiene la ruta física a un fichero.
	 *
	 * @param idVersion
	 *            the id version
	 * @return the physical file for
	 */
	protected Path getPhysicalFileFor(Object idVersion) {
		CheckingTools.failIfNull(idVersion, DMSNaming.ERROR_FILE_VERSION_ID_IS_MANDATORY);
		Map<String, Object> kvVersion = new HashMap<>();
		kvVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, idVersion);
		EntityResult res = this.daoHelper.query(this.documentFileVersionDao, kvVersion,
				Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_VERSION, DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE }));
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_VERSION_NOT_FOUND);
		Hashtable<?, ?> rv = res.getRecordValues(0);
		Object fileId = rv.get(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);

		Map<String, Object> kvFile = new HashMap<>();
		kvFile.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		res = this.daoHelper.query(this.documentFileDao, kvFile, Arrays.asList(new String[] { DMSNaming.DOCUMENT_ID_DMS_DOCUMENT }), "allfiles");
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_NOT_FOUND);

		rv = res.getRecordValues(0);
		Object documentId = rv.get(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		return this.getPhysicalFileFor(documentId, fileId, idVersion);
	}

	/**
	 * Obtiene la ruta física a un fichero.
	 *
	 * @param documentId
	 *            the document id
	 * @param fileId
	 *            the file id
	 * @param versionId
	 *            the version id
	 * @return the physical file for
	 */
	protected Path getPhysicalFileFor(Object documentId, Object fileId, Object versionId) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_NO_FILE_VERSION);
		String id = DMSServiceFileHelper.pathNameIdFormatter.format(fileId);
		String version = DMSServiceFileHelper.pathNameIdFormatter.format(versionId);
		return this.getDocumentBasePathForDocumentId(documentId).resolve(id + "_" + version);
	}

	/**
	 * Gets the document base path for document id.
	 *
	 * @param documentId
	 *            the document id
	 * @return the document base path for document id
	 */
	protected Path getDocumentBasePathForDocumentId(Object documentId) {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		return this.getDocumentsBasePath().resolve(DMSServiceFileHelper.pathNameIdFormatter.format(documentId));
	}

	/**
	 * Gets the documents base path.
	 *
	 * @return the documents base path
	 */
	protected Path getDocumentsBasePath() {
		Path path = ((OntimizeDMSEngine) this.ontimizeDMSConfiguration.getDmsConfiguration().getEngine()).getDocumentsBasePath();
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				throw new OntimizeJEERuntimeException("Could not create base folder for DMS", e);
			}
		}
		return path;
	}

	/**
	 * Delete file versions without delete files.
	 *
	 * @param fileId
	 *            the file id
	 * @param fileVersionIds
	 *            the file version ids
	 * @return the list
	 */
	public List<Path> deleteFileVersionsWithoutDeleteFiles(Object fileId, List<?> fileVersionIds) {
		List<Path> toDelete = new ArrayList<>(fileVersionIds.size());
		for (Object idVersion : fileVersionIds) {
			Path deleteFileVersion = this.deleteFileVersionWithoutDeleteFile(fileId, idVersion);
			toDelete.add(deleteFileVersion);
		}
		return toDelete;
	}

	/**
	 * Delete a file version.
	 *
	 * @param fileId
	 *            the file id
	 * @param idVersion
	 *            the id version
	 * @return the file that need to be deleted
	 */
	protected Path deleteFileVersionWithoutDeleteFile(Object fileId, Object idVersion) {
		Map<String, Object> kvVersion = new HashMap<>();
		Path physicalFileFor = this.getPhysicalFileFor(idVersion);
		kvVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, idVersion);
		this.daoHelper.delete(this.documentFileVersionDao, kvVersion);
		return physicalFileFor;
	}

	/**
	 * Update current version attributes.
	 *
	 * @param fileId
	 *            the file id
	 * @param currentVersionId
	 *            the current version id
	 * @param attributesValues
	 *            the attributes values
	 */
	protected void updateCurrentVersionAttributes(Object fileId, Object currentVersionId, Map<?, ?> attributesValues) {
		List<String> columnsDocumentFile = Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_NAME });
		List<String> columnsDocumentFileVersion = Arrays.asList(
				new String[] { DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH, DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION, DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL });
		Map<String, Object> avFile = new HashMap<>();
		Map<String, Object> avVersion = new HashMap<>();
		for (Entry<?, ?> entry : attributesValues.entrySet()) {
			if (columnsDocumentFile.contains(entry.getKey())) {
				avFile.put((String) entry.getKey(), entry.getValue());
			}
			if (columnsDocumentFileVersion.contains(entry.getKey())) {
				avVersion.put((String) entry.getKey(), entry.getValue());
			}
		}
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		this.daoHelper.update(this.documentFileDao, avFile, kv);

		kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, currentVersionId);
		this.daoHelper.update(this.documentFileVersionDao, avVersion, kv);
	}

	/**
	 * Ensures to deprecate current ACTIVE version of file (if exists) and return next DEFAULT AUTOMATIC version.
	 *
	 * @param fileId
	 *            the file id
	 * @return the current file version and deprecate
	 */
	protected Number getCurrentFileVersionAndDeprecate(Object fileId) {
		Number fileVersion = Long.valueOf(1);

		// cogemos la referencia a la versi�n actual
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		EntityResult er = this.fileQuery(kv, Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, DMSNaming.DOCUMENT_FILE_VERSION_VERSION }));
		CheckingTools.failIf(er.calculateRecordNumber() > 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);

		// Si existe la marcamos como no activa y sumamos 1 a la versi�n que vamos a insertar
		if (er.calculateRecordNumber() == 1) {
			Map<?, ?> record = er.getRecordValues(0);
			Object oldVersionId = record.get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
			if (oldVersionId != null) {
				Number oldVersion = (Number) record.get(DMSNaming.DOCUMENT_FILE_VERSION_VERSION);
				fileVersion = Long.valueOf(oldVersion.longValue() + 1);

				kv = new HashMap<>();
				kv.put(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, oldVersionId);
				Map<String, Object> avUpdate = new HashMap<>();
				avUpdate.put(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, OntimizeDMSEngine.INACTIVE);
				this.daoHelper.update(this.documentFileVersionDao, avUpdate, kv);
			}
		}
		return fileVersion;
	}

	/**
	 * Move files to a new idDmsDoc. It ensures to change the reference in database and move files to the new directory
	 *
	 * @param idDmsDocFiles
	 * @param idDmsDoc
	 * @throws OntimizeJEERuntimeException
	 */
	public void moveFilesToDoc(List<Object> idDmsDocFiles, Object idDmsDoc) throws OntimizeJEERuntimeException {
		// Comprobamos par�metros
		if ((idDmsDocFiles == null) || (idDmsDocFiles.size() <= 0)) {
			throw new OntimizeJEERuntimeException("ErrorNoDocFiles");
		}
		if ((idDmsDoc == null) || (idDmsDoc instanceof NullValue)) {
			throw new OntimizeJEERuntimeException("ErrorNoIdDmsDoc");
		}
		Map<String, Object> av = new HashMap<>();
		av.put("ID_DMS_DOC", idDmsDoc);
		Map<String, Object> kv = new HashMap<>();

		for (final Object idDmsDocFile : idDmsDocFiles) {
			// Cogemos todas las versiones del fichero que vamos a mover
			Object documentId = this.getDocumentIdForFile(idDmsDocFile);
			Path pathIdDMSDocFile = this.getDocumentBasePathForDocumentId(documentId);
			File dir = pathIdDMSDocFile.toFile();
			File[] foundFiles = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(idDmsDocFile.toString());
				}
			});
			// Movemos los ficheros al nuevo directorio determinado por el par�metros idDmsDoc
			for (File file : foundFiles) {
				try {
					FileUtils.moveToDirectory(file, this.getDocumentBasePathForDocumentId(idDmsDoc).toFile(), true);
				} catch (IOException e) {
					throw new OntimizeJEERuntimeException(e);
				}
			}
			// Actualizamos las referencias en base de datos
			kv.put("ID_DMS_DOC_FILE", idDmsDocFile);
			this.daoHelper.update(this.documentFileDao, av, kv);
		}
	}

	/**
	 * Creates the new version for file.
	 *
	 * @param fileId
	 *            the file id
	 * @param attributes
	 *            the attributes
	 * @param is
	 *            the is
	 * @return the Object
	 */
	protected Object createNewVersionForFile(Object fileId, Map<?, ?> attributes, InputStream is) {
		Object documentId = this.getDocumentIdForFile(fileId);
		Object fileVersion = null;

		// Si recibimos la versi�n por parte del usuario utilizamos esa, siempre y cuando no exista ya
		if (attributes.containsKey(DMSNaming.DOCUMENT_FILE_VERSION_VERSION)) {
			fileVersion = attributes.get(DMSNaming.DOCUMENT_FILE_VERSION_VERSION);
			// Check if conflict
			Map<Object, Object> kvCheck = new HashMap<>();
			kvCheck.put(DMSNaming.DOCUMENT_FILE_VERSION_VERSION, fileVersion);
			EntityResult resVersions = this.fileGetVersions(fileId, kvCheck, Arrays.asList(new String[] { DMSNaming.DOCUMENT_FILE_VERSION_VERSION }));
			CheckingTools.failIf(resVersions.calculateRecordNumber() > 0, DMSNaming.ERROR_VERSION_ALREADY_EXISTS);
		} else {
			fileVersion = this.getCurrentFileVersionAndDeprecate(fileId);
		}

		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		CheckingTools.failIfNull(is, DMSNaming.ERROR_INPUTSTREAM_IS_MANDATORY);
		Map<String, Object> avVersion = new HashMap<>();
		avVersion.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		avVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE, new Date());
		// TODO pendiente de tener la informaci�n en el userinfo
		avVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER, Integer.valueOf(1));
		avVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION, attributes.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION));
		avVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH, attributes.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH));
		avVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE,
				attributes.containsKey(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE) ? attributes.get(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE) : OntimizeDMSEngine.ACTIVE);
		avVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_VERSION, fileVersion);
		if (attributes.containsKey(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL)) {
			avVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL, attributes.get(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL));
		}
		EntityResult resVersion = this.daoHelper.insert(this.documentFileVersionDao, avVersion);
		Object versionId = resVersion.get(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_CREATING_FILE_VERSION);
		Path file = this.getPhysicalFileFor(documentId, fileId, versionId);
		try {
			Path tmpFile = Files.createTempFile("dms", "tmp");
			CheckingTools.failIf(Files.exists(file), DMSNaming.ERROR_FILE_ALREADY_EXISTS);
			// TODO ver si es necesario hacer esto antes para no ocupar memoria,
			// aunque las operaciones anteriores deber�an ser inmediatas
			long time = System.currentTimeMillis();
			try (OutputStream output = Files.newOutputStream(tmpFile)) {
				IOUtils.copy(is, output);
			}
			Files.createDirectories(file.getParent());
			try {
				Files.move(tmpFile, file);
			} catch (IOException ex) {
				DMSServiceFileHelper.logger.warn("Move option not work, using copy option: {}", ex.getMessage());
				Files.copy(tmpFile, file);
				FileTools.deleteQuitely(tmpFile);
			}
			// update filesize
			long fileSize = Files.size(file);
			Map<String, Object> kvVersion = new HashMap<String, Object>();
			kvVersion.put(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, versionId);
			Map<String, Object> avVersionSize = new HashMap<String, Object>();
			avVersionSize.put(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, fileSize);
			this.daoHelper.update(this.documentFileVersionDao, avVersionSize, kvVersion);

			DMSServiceFileHelper.logger.debug("Time copying file: {}", (System.currentTimeMillis() - time));
			return versionId;
		} catch (Exception e) {
			FileTools.deleteQuitely(file);
			throw new OntimizeJEERuntimeException(e);
		}
	}

	/**
	 * Gets the document id for file.
	 *
	 * @param fileId
	 *            the file id
	 * @return the document id for file
	 */
	protected Object getDocumentIdForFile(Object fileId) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, fileId);
		EntityResult res = this.daoHelper.query(this.documentFileDao, kv, Arrays.asList(new String[] { DMSNaming.DOCUMENT_ID_DMS_DOCUMENT }), "allfiles");
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_DOCUMENT_NOT_FOUND);
		return res.getRecordValues(0).get(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
	}

	public void moveFilesToCategory(Object idCategory, List<Object> idFiles) {
		if ((idFiles == null) || idFiles.isEmpty()) {
			return;
		}
		Map<String, Object> av = new HashMap<>();
		av.put(DMSNaming.CATEGORY_ID_CATEGORY, idCategory == null ? new NullValue() : idCategory);
		Map<String, Object> kv = new HashMap<>();
		kv.put(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, new SearchValue(SearchValue.IN, new Vector<>(idFiles)));
		this.documentFileDao.unsafeUpdate(av, kv);
	}
}