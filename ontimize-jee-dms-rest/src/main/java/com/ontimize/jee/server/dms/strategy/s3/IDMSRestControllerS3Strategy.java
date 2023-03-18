package com.ontimize.jee.server.dms.strategy.s3;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.dms.strategy.IDMSRestConstrollerStrategy;
import com.ontimize.jee.server.rest.FileListParameter;
import com.ontimize.jee.server.rest.InsertParameter;
import com.ontimize.jee.server.rest.QueryParameter;
import com.ontimize.jee.server.rest.UpdateFileParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class IDMSRestControllerS3Strategy extends IDMSRestConstrollerStrategy {

    //CONSTANTS

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger( IDMSRestControllerS3Strategy.class );

    private static final String MSG_SERVICE_IS_NULL = "Service is null";

// ------------------------------------------------------------------------------------------------------------------ //

    /** Constructor */
    public IDMSRestControllerS3Strategy(){ super(); }


// ------------------------------------------------------------------------------------------------------------------ //
// ----------------------------------------------| ENDPOINTS |------------------------------------------------------- //
// ------------------------------------------------------------------------------------------------------------------ //

    @Override
    public ResponseEntity<List<? extends OFile>> documentGetFiles( final Integer workspaceId, final QueryParameter queryParam ){
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call

            return new ResponseEntity<>( null, HttpStatus.OK );
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public @ResponseBody void fileGetContent( final Integer fileId, final HttpServletResponse response ) {
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public ResponseEntity<Map<String, String>> fileGetContent( final Integer workspaceId, final List<OFile> files ) {
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call

            return new ResponseEntity<>( null, HttpStatus.OK );
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public @ResponseBody void fileGetZip( final String file, final HttpServletResponse response ){
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public ResponseEntity<DocumentIdentifier> fileInsert( final Integer workspaceId, final MultipartFile multipart, final Object folderId ){
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call

            return new ResponseEntity<>( null, HttpStatus.OK );
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public ResponseEntity<Void> delete( final Integer workspaceId, final FileListParameter deleteParameter ) throws DmsException {
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call

            return new ResponseEntity<>( null, HttpStatus.OK );
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public ResponseEntity<Void> folderInsert( final Integer workspaceId, final String name, final InsertParameter insertParam ){
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call

            return new ResponseEntity<>( null, HttpStatus.OK );
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public ResponseEntity<Void> fileUpdate( final UpdateFileParameter updateFileParameter ){
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call

            return new ResponseEntity<>( null, HttpStatus.OK );
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public ResponseEntity<Number> createDocument( final String name ){
        CheckingTools.failIf( this.service == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Service Call

            return new ResponseEntity<>( null, HttpStatus.OK );
        }
        catch( final Exception e ){
            LOGGER.error( "{}", e.getMessage(), e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
}
