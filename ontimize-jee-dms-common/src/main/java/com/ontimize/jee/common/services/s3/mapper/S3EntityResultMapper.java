package com.ontimize.jee.common.services.s3.mapper;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.services.s3.result.S3RepositoryResult;

import java.util.List;


/**
 * Class mapping Objects to EntityResult
 */
public class S3EntityResultMapper {

    /**
     * Private Constructor, utility class
     */
    private S3EntityResultMapper() {
        throw new IllegalStateException( "Utility class" );
    }

    /**
     * Method that creates an EntityResult from a list of S3RepositoryResult
     *
     * @param source The list of S3RepositoryResult
     *
     * @return The EntityResult
     */
    public static final EntityResult map( final List<S3RepositoryResult> source ){
        //Initialise result
        final EntityResult result = new EntityResultMapImpl();

        //Add data into result
        source.stream().forEach( target -> result.addRecord( target.toMap() ));

        return result;
    }


    /**
     * Method that creates an EntityResult from a S3RepositoryResult
     *
     * @param source The S3RepositoryResult
     *
     * @return The EntityResult
     */
    public static final EntityResult map( final S3RepositoryResult source ){
        //Initialise result
        final EntityResult result = new EntityResultMapImpl();

        //Add data into result
        result.addRecord( source.toMap() );

        return result;
    }
}
