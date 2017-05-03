package com.ontimize.jee.server.services.dms;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileVersionDao;
import com.ontimize.jee.server.spring.namespace.OntimizeDMSConfiguration;

@Component
@Lazy(value = true)
public class DMSServiceFileHelper extends AbstractDMSServiceHelper {

	@Autowired
	DefaultOntimizeDaoHelper				daoHelper;

	/** The Constant logger. */
	private static final Logger				logger				= LoggerFactory.getLogger(DMSServiceFileHelper.class);

	/** The path name id formatter. */
	private static DecimalFormat			pathNameIdFormatter	= new DecimalFormat("#");

	/** The ontimize configuration. */
	@Autowired
	protected OntimizeDMSConfiguration		ontimizeDMSConfiguration;

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
	public InputStream fileGetContent(Serializable fileId) {
		Serializable versionId = this.getCurrentFileVersion(fileId);
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		return this.fileGetContentOfVersion(versionId);
	}

	/**
	 * Returns the file physical path.
	 *
	 * @param fileId
	 * @return the file path
	 */
	public Path fileGetPath(Serializable fileId) {
		Serializable versionId = this.getCurrentFileVersion(fileId);
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		return this.fileGetPathOfVersion(versionId);
	}

	/**
	 * File get content of version.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @return the input stream
	 */
	public InputStream fileGetContentOfVersion(Serializable fileVersionId) {
		Path file = this.getPhysicalFileFor(fileVersionId);
		try {
			return Files.newInputStream(file);
		} catch (IOException e) {
			throw new OntimizeJEERuntimeException(e);
		}
	}

	/**
	 * Returns the file physical path.
	 *
	 * @param fileVersionId
	 * @return the file path
	 */
	public Path fileGetPathOfVersion(Serializable fileVersionId) {
		return this.getPhysicalFileFor(fileVersionId);
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
	public DocumentIdentifier fileInsert(Serializable documentId, Map<?, ?> av, InputStream is) {
		String fileName = (String) av.get(this.getColumnHelper().getFileNameColumn());
		CheckingTools.failIfNull(fileName, DMSNaming.ERROR_FILE_NAME_MANDATORY);
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);

		// insertamos en la tabla de ficheros
		Map<Object, Object> avFile = new HashMap<>();
		avFile.putAll(av);// Pass other columns (extended implementations)
		avFile.put(this.getColumnHelper().getFileNameColumn(), fileName);
		avFile.put(this.getColumnHelper().getDocumentIdColumn(), documentId);
		EntityResult res = this.daoHelper.insert(this.documentFileDao, avFile);
		Serializable fileId = (Serializable) res.get(this.getColumnHelper().getFileIdColumn());
		CheckingTools.failIfNull(fileName, DMSNaming.ERROR_ERROR_CREATING_FILE);

		// insertamos en las versiones
		Serializable fileVersionId = this.createNewVersionForFile(fileId, av, is);

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
	public DocumentIdentifier fileUpdate(Serializable fileId, Map<?, ?> attributesValues, InputStream is) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);

		// si el inputstream es nulo actualizamos los campos
		if (is == null) {
			// Obtenemos el id de la versión actual
			Map<String, Object> kv = new HashMap<>();
			kv.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
			kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
			EntityResult er = this.daoHelper.query(this.documentFileVersionDao, kv, EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()));
			CheckingTools.failIf(er.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
			Serializable currentVersionId = (Serializable) er.getRecordValues(0).get(this.getColumnHelper().getVersionIdColumn());
			this.updateCurrentVersionAttributes(fileId, currentVersionId, attributesValues);

			return new DocumentIdentifier(null, fileId, currentVersionId);// FIXME Consider to catch documentId
		} else {
			// En este caso hay que crear una nueva versión
			// Si viene el nombre del fichero lo actualizamos en la tabla de ficheros
			String fileName = (String) attributesValues.remove(this.getColumnHelper().getFileNameColumn());
			if (fileName != null) {
				Map<String, Object> kv = new HashMap<>();
				kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
				Map<String, Object> avUpdate = new HashMap<>();
				avUpdate.put(this.getColumnHelper().getFileNameColumn(), fileName);
				this.daoHelper.update(this.documentFileDao, avUpdate, kv);
			}
			Serializable versionId = this.createNewVersionForFile(fileId, attributesValues, is);

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
	public void fileDelete(Serializable fileId) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		EntityResult res = this.fileGetVersions(fileId, new HashMap<>(), EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()));
		List<Serializable> fileVersionIds = (List<Serializable>) res.get(this.getColumnHelper().getVersionIdColumn());

		// borramos las versiones, sin borrar los ficheros
		List<Path> toDelete = this.deleteFileVersionsWithoutDeleteFiles(fileId, fileVersionIds);

		// borramos el fichero
		Map<String, Object> kvFile = new HashMap<>();
		kvFile.put(this.getColumnHelper().getFileIdColumn(), fileId);
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
	public EntityResult fileVersionQuery(Serializable fileVersionId, List<?> attributes) throws OntimizeJEERuntimeException {
		HashMap<String, Object> kv = new HashMap<String, Object>();
		kv.put(this.getColumnHelper().getVersionIdColumn(), fileVersionId);
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
	public EntityResult fileGetVersions(Serializable fileId, Map<?, ?> kv, List<?> attributes) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		((Map<Object, Object>) kv).put(this.getColumnHelper().getFileIdColumn(), fileId);
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
	public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion) {
		EntityResult availableVersions = this.fileGetVersions(fileId, new HashMap(), this.getColumnHelper().getVersionColumns());
		if ((!acceptNotPreviousVersion && (availableVersions == null)) || (availableVersions.calculateRecordNumber() < 2)) {
			throw new OntimizeJEERuntimeException("E_NOT_AVAILABLE_VERSIONS_TO_RECOVER");
		}

		// Detect current version and previous
		List<Serializable> vId = (List) availableVersions.get(this.getColumnHelper().getVersionIdColumn());
		List<Serializable> vActive = (List) availableVersions.get(this.getColumnHelper().getVersionActiveColumn());
		List<Number> vVersion = (List<Number>) availableVersions.get(this.getColumnHelper().getVersionVersionColumn());
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
			kv.put(this.getColumnHelper().getVersionIdColumn(), vId.get(previousVersionIdx));
			Map<String, Object> avUpdate = new HashMap<>();
			avUpdate.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
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
	 * Obtiene la ruta fÃ­sica a un fichero.
	 *
	 * @param idVersion
	 *            the id version
	 * @return the physical file for
	 */
	protected Path getPhysicalFileFor(Serializable idVersion) {
		CheckingTools.failIfNull(idVersion, DMSNaming.ERROR_FILE_VERSION_ID_IS_MANDATORY);
		Map<String, Object> kvVersion = new HashMap<>();
		kvVersion.put(this.getColumnHelper().getVersionIdColumn(), idVersion);
		EntityResult res = this.daoHelper.query(this.documentFileVersionDao, kvVersion,
				EntityResultTools.attributes(this.getColumnHelper().getVersionVersionColumn(), this.getColumnHelper().getFileIdColumn()));
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_VERSION_NOT_FOUND);
		Hashtable<?, ?> rv = res.getRecordValues(0);
		Serializable fileId = (Serializable) rv.get(this.getColumnHelper().getFileIdColumn());

		Map<String, Object> kvFile = new HashMap<>();
		kvFile.put(this.getColumnHelper().getFileIdColumn(), fileId);
		res = this.daoHelper.query(this.documentFileDao, kvFile, EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn()), "allfiles");
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_NOT_FOUND);

		rv = res.getRecordValues(0);
		Serializable documentId = (Serializable) rv.get(this.getColumnHelper().getDocumentIdColumn());
		return this.getPhysicalFileFor(documentId, fileId, idVersion);
	}

	/**
	 * Obtiene la ruta fÃ­sica a un fichero.
	 *
	 * @param documentId
	 *            the document id
	 * @param fileId
	 *            the file id
	 * @param versionId
	 *            the version id
	 * @return the physical file for
	 */
	protected Path getPhysicalFileFor(Serializable documentId, Serializable fileId, Serializable versionId) {
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
	protected Path getDocumentBasePathForDocumentId(Serializable documentId) {
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
	public List<Path> deleteFileVersionsWithoutDeleteFiles(Serializable fileId, List<Serializable> fileVersionIds) {
		List<Path> toDelete = new ArrayList<>(fileVersionIds.size());
		for (Serializable idVersion : fileVersionIds) {
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
	protected Path deleteFileVersionWithoutDeleteFile(Serializable fileId, Serializable idVersion) {
		Map<String, Object> kvVersion = new HashMap<>();
		Path physicalFileFor = this.getPhysicalFileFor(idVersion);
		kvVersion.put(this.getColumnHelper().getVersionIdColumn(), idVersion);
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
	protected void updateCurrentVersionAttributes(Serializable fileId, Serializable currentVersionId, Map<?, ?> attributesValues) {
		// Split columns for FILE and for VERSION daos
		List<String> columnsDocumentFile = this.getColumnHelper().getFileColumns();
		List<String> columnsDocumentFileVersion = this.getColumnHelper().getVersionColumns();
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
		if (avFile.isEmpty() && avVersion.isEmpty()){
			throw new OntimizeJEERuntimeException("dms.E_NO_DATA_TO_UPDATE");
		}

		Map<String, Object> kv = new HashMap<>();
		kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
		this.daoHelper.update(this.documentFileDao, avFile, kv);

		kv = new HashMap<>();
		kv.put(this.getColumnHelper().getVersionIdColumn(), currentVersionId);
		this.daoHelper.update(this.documentFileVersionDao, avVersion, kv);
	}

	/**
	 * Returns current active version of a file
	 *
	 * @param fileId
	 *            the file id
	 * @return the current file version
	 */
	protected Serializable getCurrentFileVersion(Serializable fileId) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		Map<String, Object> kv = new HashMap<String, Object>();
		kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
		kv.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
		EntityResult res = this.daoHelper.query(this.documentFileVersionDao, kv, EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()));
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
		Serializable versionId = (Serializable) res.getRecordValues(0).get(this.getColumnHelper().getVersionIdColumn());
		return versionId;
	}

	/**
	 * Ensures to deprecate current ACTIVE version of file (if exists) and return next DEFAULT AUTOMATIC version.
	 *
	 * @param fileId
	 *            the file id
	 * @return the current file version and deprecate
	 */
	protected Number getCurrentFileVersionAndDeprecate(Serializable fileId) {
		Number fileVersion = Long.valueOf(1);

		// cogemos la referencia a la versión actual
		Map<String, Object> kv = new HashMap<>();
		kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
		EntityResult er = this.fileQuery(kv, EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn(), this.getColumnHelper().getVersionVersionColumn()));
		CheckingTools.failIf(er.calculateRecordNumber() > 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);

		// Si existe la marcamos como no activa y sumamos 1 a la versión que vamos a insertar
		if (er.calculateRecordNumber() == 1) {
			Map<?, ?> record = er.getRecordValues(0);
			Serializable oldVersionId = (Serializable) record.get(this.getColumnHelper().getVersionIdColumn());
			if (oldVersionId != null) {
				Number oldVersion = (Number) record.get(this.getColumnHelper().getVersionVersionColumn());
				fileVersion = Long.valueOf(oldVersion.longValue() + 1);

				kv = new HashMap<>();
				kv.put(this.getColumnHelper().getVersionIdColumn(), oldVersionId);
				Map<String, Object> avUpdate = new HashMap<>();
				avUpdate.put(this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.INACTIVE);
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
	public void moveFilesToDoc(List<Serializable> idDmsDocFiles, Serializable idDmsDoc) throws OntimizeJEERuntimeException {
		// Comprobamos parámetros
		if ((idDmsDocFiles == null) || (idDmsDocFiles.size() <= 0)) {
			throw new OntimizeJEERuntimeException("ErrorNoDocFiles");
		}
		if ((idDmsDoc == null) || (idDmsDoc instanceof NullValue)) {
			throw new OntimizeJEERuntimeException("ErrorNoIdDmsDoc");
		}
		Map<String, Object> av = new HashMap<>();
		av.put("ID_DMS_DOC", idDmsDoc);
		Map<String, Object> kv = new HashMap<>();

		for (final Serializable idDmsDocFile : idDmsDocFiles) {
			// Cogemos todas las versiones del fichero que vamos a mover
			Serializable documentId = this.getDocumentIdForFile(idDmsDocFile);
			Path pathIdDMSDocFile = this.getDocumentBasePathForDocumentId(documentId);
			File dir = pathIdDMSDocFile.toFile();
			File[] foundFiles = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(idDmsDocFile.toString());
				}
			});
			// Movemos los ficheros al nuevo directorio determinado por el parámetros idDmsDoc
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
	protected Serializable createNewVersionForFile(Serializable fileId, Map<?, ?> attributes, InputStream is) {
		Serializable documentId = this.getDocumentIdForFile(fileId);
		Serializable fileVersion = null;

		// Si recibimos la versión por parte del usuario utilizamos esa, siempre y cuando no exista ya
		if (attributes.containsKey(this.getColumnHelper().getVersionVersionColumn())) {
			fileVersion = (Serializable) attributes.get(this.getColumnHelper().getVersionVersionColumn());
			// Check if conflict
			Map<Object, Object> kvCheck = new HashMap<>();
			kvCheck.put(this.getColumnHelper().getVersionVersionColumn(), fileVersion);
			EntityResult resVersions = this.fileGetVersions(fileId, kvCheck, EntityResultTools.attributes(this.getColumnHelper().getVersionVersionColumn()));
			CheckingTools.failIf(resVersions.calculateRecordNumber() > 0, DMSNaming.ERROR_VERSION_ALREADY_EXISTS);
		} else {
			fileVersion = this.getCurrentFileVersionAndDeprecate(fileId);
		}

		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		CheckingTools.failIfNull(is, DMSNaming.ERROR_INPUTSTREAM_IS_MANDATORY);
		Map<String, Object> avVersion = new HashMap<>();
		avVersion.put(this.getColumnHelper().getFileIdColumn(), fileId);
		avVersion.put(this.getColumnHelper().getVersionAddedDateColumn(), new Date());
		avVersion.put(this.getColumnHelper().getVersionAddedUserColumn(), Integer.valueOf(1)); // TODO pendiente de tener la información en el userinfo
		avVersion.put(this.getColumnHelper().getVersionDescriptionColumn(), attributes.get(this.getColumnHelper().getVersionDescriptionColumn()));
		avVersion.put(this.getColumnHelper().getVersionPathColumn(), attributes.get(this.getColumnHelper().getVersionPathColumn()));
		avVersion.put(this.getColumnHelper().getVersionActiveColumn(), attributes.containsKey(this.getColumnHelper().getVersionActiveColumn()) ? attributes
				.get(this.getColumnHelper().getVersionActiveColumn()) : OntimizeDMSEngine.ACTIVE);
		avVersion.put(this.getColumnHelper().getVersionVersionColumn(), fileVersion);
		MapTools.safePut(avVersion, this.getColumnHelper().getVersionThumbnailColumn(), attributes.get(this.getColumnHelper().getVersionThumbnailColumn()));
		EntityResult resVersion = this.daoHelper.insert(this.documentFileVersionDao, avVersion);
		Serializable versionId = (Serializable) resVersion.get(this.getColumnHelper().getVersionIdColumn());
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_CREATING_FILE_VERSION);
		Path file = this.getPhysicalFileFor(documentId, fileId, versionId);
		try {
			Path tmpFile = Files.createTempFile("dms", "tmp");
			CheckingTools.failIf(Files.exists(file), DMSNaming.ERROR_FILE_ALREADY_EXISTS);
			// TODO ver si es necesario hacer esto antes para no ocupar memoria,
			// aunque las operaciones anteriores deberían ser inmediatas
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
			kvVersion.put(this.getColumnHelper().getVersionIdColumn(), versionId);
			Map<String, Object> avVersionSize = new HashMap<String, Object>();
			avVersionSize.put(this.getColumnHelper().getVersionSizeColumn(), fileSize);
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
	protected Serializable getDocumentIdForFile(Serializable fileId) {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		Map<String, Object> kv = new HashMap<>();
		kv.put(this.getColumnHelper().getFileIdColumn(), fileId);
		EntityResult res = this.daoHelper.query(this.documentFileDao, kv, EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn()), "allfiles");
		CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_DOCUMENT_NOT_FOUND);
		return (Serializable) res.getRecordValues(0).get(this.getColumnHelper().getDocumentIdColumn());
	}

	public void moveFilesToCategory(Serializable idCategory, List<Serializable> idFiles) {
		if ((idFiles == null) || idFiles.isEmpty()) {
			return;
		}
		Map<String, Object> av = new HashMap<>();
		av.put(this.getColumnHelper().getCategoryIdColumn(), idCategory == null ? new NullValue() : idCategory);
		Map<String, Object> kv = new HashMap<>();
		kv.put(this.getColumnHelper().getFileIdColumn(), new SearchValue(SearchValue.IN, new Vector<>(idFiles)));
		this.documentFileDao.unsafeUpdate(av, kv);
	}
}
