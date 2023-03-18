package com.ontimize.jee.common.services.s3.mapper;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.ontimize.jee.common.services.s3.result.S3RepositoryResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Class mapping the different results of the S3 repository to the S3RepositoryResult class
 */
public class S3RepositoryResultMapper {

    /**
     * Private Constructor, utility class
     */
    private S3RepositoryResultMapper() {
        throw new IllegalStateException( "Utility class" );
    }

    /**
     * Method transforming the information of an S3Object to an S3RepositoryResult
     *
     * @param source The S3Object
     *
     * @return The S3RepositoryResult with S3Object data
     */
    public static final S3RepositoryResult map(final S3Object source ) {
        //Initialise result
        final S3RepositoryResult result = new S3RepositoryResult();

        //Set data
        result.setBucket( source.getBucketName() );
        result.setKey( source.getKey() );
        result.setMetadata( source.getObjectMetadata().getUserMetadata() );
        result.setFileContent( source.getObjectContent() );
        result.setFile( true );

        return result;
    }


    /**
     * Method transforming the information of an S3ObjectSummary to an S3RepositoryResult
     *
     * @param source The S3ObjectSummary
     *
     * @return The S3RepositoryResult with S3ObjectSummary data
     */
    public static final S3RepositoryResult map(final S3ObjectSummary source ) {
        //Initialise result
        final S3RepositoryResult result = new S3RepositoryResult();

        //Set data
        result.setBucket( source.getBucketName() );
        result.setKey( source.getKey() );
        result.setOwner( source.getOwner().getDisplayName() );
        result.setSize( source.getSize() );
        result.setFile( true );

        return result;
    }


    /**
     * Method transforming the information of an ObjectMetadata to an S3RepositoryResult
     *
     * @param source The ObjectMetadata
     *
     * @return The S3RepositoryResult with ObjectMetadata data
     */
    public static final S3RepositoryResult map(final ObjectMetadata source ) {
        //Initialise result
        final S3RepositoryResult result = new S3RepositoryResult();

        //Set data
        result.setMetadata( source.getUserMetadata() );
        result.setFile( true );

        return result;
    }


    /**
     * Method that transforms the data of the objects in a list into an S3RepositoryRestult list with that data.
     * Admits:
     *  - S3Object
     *  - S3ObjectSummary
     *  - ObjectMetadata
     *
     * @param source The list of objects
     * @param <T> The object type
     *
     * @return The List of S3RepositoryResult with data from source
     */
    public static final <T> List<S3RepositoryResult> map(final List<T> source ) {
        //Initialise result
        final List<S3RepositoryResult> result = new ArrayList<>();

        //Check if exists data in source
        if( !source.isEmpty() ) { // If exists data in source
            //Check if the source contains data of type S3Object
            if ( source.get(0).getClass() == S3Object.class ){ //If the type is S3Object
                source.stream().forEach(target -> result.add( S3RepositoryResultMapper.map( (S3Object) target )));
            }
            //Check if the source contains data of type S3ObjectSummary
            else if ( source.get(0).getClass() == S3ObjectSummary.class ) { //If the type is S3ObjectSummary
                source.stream().forEach(target -> result.add( S3RepositoryResultMapper.map( (S3ObjectSummary) target )));
            }
            //Check if the source contains data of type ObjectMetadata
            else if ( source.get(0).getClass() == ObjectMetadata.class ) { //If the type is ObjectMetadata
                source.stream().forEach(target -> result.add( S3RepositoryResultMapper.map( (ObjectMetadata) target )));
            }
        }

        return result;
    }
}
