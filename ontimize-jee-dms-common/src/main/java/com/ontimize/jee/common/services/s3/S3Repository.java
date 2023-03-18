package com.ontimize.jee.common.services.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.ontimize.jee.common.services.s3.mapper.S3RepositoryResultMapper;
import com.ontimize.jee.common.services.s3.result.S3RepositoryResult;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Implementation of IS3Repository
 * {@inheritDoc}
 */
@NoArgsConstructor
@Repository( "S3Repository" )
public class S3Repository implements IS3Repository {

    /** The API of S3 */
    private @Autowired AmazonS3 amazonS3;


    @Override
    public S3RepositoryResult download(final String bucket, final String key ) {
        //Initialise request
        final GetObjectRequest request = new GetObjectRequest( bucket, key );

        //Download
        final S3Object result = this.amazonS3.getObject( request );

        //Return result as S3RepositoryResult
        return S3RepositoryResultMapper.map( result );
    }


    @Override
    public List<S3RepositoryResult> find(final String bucket, final String key ) {
        //Initialise request
        final ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName( bucket )
                .withPrefix( key );

        //Get list of objects
        final ObjectListing objectListing = this.amazonS3.listObjects( request );

        //Return result as list of S3RepositoryResult
        return S3RepositoryResultMapper.map( objectListing.getObjectSummaries() );
    }


    @Override
    public S3RepositoryResult getMetadata(final String bucket, final String key ) {
        //Initialise request
        final GetObjectMetadataRequest request = new GetObjectMetadataRequest( bucket, key );

        //Get metadata
        final ObjectMetadata result = this.amazonS3.getObjectMetadata( request );

        //Return result as S3RepositoryResult
        return S3RepositoryResultMapper.map( result );
    }


    @Override
    public S3RepositoryResult upload(final InputStream file, final String bucket, final String key, final Map<String, String> metadata ){
        //Initialise result and request
        S3RepositoryResult result = null;
        final PutObjectRequest request = new PutObjectRequest( bucket, key, file, null );

        //Check if there are metadata
        if( metadata != null && !metadata.isEmpty() ){ //If there are metadata
            //Add Metadata to request
            final ObjectMetadata objectMetadata = new ObjectMetadata();
            metadata.forEach( objectMetadata::addUserMetadata );
            request.withMetadata( objectMetadata );
        }

        //Upload
        this.amazonS3.putObject( request );
        final boolean isSuccessful = this.amazonS3.doesObjectExist( request.getBucketName(), request.getKey() );

        //Check the result
        if( isSuccessful ){ //If is succesful
            //Find Object to return
            result = this.findByKey( bucket, key );
        }

        return result;
    }


    @Override
    public S3RepositoryResult copy(final String sourceBucket, final String sourceKey, final String destinationBucket, final String destinationKey, final Map<String, String> metadata ) {
        //Initialise result and request
        S3RepositoryResult result = null;
        final CopyObjectRequest request = new CopyObjectRequest ( sourceBucket, sourceKey, destinationBucket, destinationKey );

        //Check if there are metadata
        if( metadata != null && !metadata.isEmpty() ){ //If there are metadata
            //Add Metadata to request
            final ObjectMetadata objectMetadata = new ObjectMetadata();
            metadata.forEach( objectMetadata::addUserMetadata );
            request.withNewObjectMetadata( objectMetadata );
        }

        //Copy
        this.amazonS3.copyObject( request );
        final boolean isSuccessful = this.amazonS3.doesObjectExist( request.getSourceBucketName(), request.getSourceKey() )
            && this.amazonS3.doesObjectExist( request.getDestinationBucketName(), request.getDestinationKey() );

        //Check the result
        if( isSuccessful ){ //If is succesful
            //Find Object to return
            result = this.findByKey( destinationBucket, destinationKey );
        }

        return result;
    }


    @Override
    public S3RepositoryResult move(final String sourceBucket, final String sourceKey, final String destinationBucket, final String destinationKey, final Map<String, String> metadata ) {
        //Copy
        final S3RepositoryResult copyResult = this.copy( sourceBucket, sourceKey, destinationBucket, destinationKey, metadata );

        //Delete
        this.delete( sourceBucket, sourceKey );

        return copyResult;
    }


    @Override
    public List<S3RepositoryResult> delete(final String bucket, final String key ) {
        //Initialise result
        final List<S3RepositoryResult> result = new ArrayList<>();

        //Search object to delete
        final S3RepositoryResult object = this.findByKey( bucket, key );

        //Check object exists
        if( object != null ) { //If object exists
            //Delete object
            final DeleteObjectRequest request = new DeleteObjectRequest( bucket, key );
            this.amazonS3.deleteObject( request );
            result.add( object );
        }
        else{
            //Get All objects from the folder
            final List<S3RepositoryResult> findResultList = this.find( bucket, key );

            //Delete all
            findResultList.stream().forEach( target ->
                result.addAll( this.delete( target.getBucket(), target.getKey() ))
            );
        }

        return result;
    }


    @Override
    public S3RepositoryResult findByKey(final String bucket, final String key ){
        //Initialise result and request
        S3RepositoryResult result = null;

        //Find
        final List<S3RepositoryResult> findResultList = this.find( bucket, key );

        //Check findResultList if there is exactly one result and it's a file
        if( findResultList.size() == 1 && findResultList.get( 0 ).getKey().equals( key ) && findResultList.get( 0 ).isFile() ){ //If there is exactly one result and it's a file
            //Set as result
            result = findResultList.get( 0 );
        }

        return result;
    }
}
