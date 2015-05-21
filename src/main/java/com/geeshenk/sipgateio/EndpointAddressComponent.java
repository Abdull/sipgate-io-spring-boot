package com.geeshenk.sipgateio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EndpointAddressComponent {

    @Value("${inside.protocol:http}")
    private String insideProtocol;
    
    @Value("${server.address:localhost}")
    private String insideServerHost;
    
    //@Value("${local.server.port}")
    @Value("${server.port}")
    private String insideServerPort;
    
    
    @Value("${outside.protocol:http}")
    private String outsideProtocol;
    
    @Value("${outside.server.address}")
    private String outsideServerHost;
    
    @Value("${outside.server.port}")
    private String outsideServerPort;
    
    public String getString() {
        return "hello";
    }
    
    public String getInsideRootUrl() {
        return insideProtocol + "://" + insideServerHost + ":" + insideServerPort;
    }
    
    public String getOutsideRootUrl() {
        String portPart = outsideServerPort.equals("80")? 
                "" :
                ":" + outsideServerPort; 
        return outsideProtocol + "://" + outsideServerHost + portPart;
    }
}
