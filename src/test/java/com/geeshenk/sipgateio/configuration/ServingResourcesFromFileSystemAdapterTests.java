package com.geeshenk.sipgateio.configuration;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.geeshenk.sipgateio.Application;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ServingResourcesFromFileSystemAdapterTests {

    @Inject
    private ServingResourcesFromFileSystemAdapter srffsa;
    
    @Test
    public void testExternalFileSystemChroot() {
        String someFileSystemLocation = srffsa.getExternalFileSystemChroot() + "foo.txt";
        assertEquals("./ext-file-system-resources/foo.txt", someFileSystemLocation);
    }
    
    @Test
    public void testMappingFromFileSystemLocationToUrlPath() {
        String someFileSystemLocation = srffsa.getExternalFileSystemChroot() + "foo.txt";
        
        String someUrlPath = srffsa.getUrlPathForFileLocation(someFileSystemLocation);
        assertEquals("/ext-url-resources/foo.txt", someUrlPath);
    }
    
    @Test
    public void testMappingFromFileSystemToInsideUrl() {
        String someFileSystemLocation = srffsa.getExternalFileSystemChroot() + "foo.txt";
        
        String insideUrl = srffsa.getInsideUrlForFileLocation(someFileSystemLocation);
        assertEquals("http://localhost:8085/ext-url-resources/foo.txt", insideUrl);
    }
    
    @Test
    public void testMappingFromFileSystemToOutsideUrl() {
        String someFileSystemLocation = srffsa.getExternalFileSystemChroot() + "foo.txt";
        
        String outsideUrl = srffsa.getOutsideUrlForFileLocation(someFileSystemLocation);
        assertEquals("http://localhost:8085/ext-url-resources/foo.txt", outsideUrl);
    }
}
