package com.geeshenk.sipgateio.web;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.geeshenk.commons.ApplicationException;
import com.geeshenk.sipgateio.configuration.ServingResourcesFromFileSystemAdapter;
import com.geeshenk.sipgateio.model.Greeting;
import com.geeshenk.sipgateio.model.Play;
import com.geeshenk.sipgateio.model.SipgateIoRequest;
import com.geeshenk.sipgateio.model.SipgateIoResponse;
import com.geeshenk.sipgateio.service.SipgateIoService;

@Controller
public class SipgateIoController {
    
    private final static Logger logger = LoggerFactory
            .getLogger(SipgateIoController.class);
    
    private SipgateIoService sipgateIoService;
    private ServingResourcesFromFileSystemAdapter servingResourcesFromFileSystemAdapter;
    
    @Value("${local.server.address:localhost}")
    private String host;
    
    @Value("${local.server.port:8083}")
    private String listeningPort;
    
    @Value("${outside.host.and.port:localhost:8080}")
    private String outsideHostAndPort;
    
    @Value("${outside.protocol:http}")
    private String outsideProtocol;
    
    @Inject
    public SipgateIoController(SipgateIoService sipgateIoService, ServingResourcesFromFileSystemAdapter servingResourcesFromFileSystemAdapter) {
        this.sipgateIoService = sipgateIoService;
        this.servingResourcesFromFileSystemAdapter = servingResourcesFromFileSystemAdapter;
    }
    
    @RequestMapping("/")
    @ResponseBody
    public String helloWorld() {
        return this.sipgateIoService.getHelloMessage();
    }
    
    @RequestMapping(value = "/api/currentTime", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public SipgateIoResponse generateCurrentTimeSpeak(
            @ModelAttribute SipgateIoRequest request) throws ApplicationException {

        logger.info("Got repeat request: {} {} {}", request.getDirection(),
                request.getFrom(), request.getTo());
        ZonedDateTime now = ZonedDateTime.now();
        
        String verbalizedCurrentTime = "Time is " + now.getHour() + " " + now.getMinute() + " and " + now.getSecond() + " seconds, on " + 
        now.getMonth() + " " + now.getDayOfMonth() + " " + now.getYear() + ", timezone " + now.getZone();
        String fileLocation = sipgateIoService.getWaveFileLocationForSpokenString(verbalizedCurrentTime);
        logger.info("fileLocation is: {}", fileLocation);
        
        String urlForWaveFile = servingResourcesFromFileSystemAdapter.getOutsideUrlForFileLocation(fileLocation);
        Play locationOfWaveWithCurrentTimeSpeakPlay = new Play(urlForWaveFile);
        SipgateIoResponse locationOfWaveWithCurrentTimeSpeakSipgateIoResponse = new SipgateIoResponse(locationOfWaveWithCurrentTimeSpeakPlay);
        
        return locationOfWaveWithCurrentTimeSpeakSipgateIoResponse;
    }

    @RequestMapping(value = "/api/greeting", method = RequestMethod.POST)
    @ResponseBody
    public String greetingSubmit(@ModelAttribute Greeting greeting) {
        logger.info("Greeting: {} {}", greeting.getId(), greeting.getContent());
        return greeting.getId() + " " + greeting.getContent();
    }
    
    @RequestMapping(value = "/api/generateSpeak", method = RequestMethod.GET)
    @ResponseBody
    public String generateSpeak(@RequestParam("words") String words) throws ApplicationException {
        String spokenWaveFilesystemLocation = sipgateIoService.getWaveFileLocationForSpokenString(words);
        String spokenWaveUrl = servingResourcesFromFileSystemAdapter.getOutsideUrlForFileLocation(spokenWaveFilesystemLocation);
        return spokenWaveUrl;
    }
}
