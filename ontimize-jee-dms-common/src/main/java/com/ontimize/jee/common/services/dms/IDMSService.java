package com.ontimize.jee.common.services.dms;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;

/**
 * Servicio para la gestion de documentos, propiedades de documentos, ficheros y versiones de ficheros.
 *
 */
public interface IDMSService {

	/**
	 * Obtiene el contenido de un fichero.
	 *
	 * @param fileId
	 *            the file id
	 * @return the input stream
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	InputStream fileGetContent(Object fileId) throws OntimizeJEERuntimeException;

	/**
	 * Inserta un fichero al documento indicado.
	 *
	 * @param documentId
	 *            the document id
	 * @param av
	 *            the av
	 * @param file
	 *            the file
	 * @return el identificador del fichero insertado
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	DocumentIdentifier fileInsert(Object documentId, Map<?, ?> av, InputStream file) throws OntimizeJEERuntimeException;

	/**
	 * Búsqueda sobre el repositorio de ficheros.
	 *
	 * @param criteria
	 *            the criteria
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) throws OntimizeJEERuntimeException;

	/**
	 * Elimina un fichero.
	 *
	 * @param fileId
	 *            the file id
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void fileDelete(Object fileId) throws OntimizeJEERuntimeException;

	/**
	 * Actualiza un fichero. Si file != null, se crea una nueva versión
	 *
	 * @param fileId
	 *            the file id
	 * @param attributesValues
	 *            the attributes values
	 * @param file
	 *            the file
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	DocumentIdentifier fileUpdate(Object fileId, Map<?, ?> attributesValues, InputStream file) throws OntimizeJEERuntimeException;

	/**
	 * Obtiene las versiones de un fichero.
	 *
	 * @param fileId
	 *            the file id
	 * @param kv
	 *            the kv
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	EntityResult fileGetVersions(Object fileId, Map<?, ?> kv, List<?> attributes) throws OntimizeJEERuntimeException;

	/**
	 * Obtiene los datos de una versi�n concreta de un fichero.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	EntityResult fileVersionQuery(Object fileVersionId, List<?> attributes) throws OntimizeJEERuntimeException;

	/**
	 * Obtiene una versi�n dada de un fichero.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @return the input stream
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	InputStream fileGetContentOfVersion(Object fileVersionId) throws OntimizeJEERuntimeException;

	/**
	 * Delete last version of this file and mark previous as current active. If no previous version available, operation must be aborted and error
	 * must be raised.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @param acceptNotPreviousVersion
	 *            if false and not previous version located (for instance the only one) exception will be raise, else file will continue without any
	 *            version
	 * @return
	 * @throws OntimizeJEERuntimeException
	 */
	void fileRecoverPreviousVersion(Object fileId, boolean acceptNotPreviousVersion) throws OntimizeJEERuntimeException;

	/**
	 * Consulta documentos en base a un criterio.
	 *
	 * @param attributes
	 *            the attributes
	 * @param criteria
	 *            the criteria
	 * @return the entity result
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) throws OntimizeJEERuntimeException;

	/**
	 * Inserta un documento.
	 *
	 * @param av
	 *            the av
	 * @return el id del documento insertado
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	DocumentIdentifier documentInsert(Map<?, ?> av) throws OntimizeJEERuntimeException;

	/**
	 * Actualiza un documento.
	 *
	 * @param documentId
	 *            the document id
	 * @param attributesValues
	 *            the attributes values
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void documentUpdate(Object documentId, Map<?, ?> attributesValues) throws OntimizeJEERuntimeException;

	/**
	 * Elimina un documento.
	 *
	 * @param documentId
	 *            the document id
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void documentDelete(Object documentId) throws OntimizeJEERuntimeException;

	/**
	 * Añade propiedades a un documento.
	 *
	 * @param documentId
	 *            the document id
	 * @param properties
	 *            the properties
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void documentAddProperties(Object documentId, Map<String, String> properties) throws OntimizeJEERuntimeException;

	/**
	 * Elimina propiedades de un documento.
	 *
	 * @param documentId
	 *            the document id
	 * @param propertyKeys
	 *            the property keys
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void documentDeleteProperties(Object documentId, List<String> propertyKeys) throws OntimizeJEERuntimeException;

	/**
	 * Obtiene una propiedad de un documento.
	 *
	 * @param documentId
	 *            the document id
	 * @param propertyKey
	 *            the property key
	 * @return the string
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	String documentGetProperty(Object documentId, String propertyKey) throws OntimizeJEERuntimeException;

	/**
	 * Otiene las propiedades de un documento.
	 *
	 * @param documentId
	 *            the document id
	 * @param kv
	 *            the kv
	 * @return the map
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	Map<String, String> documentGetProperties(Object documentId, Map<?, ?> kv) throws OntimizeJEERuntimeException;

	/**
	 * Obtiene los ficheros de un documento. Por defecto s�lo devuelve los ficheros/versiones activos
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
	EntityResult documentGetFiles(Object documentId, Map<?, ?> kv, List<?> attributes) throws OntimizeJEERuntimeException;

	/**
	 * Obtiene TODOS los ficheros de un documento.
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
	EntityResult documentGetAllFiles(Object documentId, Map<?, ?> kv, List<?> attributes) throws OntimizeJEERuntimeException;

	/**
	 * Obtiene los documentos relacionados con un documento.
	 *
	 * @param documentId
	 *            the document id
	 * @return the related document
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	EntityResult getRelatedDocument(Object documentId) throws OntimizeJEERuntimeException;

	/**
	 * Establece una relación entre dos documentos.
	 *
	 * @param documentId
	 *            the document id
	 * @param otherDocumentId
	 *            the other document id
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void setRelatedDocuments(Object documentId, Object otherDocumentId) throws OntimizeJEERuntimeException;

	/**
	 * Category get for document.
	 *
	 * @param documentId
	 *            the document id
	 * @param attributes
	 *            the attributes
	 * @return the DMS category
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	DMSCategory categoryGetForDocument(Object documentId, List<?> attributes) throws OntimizeJEERuntimeException;

	/**
	 * Category insert.
	 *
	 * @param documentId
	 *            the document id
	 * @param name
	 *            the name
	 * @param parentCategoryId
	 *            the parent category id
	 * @param otherData
	 *            the other data
	 * @return the object
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	Object categoryInsert(Object documentId, String name, Object parentCategoryId, Map<?, ?> otherData) throws OntimizeJEERuntimeException;

	/**
	 * Category update.
	 *
	 * @param idCategory
	 *            the id category
	 * @param av
	 *            the av
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void categoryUpdate(Object idCategory, Map<?, ?> av) throws OntimizeJEERuntimeException;

	/**
	 * Category delete.
	 *
	 * @param idCategory
	 *            the id category
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void categoryDelete(Object idCategory) throws OntimizeJEERuntimeException;

	/**
	 * Move files to cagetory.
	 *
	 * @param idCategory
	 *            the id category
	 * @param idFiles
	 *            the id files
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	void moveFilesToCategory(Object idCategory, List<Object> idFiles) throws OntimizeJEERuntimeException;
}
