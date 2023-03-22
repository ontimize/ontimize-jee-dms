package com.ontimize.jee.server.dms.strategy;

import com.ontimize.jee.server.dms.rest.IDMSRestControllerEndpoints;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public abstract class DMSRestConstrollerStrategy implements IDMSRestControllerEndpoints {

    /**
     * Method that encodes a String in Base64.
     * @param target The String to encode in Base64.
     * @return The String encoded in Base64.
     */
    protected String encode( final String target ){
        return new String( Base64.getEncoder().encode( target.getBytes( StandardCharsets.UTF_8 )));
    }


    /**
     * Method that dencodes a String in Base64
     * @param target The String to dencode in Base64.
     * @return The String dencoded in Base64.
     */
    protected String decodeId( final String target ){
        return new String( Base64.getDecoder().decode( String.valueOf( target )));
    }

    /**
     * Method that obtains the metadata of an S3 object in a Map from a JSON in String format.
     *
     * @param metadata The metadata as JSON in String format.
     *
     * @return The map with the metadata of the S3 object
     */
    protected Map<String, String> getMetadataFromJSONString(final String metadata ){
        //Intialize the result as null
        Map<String, String> result = null;

        //Check if the metadata exists
        if( metadata != null && !metadata.isBlank() ){ //If exists
            //Initialize the data as Empty Map
            final Map<String, String> data = new HashMap<>();

            //Get Metadata as JSON Object
            final JSONObject jsonMetadata = new JSONObject( metadata );

            //Add data from JSON Object to result
            jsonMetadata.toMap().entrySet().stream()
                    .forEach( target -> data.put( target.getKey(), String.valueOf( target.getValue() )));

            //Set Data in result
            result = data;
        }

        //Return result
        return result;
    }
}
