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

    ResponseEntity<List<? extends OFile>> documentGetFiles( String id, QueryParameter queryParam );

    void fileGetContent( String id, HttpServletResponse response );

    ResponseEntity<Map<String, String>> fileGetContent( String id, List<OFile> files );

    void fileGetZip( String id, HttpServletResponse response );

    ResponseEntity<DocumentIdentifier> fileInsert( String id, MultipartFile file, String metadata, Object folderId );

    ResponseEntity<Void> fileDelete( String id, FileListParameter deleteParameter ) throws DmsException;

    ResponseEntity<Void> folderInsert( String id, String name, InsertParameter insertParam);

    ResponseEntity<Void> fileUpdate( UpdateFileParameter updateFileParameter );

    ResponseEntity<Number> createDocument( String name );

    ResponseEntity<Void> fileCopy( String sourceKey, String destinationId, Map<String, Object> data );

    ResponseEntity<Void> fileMove( String sourceKey, String destinationId, Map<String, Object> data );
}
