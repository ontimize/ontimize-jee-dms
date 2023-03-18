package com.ontimize.jee.common.services.s3.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Class wrapping the S3 repository results data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3RepositoryResult {

    /** The key of the S3 object in Base64 encoded format */
    private String id;
    /** The key to the map on which the ID of the S3 Bucket */
    public static final String KEY_ID = "id";

    /** The name of the S3 Bucket */
    private String bucket;
    /** The key to the map on which the name of the S3 Bucket */
    public static final String KEY_BUCKET = "bucket";

    /** The prefix of the S3 object */
    private String prefix;
    /** The key to the map on which the prefix of the S3 object */
    public static final String KEY_PREFIX = "prefix";

    /** The name of the S3 object */
    private String name;
    /** The key to the map on which the name of the S3 object */
    public static final String KEY_NAME = "name";

    /** The key of the S3 object */
    private String key;
    /** The key to the map on which the key of the S3 object */
    public static final String KEY_KEY = "key";

    /** The metadata of the S3 object */
    private Map<String, String> metadata;
    /** The key to the map on which the metadata of the S3 object */
    public static final String KEY_METADATA = "metadata";

    /** The size of the S3 object */
    private Long size;
    /** The key to the map on which the size of the S3 object */
    public static final String KEY_SIZE = "size";

    /** The owner name of the S3 object */
    private String owner;
    /** The key to the map on which the owner name of the S3 object */
    public static final String KEY_OWNER = "owner";

    /** The content of the S3 object */
    private InputStream fileContent;
    /** The key to the map on which the content of the S3 object */
    public static final String KEY_FILE_CONTENT = "fileContent";

    /** The flag indicating whether the S3 object is a file */
    private boolean file = false;
    /** The key to the map on which the flag indicating whether the S3 object is a file */
    public static final String KEY_FILE = "file";



    /**
     * Method that sets the key, prefix and object name of the S3 object from its key
     *
     * @param key The S3 key of the object
     */
    public void setKey( final String key ) {
        //Split key
        final String[] parts = key.split("/");

        //Create name
        final String newName = parts[ parts.length - 1 ];

        //Check if there are a prefix
        if( parts.length > 1 ) {
            //Create Prefix
            final StringBuilder prefixBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                prefixBuilder.append(String.format("%s/", parts[i]));
            }
            final int prefixSize = prefixBuilder.length();
            final String newPrefix = prefixBuilder.substring(0, prefixSize - 1);

            //Set Prefix
            this.setPrefix( newPrefix );
        }

        //Set data
        this.setName( newName );
        this.key = key;
    }


    /**
     * Method that sets the prefix of the S3 object
     *
     * @param prefix The S3 prefix of the object
     */
    public void setPrefix( final String prefix ){
        this.prefix = prefix;
    }


    /**
     * Method that sets the name of the S3 object
     *
     * @param name The S3 name of the object
     */
    public void setName( final String name ){
        this.name = name;
    }


    /**
     * Method that returns a map with the class data
     *
     * @return The map with the class data
     */
    public Map<String, Object> toMap(){
        //Initialise result
        final Map<String, Object> result = new HashMap<>();

        //Set data
        result.put(KEY_ID, this.getId() );
        result.put(KEY_BUCKET, this.getBucket() );
        result.put(KEY_PREFIX, this.getPrefix() );
        result.put(KEY_NAME, this.getName() );
        result.put(KEY_KEY, this.getKey() );
        result.put(KEY_METADATA, this.getMetadata() );
        result.put(KEY_SIZE, this.getSize() );
        result.put(KEY_OWNER, this.getOwner() );
        result.put(KEY_FILE_CONTENT, this.getFileContent() );
        result.put(KEY_FILE, this.isFile() );

        return result;
    }
}
