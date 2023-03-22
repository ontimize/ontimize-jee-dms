package com.ontimize.jee.server.dms.strategy.s3;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.services.s3.result.S3RepositoryResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.dms.rest.IDMSNameConverter;
import com.ontimize.jee.server.dms.strategy.DMSRestConstrollerStrategy;
import com.ontimize.jee.server.rest.FileListParameter;
import com.ontimize.jee.server.rest.InsertParameter;
import com.ontimize.jee.server.rest.QueryParameter;
import com.ontimize.jee.server.rest.UpdateFileParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IDMSRestControllerS3Strategy extends DMSRestConstrollerStrategy {

    //CONSTANTS

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger( IDMSRestControllerS3Strategy.class );

    private static final String MSG_SERVICE_IS_NULL = "Service is null";
    private static final String KW_METADATA = "metadata";

    //PROPERTIES

    /** The bean property service. */
    private @Autowired() IDMSService dmsService;

    /** The bean property converter. */
    private @Autowired( required = false ) IDMSNameConverter dmsNameConverter;

// ------------------------------------------------------------------------------------------------------------------ //

    /** Constructor. */
    public IDMSRestControllerS3Strategy(){ super(); }

// ------------------------------------------------------------------------------------------------------------------ \\
// -------| FIND DOCUMENTS ENDPOINT |-------------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    // TODO: For implementing the return of the files in the response.
    @Override
    public ResponseEntity<List<? extends OFile>> documentGetFiles( final String id, final QueryParameter queryParam ){
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Key from ID
            final String key = this.decodeId( id );

            //Get Data
            Map<Object, Object> filter = null;
            List<Object> columns = null;
            if( queryParam != null ) {
                filter = queryParam.getFilter();
                columns = queryParam.getColumns();
            }

            //Request
            final EntityResult entityResult = this.dmsService.documentGetFiles( key, filter, columns );

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    @Override
    public @ResponseBody void fileGetContent( final String id, final HttpServletResponse response ) {
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        //Initialize InputStream
        InputStream inputStream = null;

        try {
            //Get Key from ID
            final String key = this.decodeId( id );

            //Get Documen info
            final EntityResult entityResult = this.dmsService.documentGetFiles( id, null, null );

            //Check if there is a result
            if( entityResult.getCode() != EntityResult.OPERATION_WRONG && entityResult.calculateRecordNumber() == 1 ){ // If there is a result
                //Get result
                final Map<String, Object> result = entityResult.getRecordValues( 0 );

                //Get Data
                final String name = (String) result.get( S3RepositoryResult.KEY_NAME );
                final long size = (long) result.get( S3RepositoryResult.KEY_SIZE );

                //Get Content
                inputStream = this.dmsService.fileGetContent( id );

                //Set Response
                FileCopyUtils.copy( inputStream, response.getOutputStream() );
                response.setHeader( HttpHeaders.CONTENT_DISPOSITION, String.format( "attachment; filename=\"%s\"", name ));
                response.setContentType( MediaType.APPLICATION_OCTET_STREAM_VALUE );
                response.setContentLengthLong( size );
            }
            else {
                response.setStatus( HttpServletResponse.SC_NOT_FOUND );
            }
        }
        catch ( final Exception exception ) { //Return Error 500
            LOGGER.error( "{}", exception.getMessage(), exception );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
        finally {
            if( inputStream != null ){
                try {
                    inputStream.close();
                }
                catch( final IOException exception ) {
                    LOGGER.error( "{}", exception.getMessage(), exception );
                }
            }
        }
    }



    // TODO: To be implemented
    @Override
    public ResponseEntity<Map<String, String>> fileGetContent( final String id, final List<OFile> files ) {
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        //Initialize InputStream
        InputStream inputStream = null;

        try {
            //Get Key from ID
            final String key = this.decodeId( id );

            //Service Call...

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
        finally {
            if( inputStream != null ){
                try {
                    inputStream.close();
                }
                catch( final IOException exception ) {
                    LOGGER.error( "{}", exception.getMessage(), exception );
                }
            }
        }
    }



    // TODO: To be implemented
    @Override
    public @ResponseBody void fileGetZip( final String file, final HttpServletResponse response ){
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        //Initialize InputStream
        InputStream inputStream = null;

        try {
            //Service Call...

            //Return Result
            response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ) { //Return Error 500
            LOGGER.error( "{}", exception.getMessage(), exception );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
        finally {
            if( inputStream != null ){
                try {
                    inputStream.close();
                }
                catch( final IOException exception ) {
                    LOGGER.error( "{}", exception.getMessage(), exception );
                }
            }
        }
    }



    // TODO: For implementing the logic to pass to the service the possible metadata of the file
    @Override
    public ResponseEntity<DocumentIdentifier> fileInsert( final String id, final MultipartFile file, final String metadata, final Object folderId ){
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Key from ID
            final String key = this.decodeId( id );

            //Get Data
            final Map<Object, Object> av = new HashMap<>();
            av.put( this.dmsNameConverter.getFileNameColumn(), file.getOriginalFilename() );
            if( folderId != null ) av.put( this.dmsNameConverter.getCategoryIdColumn(), folderId );

            //Request
            final DocumentIdentifier documentIdentifier = this.dmsService.fileInsert( key, av, file.getInputStream() );

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    // TODO: For implementing the logic to delete the files from the FileListParameter parameter
    @Override
    public ResponseEntity<Void> fileDelete( final String id, final FileListParameter deleteParameter ) throws DmsException {
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Key from ID
            final String key = this.decodeId( id );

            //Request
            this.dmsService.documentDelete( key );

            //Return Result
            return new ResponseEntity<>( HttpStatus.OK );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    // TODO: To be implemented
    @Override
    public ResponseEntity<Void> folderInsert( final String id, final String name, final InsertParameter insertParam ){
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Key from ID
            final String key = this.decodeId( id );

            //Get Data
            Map<Object, Object> data = null;
            if( insertParam != null ) {
                data = insertParam.getData();
            }

            //Service Call...

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    // TODO: To be implemented
    @Override
    public ResponseEntity<Void> fileUpdate( final UpdateFileParameter updateFileParameter ){
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Key from ID
            //final String key = this.decodeId( id );

            //Get Data
            Map<Object, Object> av = new HashMap<>();

            //Service Call...

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    // TODO: To be implemented
    @Override
    public ResponseEntity<Number> createDocument( final String name ){
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Key from ID
            //final String key = this.decodeId( id );

            //Get Data
            Map<Object, Object> av = new HashMap<>();

            //Service Call...

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    // TODO: To be implemented
    @Override
    public ResponseEntity<Void> fileCopy( final String sourceId, final String destinationId, final Map<String, Object> data ) {
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Keys from IDs
            final String sourceKey = this.decodeId( sourceId );
            final String destinationKey = this.decodeId( destinationId );

            //Initialize metadata
            Map<String, String> metadata = null;

            //Check if there are metadata
            if( data != null && !data.isEmpty() && data.containsKey( KW_METADATA ) ){
                metadata = (Map<String, String>) data.get( KW_METADATA );
            }

            //Request
            final DocumentIdentifier documentIdentifier = null; //this.dmsService.documentCopy( sourceKey, destinationKey, metadata );

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }



    // TODO: To be implemented
    @Override
    public ResponseEntity<Void> fileMove( final String sourceId, final String destinationId, final Map<String, Object> data ) {
        CheckingTools.failIf( this.dmsService == null, NullPointerException.class, MSG_SERVICE_IS_NULL );

        try {
            //Get Keys from IDs
            final String sourceKey = this.decodeId( sourceId );
            final String destinationKey = this.decodeId( destinationId );

            //Initialize metadata
            Map<String, String> metadata = null;

            //Check if there are metadata
            if( data != null && !data.isEmpty() && data.containsKey( KW_METADATA ) ){
                metadata = (Map<String, String>) data.get( KW_METADATA );
            }

            //Request
            final DocumentIdentifier documentIdentifier = null; //this.dmsService.documentCopy( sourceKey, destinationKey, metadata );

            //Return Result
            return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
        }
        catch ( final Exception exception ){ //Return Error
            LOGGER.error( "{}", exception.getMessage(), exception );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

// ------------------------------------------------------------------------------------------------------------------ \\

}
