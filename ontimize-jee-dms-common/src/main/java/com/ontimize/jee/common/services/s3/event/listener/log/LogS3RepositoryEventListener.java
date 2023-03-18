package com.ontimize.jee.common.services.s3.event.listener.log;

import com.ontimize.jee.common.event.annotation.OntimizeEventListener;
import com.ontimize.jee.common.event.listener.IEventListener;
import com.ontimize.jee.common.services.s3.event.S3RepositoryEvent;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class for displaying an event information message in the Log
 * {@inheritDoc}
 * @see S3RepositoryEventData
 */
@NoArgsConstructor
@EqualsAndHashCode
@OntimizeEventListener
public class LogS3RepositoryEventListener implements IEventListener<S3RepositoryEventData> {

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger( "com.imatia.platform.hr.model.core.dms.service.s3.event.listener.log.LogS3RepositoryEventListener" );

    /** Event List */
    private final List<Enum> events = Arrays.asList(
            S3RepositoryEvent.DOWNLOAD,
            S3RepositoryEvent.FIND,
            S3RepositoryEvent.FIND_BY_KEY,
            S3RepositoryEvent.GET_METADATA,
            S3RepositoryEvent.UPLOAD,
            S3RepositoryEvent.COPY,
            S3RepositoryEvent.MOVE,
            S3RepositoryEvent.DELETE
    );


    @Override
    public List<Enum> getEvents() {
        return this.events;
    }


    @Override
    public void run( final S3RepositoryEventData data ) {
        String msg = String.format( "Event Listener Data: Event: %s, Bucket: %s, Key: %s", data.getEvent(), data.getBucket(), data.getKey() );
        LOGGER.log( Level.INFO, msg );
    }
}