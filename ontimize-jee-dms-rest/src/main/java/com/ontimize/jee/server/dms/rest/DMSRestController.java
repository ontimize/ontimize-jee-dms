package com.ontimize.jee.server.dms.rest;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.dms.strategy.IDMSRestConstrollerStrategy;
import com.ontimize.jee.server.rest.FileListParameter;
import com.ontimize.jee.server.rest.InsertParameter;
import com.ontimize.jee.server.rest.QueryParameter;
import com.ontimize.jee.server.rest.UpdateFileParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public abstract class DMSRestController<T extends IDMSService, N extends IDMSNameConverter> implements IDMSRestControllerEndpoints, InitializingBean {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(DMSRestController.class);

    /** The Strategy */
    private IDMSRestConstrollerStrategy strategy;

    /** The bean property converter. */
    @Autowired( required = false )
    private N dmsNameConverter;

// ------------------------------------------------------------------------------------------------------------------ //

    public abstract T getService();

    public DMSRestController(){}
    public DMSRestController( final IDMSRestConstrollerStrategy strategy ){
        this.strategy = strategy;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.strategy.setService( this.getService() );
        this.strategy.setDmsNameConverter( this.dmsNameConverter );
    }

// ------------------------------------------------------------------------------------------------------------------ //
// ----------------------------------------------| ENDPOINTS |------------------------------------------------------- //
// ------------------------------------------------------------------------------------------------------------------ //

    @RequestMapping(
           path = "/queryFiles/{workspaceId}",
           method = RequestMethod.POST,
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<List<? extends OFile>> documentGetFiles(
           @PathVariable("workspaceId") Integer workspaceId,
           @RequestBody QueryParameter queryParam
    ) {
        return this.strategy.documentGetFiles( workspaceId, queryParam );
    }



    @RequestMapping(
           path = "/getFile/{fileId}",
           method = RequestMethod.GET,
           produces = "application/octet-stream"
    )
    @Override
    public @ResponseBody void fileGetContent(
           @PathVariable("fileId") Integer fileId,
           HttpServletResponse response
    ){
        this.strategy.fileGetContent( fileId, response );
    }



    @RequestMapping(
           path = "/getFiles/{workspaceId}",
           method = RequestMethod.POST,
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public @ResponseBody ResponseEntity<Map<String, String>> fileGetContent(
           @PathVariable("workspaceId") Integer workspaceId,
           @RequestBody List<OFile> files
    ){
        return this.strategy.fileGetContent( workspaceId, files );
    }



    @RequestMapping(
           path = "/getZipFile/{file}",
           method = RequestMethod.GET,
           produces = "application/octet-stream"
    )
    @Override
    public @ResponseBody void fileGetZip(
           @PathVariable("file") String file,
           HttpServletResponse response
    ){
        this.strategy.fileGetZip( file, response );
    }



    @RequestMapping(
           path = "/insertFile/{workspaceId}",
           method = RequestMethod.POST
    )
    @Override
    public ResponseEntity<DocumentIdentifier> fileInsert(
           @PathVariable("workspaceId") Integer workspaceId,
           @RequestParam("file") MultipartFile multipart,
           @RequestParam(name = "folderId", required = false) Object folderId
    ){
        return this.strategy.fileInsert( workspaceId, multipart, folderId );
    }



    @RequestMapping(
           value = "/deleteFiles/{workspaceId}",
           method = RequestMethod.POST,
           produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Void> delete(
           @PathVariable("workspaceId") Integer workspaceId,
           @RequestBody FileListParameter deleteParameter
    ) throws DmsException {
        return this.strategy.delete( workspaceId, deleteParameter );
    }



    @RequestMapping(
           path = "/insertFolder/{workspaceId}/{name}",
           method = RequestMethod.POST
    )
    @Override
    public ResponseEntity<Void> folderInsert(
           @PathVariable("workspaceId") Integer workspaceId,
           @PathVariable("name") String name,
           @RequestBody InsertParameter insertParam
    ) {
        return this.strategy.folderInsert( workspaceId, name, insertParam );
    }



    @RequestMapping(
           path = "/fileUpdate",
           method = RequestMethod.POST
    )
    @Override
    public ResponseEntity<Void> fileUpdate(
           @RequestBody UpdateFileParameter updateFileParameter
    ){
        return this.strategy.fileUpdate( updateFileParameter );
    }



    @RequestMapping(
           path = "/createDocument/{name}",
           method = RequestMethod.GET
    )
    @Override
    public ResponseEntity<Number> createDocument(
           @PathVariable(required = false) String name
    ){
        return this.strategy.createDocument( name );
    }
}
