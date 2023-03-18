package com.ontimize.jee.common.services.s3;

import com.ontimize.jee.common.services.s3.result.S3RepositoryResult;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


/**
 * Repository that allows us to perform basic operations in an S3 bucket through its API.
 */
public interface IS3Repository {

    /**
     * Method that downloads a document from S3 using its API
     *
     * @param bucket The name of the S3 Bucket where the object is located
     * @param key The S3 key of the object in Base64 encoded format
     *
     * @return The S3RepositoryResult
     */
    S3RepositoryResult download(final String bucket, final String key );


    /**
     * Method that obtains the objects found in an S3 Bucket through its API from a key
     *
     * @param bucket The name of the S3 Bucket where the object is located
     * @param key The S3 key of the object in Base64 encoded format
     *
     * @return The S3RepositoryResult
     */
    List<S3RepositoryResult> find(final String bucket, final String key );


    /**
     * Method that obtains a document in an S3 Bucket via its API from its key
     *
     * @param bucket The name of the S3 Bucket where the object is located
     * @param key The S3 key of the object in Base64 encoded format
     *
     * @return The S3RepositoryResult
     */
    S3RepositoryResult findByKey(final String bucket, final String key );


    /**
     * Method that obtains the metadata of a document in an S3 Bucket via its API from its key
     *
     * @param bucket The name of the S3 Bucket where the object is located
     * @param key The S3 key of the object in Base64 encoded format
     *
     * @return The S3RepositoryResult
     */
    S3RepositoryResult getMetadata(final String bucket, final String key );


    /**
     * Method that uploads a document to an S3 Bucket via its API
     *
     * @param file Document data to be written to an S3 Bucket
     * @param bucket The name of the S3 Bucket where the object is located
     * @param key The S3 key of the object in Base64 encoded format
     * @param metadata The metadata to be added to the new S3 object
     *
     * @return The S3RepositoryResult
     */
    S3RepositoryResult upload(final InputStream file, final String bucket, final String key, final Map<String, String> metadata );


    /**
     * Method that copies a document from one S3 Bucket to another S3 Bucket using its API
     *
     * @param sourceBucket The name of the S3 bucket where the object to be copied is located
     * @param sourceKey The S3 key of the object to copy
     * @param destinationBucket The name of the S3 bucket where the object is to be copied to
     * @param destinationKey The S3 key of the copied object
     * @param metadata The metadata to be added to the new S3 object
     *
     * @return The S3RepositoryResult
     */
    S3RepositoryResult copy(final String sourceBucket, final String sourceKey, final String destinationBucket, final String destinationKey, final Map<String, String> metadata );


    /**
     * Method that moves a document from one S3 Bucket to another S3 Bucket using its API
     *
     * @param sourceBucket The name of the S3 bucket where the object to be moved is located
     * @param sourceKey The S3 key of the object to move
     * @param destinationBucket The name of the S3 bucket where the object is to be moved to
     * @param destinationKey The S3 key of the moved object
     * @param metadata The metadata to be added to the new S3 object
     *
     * @return The S3RepositoryResult
     */
    S3RepositoryResult move(final String sourceBucket, final String sourceKey, final String destinationBucket, final String destinationKey, final Map<String, String> metadata );


    /**
     * Method that removes a document from an S3 Bucket using its API via its S3 key
     *
     * @param bucket The name of the S3 Bucket where the object is located
     * @param key The S3 key of the object in Base64 encoded format
     *
     * @return The S3RepositoryResult
     */
    List<S3RepositoryResult> delete(final String bucket, final String key );
}
