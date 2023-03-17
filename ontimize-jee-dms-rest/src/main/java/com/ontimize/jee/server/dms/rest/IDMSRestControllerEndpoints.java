package com.ontimize.jee.server.dms.rest;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.rest.FileListParameter;
import com.ontimize.jee.server.rest.InsertParameter;
import com.ontimize.jee.server.rest.QueryParameter;
import com.ontimize.jee.server.rest.UpdateFileParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IDMSRestControllerEndpoints {

    /**
     * Get document files. Only active files/versions by default
     * @param workspaceId the work space identifier
     * @param queryParam the query parameters
     * @return
     */
    ResponseEntity<List<? extends OFile>> documentGetFiles( Integer workspaceId, QueryParameter queryParam );

    /**
     * Get file content
     * @param fileId the file identifier
     * @param response
     */
    void fileGetContent( Integer fileId, HttpServletResponse response );

    /**
     * Get files and folders compressed on a zip file
     * @param workspaceId the work space identifier
     * @param files the file list for downloading
     * @return
     */
    ResponseEntity<Map<String, String>> fileGetContent( Integer workspaceId, List<OFile> files );

    /**
     * Download the zip file provided stored in temporary files folder.
     * @param file the file name
     * @param response
     */
    void fileGetZip( String file, HttpServletResponse response );

    /**
     * Insert a file in the specified folder for the specified document
     * @param workspaceId the work space identifier
     * @param multipart the file
     * @param folderId the folder identifier
     * @return
     */
    ResponseEntity<DocumentIdentifier> fileInsert( Integer workspaceId, MultipartFile multipart, Object folderId );

    /**
     * Delete the files provided
     * @param workspaceId the work space identifier
     * @param deleteParameter
     * @return
     */
    ResponseEntity<Void> delete( Integer workspaceId, FileListParameter deleteParameter ) throws DmsException;

    /**
     * Inserts a folder in the workspace with the provided work space identifier
     * @param workspaceId the work space identifier
     * @param name the folder name
     * @param insertParam
     * @return
     */
    ResponseEntity<Void> folderInsert( Integer workspaceId, String name, InsertParameter insertParam);

    ResponseEntity<Void> fileUpdate( UpdateFileParameter updateFileParameter );

    ResponseEntity<Number> createDocument( String name );
}
