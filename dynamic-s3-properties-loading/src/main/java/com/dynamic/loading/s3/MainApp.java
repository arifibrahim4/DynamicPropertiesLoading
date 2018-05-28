package com.dynamic.loading.s3;

import com.dynamic.loading.s3.component.S3PropertiesLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
public class MainApp {

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private static ApplicationContext context;

    @Autowired
    private RefreshEndpoint refreshEndpoint;

    @Autowired
    ConfigurableEnvironment env;

    @Autowired
    S3PropertiesLoader s3PropertiesLoader;

    public static void main(String[] args){
        context = SpringApplication.run(MainApp.class, args);
    }

    @Scheduled(fixedDelay = 5000)
    public void refresh() throws IOException{
        //S3PropertiesLoader s3PropertiesLoader = (S3PropertiesLoader)context.getBean("s3Properties");
        MutablePropertySources sources = env.getPropertySources();
        sources.addFirst(new PropertiesPropertySource("applicationConfig",s3PropertiesLoader.loadPropertiesFromS3()));

        refreshEndpoint.refresh();
        //propertyPlaceholderConfigurer.setProperties(reloadProperties());
        //scope
        Date date = new Date();
        System.out.println("Refresh happened @ "+format.format(date)+" version ::::::::::"+s3PropertiesLoader.getVersion());
    }

}
