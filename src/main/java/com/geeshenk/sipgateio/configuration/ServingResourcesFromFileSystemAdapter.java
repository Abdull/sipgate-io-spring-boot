package com.geeshenk.sipgateio.configuration;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.geeshenk.sipgateio.EndpointAddressComponent;

//see http://stackoverflow.com/a/26939359/923560
@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class ServingResourcesFromFileSystemAdapter extends
WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {
    
    private final static Logger logger = LoggerFactory.getLogger(ServingResourcesFromFileSystemAdapter.class);
    
    @Value("${external.file.system.chroot:./ext-resources/}")
    private String externalFileSystemChroot;
    
    @Value("${external.resources.http.root.path:/ext-resources/}")
    private String externalResourcesHttpRootPath;
    
    
    @Inject
    private EndpointAddressComponent eac;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("externalFileSystemChroot: {}", externalFileSystemChroot);
        
        //see http://stackoverflow.com/a/28559963/923560
        registry.addResourceHandler(externalResourcesHttpRootPath + "**").addResourceLocations("file:" + externalFileSystemChroot);
        
        super.addResourceHandlers(registry);
    }
    
    public String getExternalFileSystemChroot() {
        return externalFileSystemChroot;
    }
    
    public String getExternalResourcesHttpRootPath() {
        return externalResourcesHttpRootPath;
    }
    
    public String getUrlPathForFileLocation(String fileLocation) {
        String rootUrl = fileLocation.replaceFirst(externalFileSystemChroot, externalResourcesHttpRootPath);
        return rootUrl;
    }
    
    public String getInsideUrlForFileLocation(String fileLocation) {
        String insideRootUrl = eac.getInsideRootUrl();
        String pathPart = getUrlPathForFileLocation(fileLocation);
        
        String completeUrl = insideRootUrl + pathPart;
        return completeUrl;
    }

    public String getOutsideUrlForFileLocation(String fileLocation) {
        String outsideRootUrl = eac.getOutsideRootUrl();
        String pathPart = getUrlPathForFileLocation(fileLocation);
        
        String completeUrl = outsideRootUrl + pathPart;
        return completeUrl;
    }
}
