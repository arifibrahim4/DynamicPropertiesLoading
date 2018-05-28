package com.dynamic.loading.s3.component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component("s3Properties")
@RefreshScope
public class S3PropertiesLoader {

    @Value("${application.name}")
    String appName ="test";

    @Value("${application.version}")
    String version;

    @Autowired
    AmazonS3 amazonS3;

    public String getAppName(){
        return appName;
    }

    public String getVersion() {
        return version;
    }

    public Properties loadPropertiesFromS3() throws IOException{
        Properties props = new Properties();
        S3Object s3Object = amazonS3.getObject("messaging-test-dynamic-properties","appTest.properties");
        try(S3ObjectInputStream s3Stream = s3Object.getObjectContent()){
            props.load(s3Stream);
        }
        return props;
    }

}
