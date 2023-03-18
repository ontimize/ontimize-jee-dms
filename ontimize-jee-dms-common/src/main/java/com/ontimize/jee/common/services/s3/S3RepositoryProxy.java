package com.ontimize.jee.common.services.s3;

import com.ontimize.jee.common.event.IEventHandler;
import com.ontimize.jee.common.services.s3.event.S3RepositoryEvent;
import com.ontimize.jee.common.services.s3.event.listener.log.S3RepositoryEventData;
import com.ontimize.jee.common.services.s3.result.S3RepositoryResult;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * S3 repository proxy implementation
 * {@inheritDoc}
 * @see S3Repository
 */
@NoArgsConstructor
@Repository( "S3RepositoryProxy" )
public class S3RepositoryProxy implements IS3Repository {

    /** The Event Handler */
    private @Autowired IEventHandler eventHandler;

    /** The S3 repository */
    @Qualifier( "S3Repository" )
    private @Autowired
    IS3Repository s3Repository;


    @Override
    public S3RepositoryResult download(final String bucket, final String key ) {
        this.eventHandler.trigger( S3RepositoryEvent.DOWNLOAD, new S3RepositoryEventData( bucket, key ));
        final String keyDecoded = new String( Base64.getDecoder().decode( key ));
        final S3RepositoryResult result = this.s3Repository.download(bucket, keyDecoded);
        final String id = new String( Base64.getEncoder().encode( result.getKey().getBytes() ));
        result.setId( id );
        return result;
    }


    @Override
    public List<S3RepositoryResult> find(final String bucket, final String key ) {
        this.eventHandler.trigger( S3RepositoryEvent.FIND, new S3RepositoryEventData( bucket, key ) );
        final String keyDecoded = key != null ? this.decode( key ) : "";
        final List<S3RepositoryResult> list = this.s3Repository.find( bucket, keyDecoded );
        final List<S3RepositoryResult> result = this.filterFindResultWithObjectsFromPrefixOfFirstLevel( list, keyDecoded );
        result.stream().forEach( target -> target.setId( this.encode( target.getKey() )));
        result.sort( new S3RepositoryResultComparator() );
        return result;
    }


    /**
     * Method that filters the results of the object query method to S3 to return objects that are at the first level of the requested prefix
     *
     * @param findResultList The results of the object query method to S3
     * @param key The key that was used as a criterion for the object search in S3
     *
     * @return The S3RepositoryResult list filtered
     */
    private List<S3RepositoryResult> filterFindResultWithObjectsFromPrefixOfFirstLevel(final List<S3RepositoryResult> findResultList, final String key ){
        //Initialise result
        final Set<S3RepositoryResult> result = new HashSet<>();

        //Filter results
        for( final S3RepositoryResult findResult : findResultList ) {
            //Get data
            final String[] resultKeyParts = findResult.getKey().split( "/" );
            final String[] requestKeyParts = key != null && !key.isBlank() ? key.split( "/" ) : new String[0];
            final int resultPrefixDeep = resultKeyParts.length - 1;
            final int offset = requestKeyParts.length + 1;
            final int index = requestKeyParts.length - 1;

            //Check if the object is a first level
            if( resultPrefixDeep < offset ){ // If the object is a first level
                result.add( findResult );
            }

            //Add folders
            final StringBuilder folderKeyBuilder = new StringBuilder();
            for( int i = 0 ; i < resultKeyParts.length - 1 && i < offset ; i++ ){ //Browse all parts of prefix of result
                //Build key
                folderKeyBuilder.append( resultKeyParts[i] );

                //Check if it is a higher level folder
                if( i > index  ) { // If it is a higher level folder
                    //Initialise Folder
                    final S3RepositoryResult folder = new S3RepositoryResult();

                    //Set data
                    folder.setFile(false);
                    folder.setKey(folderKeyBuilder.toString());
                    folder.setBucket( findResult.getBucket() );

                    result.add(folder);
                }

                folderKeyBuilder.append("/");
            }
        }

        //return Result
        return result.stream()
                .filter( target -> (key.isBlank() && target.getPrefix() == null) || key.equals( target.getKey() ) || key.equals( target.getPrefix() ))
                .collect( Collectors.toList() );
    }


    @Override
    public S3RepositoryResult findByKey(final String bucket, final String key ) {
        this.eventHandler.trigger( S3RepositoryEvent.FIND_BY_KEY, new S3RepositoryEventData( bucket, key ) );
        final S3RepositoryResult result = this.s3Repository.findByKey( bucket, this.decode( key ));
        result.setId( this.encode( result.getKey() ));
        return result;
    }


    @Override
    public S3RepositoryResult getMetadata(final String bucket, final String key ) {
        this.eventHandler.trigger( S3RepositoryEvent.GET_METADATA, new S3RepositoryEventData( bucket, key ) );
        final String keyDecoded = new String( Base64.getDecoder().decode( key ));
        return this.s3Repository.getMetadata( bucket, keyDecoded );
    }


    @Override
    public S3RepositoryResult upload(final InputStream file, final String bucket, final String key, final Map<String, String> metadata ) {
        this.eventHandler.trigger( S3RepositoryEvent.UPLOAD, new S3RepositoryEventData( bucket, key ) );
        final String keyDecoded = new String( Base64.getDecoder().decode( key ));
        final S3RepositoryResult result = this.s3Repository.upload( file, bucket, keyDecoded, metadata );
        result.setId( this.encode( result.getKey() ));
        return result;
    }


    @Override
    public S3RepositoryResult copy(final String sourceBucket, final String sourceKey, final String destinationBucket, final String destinationKey, final Map<String, String> metadata ) {
        this.eventHandler.trigger( S3RepositoryEvent.COPY, new S3RepositoryEventData( sourceBucket, sourceKey ) );
        final String sourceKeyDecoded = new String( Base64.getDecoder().decode( sourceKey ));
        final String destinationKeyDecoded = new String( Base64.getDecoder().decode( destinationKey ));
        final S3RepositoryResult result = this.s3Repository.copy( sourceBucket, sourceKeyDecoded, destinationBucket, destinationKeyDecoded, metadata );
        result.setId( this.encode( result.getKey() ));
        return result;
    }


    @Override
    public S3RepositoryResult move(final String sourceBucket, final String sourceKey, final String destinationBucket, final String destinationKey, final Map<String, String> metadata ) {
        this.eventHandler.trigger( S3RepositoryEvent.MOVE, new S3RepositoryEventData( sourceBucket, sourceKey ) );
        final String sourceKeyDecoded = new String( Base64.getDecoder().decode( sourceKey ));
        final String destinationKeyDecoded = new String( Base64.getDecoder().decode( destinationKey ));
        final S3RepositoryResult result = this.s3Repository.move( sourceBucket, sourceKeyDecoded, destinationBucket, destinationKeyDecoded, metadata );
        result.setId( this.encode( result.getKey() ));
        return result;
    }


    @Override
    public List<S3RepositoryResult> delete(final String bucket, final String key ) {
        this.eventHandler.trigger( S3RepositoryEvent.DELETE, new S3RepositoryEventData( bucket, key ) );
        final String keyDecoded = new String( Base64.getDecoder().decode( key ));
        final List<S3RepositoryResult> result = this.s3Repository.delete( bucket, keyDecoded );
        result.stream().forEach( target -> target.setId( this.encode( target.getKey() )));
        return result;
    }

    private String decode( final String id ){
        return new String( Base64.getDecoder().decode( id ));
    }

    private String encode( final String id ){
        return new String( Base64.getEncoder().encode( id.getBytes() ));
    }
}
