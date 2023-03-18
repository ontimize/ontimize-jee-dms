package com.ontimize.jee.common.services.s3.event;

/**
 * List of S3 repository events
 */
public enum S3RepositoryEvent {
    DOWNLOAD,
    FIND,
    FIND_BY_KEY,
    GET_METADATA,
    UPLOAD,
    COPY,
    MOVE,
    DELETE
}
