package com.geeshenk.sipgateio.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Response")
public class SipgateIoResponse {
    
    private Dial dial;
    private Play play;
    
    //no-arg constructor required for JAXB2
    public SipgateIoResponse() {
    }
    
    public SipgateIoResponse(Dial dial) {
        this.dial = dial;
    }
    
    public SipgateIoResponse(Play play) {
        this.play = play;
    }

    @XmlElement(name = "Dial")
    public Dial getDial() {
        return dial;
    }

    public void setDial(Dial dial) {
        this.dial = dial;
    }

    @XmlElement(name = "Play")
    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }
    
    
}
