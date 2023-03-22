package com.ontimize.jee.server.dms.rest;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.dms.strategy.DMSRestConstrollerStrategy;
import com.ontimize.jee.server.rest.FileListParameter;
import com.ontimize.jee.server.rest.InsertParameter;
import com.ontimize.jee.server.rest.QueryParameter;
import com.ontimize.jee.server.rest.UpdateFileParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public abstract class DMSRestController<S extends DMSRestConstrollerStrategy> implements IDMSRestControllerEndpoints, InitializingBean {

    //CONSTANTS

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DMSRestController.class);

    private static final String MSG_SERVICE_IS_NULL = "Service is null";

    //KEYWORDS OF PARAMETERS
    public static final String KW_NAME = "name";
    public static final String KW_ID = "id";
    public static final String KW_FILE = "file";
    public static final String KW_METADATA = "metadata";
    public static final String KW_DATA = "data";
    public static final String KW_FOLDER_ID = "folder-id";
    public static final String KW_SOURCE_ID = "source-key";
    public static final String KW_DESTINATION_ID = "destination-key";

// ------------------------------------------------------------------------------------------------------------------ //

    public abstract S getStrategy();

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| FIND DOCUMENTS ENDPOINT |-------------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @PostMapping(
           value = {"/queryFiles", "/queryFiles/{" + KW_ID + "}"},
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<List<? extends OFile>> documentGetFiles(
            @PathVariable( value = KW_ID, required = false ) final String id,
            @RequestBody( required = false ) final QueryParameter queryParam
    ) {
        return this.getStrategy().documentGetFiles( id, queryParam );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| DOWNLOAD DOCUMENTS ENDPOINTS |--------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @GetMapping(
           value = "/getFile/{" + KW_ID + "}",
           produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @Override
    public @ResponseBody void fileGetContent(
           @PathVariable( value = KW_ID, required = true ) final String id,
           HttpServletResponse response
    ){
        this.getStrategy().fileGetContent( id, response );
    }



    @PostMapping(
            value = {"/getFiles", "/getFiles/{" + KW_ID + "}"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public @ResponseBody ResponseEntity<Map<String, String>> fileGetContent(
           @PathVariable( value = KW_ID, required = false ) final String id,
           @RequestBody( required = true ) final List<OFile> files
    ){
        return this.getStrategy().fileGetContent( id, files );
    }



    @GetMapping(
           value = "/getZipFile/{" + KW_FILE + "}",
           produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @Override
    public @ResponseBody void fileGetZip(
           @PathVariable( value = KW_ID, required = true ) final String id,
           HttpServletResponse response
    ){
        this.getStrategy().fileGetZip( id, response );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| INSERT DOCUMENTS ENDPOINTS |----------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @PostMapping(
           value = "/insertFile/{" + KW_ID + "}",
           consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<DocumentIdentifier> fileInsert(
           @PathVariable( value = KW_ID, required = true ) final String id,
           @RequestParam( value = KW_FILE, required = true ) final MultipartFile file,
           @RequestParam( name = KW_METADATA, required = false ) final String metadata,
           @RequestParam( name = KW_FOLDER_ID, required = false ) Object folderId
    ){
        return this.getStrategy().fileInsert( id, file, metadata, folderId );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| INSERT FOLDERS ENDPOINTS |------------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @PostMapping(
            value = "/insertFolder/{" + KW_ID + "}/{" + KW_NAME +"}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Void> folderInsert(
            @PathVariable( value = KW_ID, required = true ) final String id,
            @PathVariable( value = KW_NAME, required = true ) final String name,
            @RequestBody( required = false ) InsertParameter insertParam
    ) {
        return this.getStrategy().folderInsert( id, name, insertParam );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| DELETE DOCUMENTS ENDPOINTS |----------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @PostMapping(
           value = {"/deleteFiles", "/deleteFiles/{" + KW_ID + "}"},
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Void> fileDelete(
           @PathVariable( value = KW_ID, required = false ) final String id,
           @RequestBody( required = true ) final FileListParameter deleteParameter
    ) throws DmsException {
        return this.getStrategy().fileDelete( id, deleteParameter );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| UPDATE DOCUMENTS ENDPOINTS |----------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @PostMapping(
           value = "/fileUpdate",
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Void> fileUpdate(
           @RequestBody( required = true ) UpdateFileParameter updateFileParameter
    ){
        return this.getStrategy().fileUpdate( updateFileParameter );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| CREATE DOCUMENTS ENDPOINTS |----------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @GetMapping(
           value = {"/createDocument", "/createDocument/{" + KW_NAME + "}"},
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Number> createDocument(
            @PathVariable( value = KW_NAME, required = false ) final String name
    ){
        return this.getStrategy().createDocument( name );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| COPY / MOVE DOCUMENTS ENDPOINTS |------------------------------------------------------------------------ \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @PostMapping(
            value = "/fileCopy/{" + KW_SOURCE_ID + "}/{" + KW_DESTINATION_ID + "}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Void> fileCopy(
            @PathVariable( name = KW_SOURCE_ID, required = true ) final String sourceId,
            @PathVariable( name = KW_DESTINATION_ID, required = true ) final String destinationId,
            @RequestBody( required = false ) final Map<String, Object> data
    ){
        return this.getStrategy().fileCopy( sourceId, destinationId, data );
    }

    @PostMapping(
            value = "/fileMove/{" + KW_SOURCE_ID + "}/{" + KW_DESTINATION_ID + "}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Void> fileMove(
            @PathVariable( name = KW_SOURCE_ID, required = true ) final String sourceId,
            @PathVariable( name = KW_DESTINATION_ID, required = true ) final String destinationId,
            @RequestBody( required = false ) final Map<String, Object> data
    ){
        return this.getStrategy().fileMove( sourceId, destinationId, data );
    }

// ------------------------------------------------------------------------------------------------------------------ \\

}
