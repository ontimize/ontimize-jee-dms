package com.ontimize.jee.common.services.s3;

import com.ontimize.jee.common.services.s3.result.S3RepositoryResult;

import java.util.Comparator;

public class S3RepositoryResultComparator implements Comparator<S3RepositoryResult> {
    @Override
    public int compare( final S3RepositoryResult o1, final S3RepositoryResult o2 ){
        int result = Boolean.compare( o2.isFile(), o1.isFile() );
        if( result == 0 ){
            result = o2.getName().compareTo( o1.getName() );
        }
        return result;
    }
}
