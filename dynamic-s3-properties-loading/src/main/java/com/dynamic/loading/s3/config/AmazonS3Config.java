package com.dynamic.loading.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class AmazonS3Config {


    @Bean
    public BasicAWSCredentials getAwsCredentials(@Value("${cloud.aws.credentials.accessKey}")
                                                                String accessKey, @Value("${cloud.aws.credentials.secretKey}") String sceret){
        System.out.println(accessKey);
        return new BasicAWSCredentials(accessKey, sceret);
    }

    @Bean
    public AmazonS3 getAWSS3Client(BasicAWSCredentials awsCredentials){
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    @Bean
    public Properties loadProperties(AmazonS3 amazonS3, ConfigurableEnvironment env) throws IOException{
        Properties props = new Properties();
        S3Object s3Object = amazonS3.getObject("messaging-test-dynamic-properties","appTest.properties");
        try(S3ObjectInputStream s3Stream = s3Object.getObjectContent()){
            props.load(s3Stream);
        }
        MutablePropertySources sources = env.getPropertySources();
        sources.addFirst(new PropertiesPropertySource("applicationConfig",props));
        return props;
    }


}
