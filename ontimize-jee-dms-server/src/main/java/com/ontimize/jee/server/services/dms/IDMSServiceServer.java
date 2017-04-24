package com.ontimize.jee.server.services.dms;

import java.nio.file.Path;

import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.services.dms.IDMSService;

/**
 * Funcionalidades extendidas del DMS sólo disponibles desde el lado del servidor
 *
 */
public interface IDMSServiceServer extends IDMSService {

	/**
	 * Obtiene el path de un fichero.
	 *
	 * @param fileId
	 *            the file id
	 * @return the input stream
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	Path fileGetPath(Object fileId) throws OntimizeJEERuntimeException;


	/**
	 * Obtiene el path de una versión dada de un fichero.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @return the input stream
	 * @throws OntimizeJEERuntimeException
	 *             the ontimize jee runtime exception
	 */
	Path fileGetPathOfVersion(Object fileVersionId) throws OntimizeJEERuntimeException;

	/**
	 * Update settings.
	 */
	void updateSettings();

}
