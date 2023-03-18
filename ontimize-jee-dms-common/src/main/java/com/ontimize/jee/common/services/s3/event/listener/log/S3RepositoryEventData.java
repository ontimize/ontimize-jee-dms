package com.ontimize.jee.common.services.s3.event.listener.log;

import com.ontimize.jee.common.event.data.IEventData;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Class representing the triggered event information of S3 repository.
 * {@inheritDoc}
 */
@Data
@NoArgsConstructor
public class S3RepositoryEventData implements IEventData {

    /** The triggered event */
    private Enum event;

    /** The name of S3 Bucket */
    private String bucket;

    /** The key of S3 Object */
    private String key;



    /**
     * Constructor.
     *
     * @param bucket The name of S3 Bucket
     * @param key The key of S3 object
     */
    public S3RepositoryEventData( final String bucket, final String key ){
        this.setBucket( bucket );
        this.setKey( key );
    }

    @Override
    public void setEvent( final Enum event ) {
        this.event = event;
    }
}
