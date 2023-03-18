package com.ontimize.jee.common.services.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * AmazonS3 Bean configuration class
 */
@Configuration
public class AmazonS3Config {

    /** The access key from Amazon S3 */
    @Value( "${ontimize.dms.aws.access-key}" )
    private String accessKey;

    /** The secret key from Amazon S3 */
    @Value( "${ontimize.dms.aws.secret-key}" )
    private String secretKey;

    /** The region of Amazon S3 */
    @Value( "${ontimize.dms.aws.region}" )
    private String region;


    /**
     * Create and configure the AmazonS3 Bean
     *
     * @return The AmazonS3 Bean
     */
    @Bean( "AmazonS3" )
    public AmazonS3 amazonS3() {
        //Initialise AWS credentials
        BasicAWSCredentials awsCreds = new BasicAWSCredentials( this.accessKey, this.secretKey );

        //Configure and return AmazonS3 bean
        return AmazonS3ClientBuilder.standard()
                .withRegion( Regions.fromName( this.region ) )
                .withCredentials( new AWSStaticCredentialsProvider( awsCreds ) )
                .build();
    }
}
