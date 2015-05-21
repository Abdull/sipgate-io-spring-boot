package com.geeshenk.sipgateio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.inject.Inject;

import junitx.framework.FileAssert;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.geeshenk.sipgateio.Application;
import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertEquals;


@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
//@WebAppConfiguration
//@IntegrationTest("server.port:0")
//@WebIntegrationTest({"server.port=0", "management.port=0"})
@WebIntegrationTest()
@DirtiesContext
public class SipgateIoIntegrationTests {

    private final static Logger logger = LoggerFactory.getLogger(SipgateIoIntegrationTests.class);
    
    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();
    
    @Value("${local.server.port}")
    private int port;
    
    @Inject
    private EndpointAddressComponent eac;
    
    private RestTemplate restTemplate = new TestRestTemplate();
    
    @Test
    public void testHome() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
                eac.getInsideRootUrl(), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals("Hello World", entity.getBody());
    }

    @Test
    public void testAccessToSoundFile() throws IOException {
        //logger.info("working directory: {}", System.getProperty("user.dir") );
        URL url = this
                .getClass()
                .getResource(
                        "/sound_files/hello_world/b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9_processed.wav");
        File expectedHelloWorldWaveFile = new File(url.getFile());
        
        //String usedUrl = "http://localhost:" + port + "/api/generateSpeak?words={words}";
        String usedUrl = eac.getInsideRootUrl() + "/api/generateSpeak?words={words}";
        assertEquals("http://localhost:8085/api/generateSpeak?words={words}", usedUrl);
        
        Map<String, String> helloWorldQueryParameters = ImmutableMap.<String, String>builder().put("words","hello world").build();
        ResponseEntity<String> urlResponse = restTemplate.getForEntity(usedUrl, String.class, helloWorldQueryParameters);
        
        String actualHelloWorldWaveFileUrlString = urlResponse.getBody();
        assertEquals("http://localhost:8085/ext-url-resources/b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9_processed.wav", actualHelloWorldWaveFileUrlString);
        
        URL actualHelloWorldWaveFileUrl = new URL(actualHelloWorldWaveFileUrlString);
        
        File actualHelloWorldWaveFile = tempDirectory.newFile("actualHelloWorldWaveFile.wav");
        FileUtils.copyURLToFile(actualHelloWorldWaveFileUrl, actualHelloWorldWaveFile);
        
        FileAssert.assertBinaryEquals(expectedHelloWorldWaveFile, actualHelloWorldWaveFile);
    }
    
}
