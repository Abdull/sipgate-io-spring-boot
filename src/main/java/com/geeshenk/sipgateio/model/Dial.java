package com.geeshenk.sipgateio.model;

import javax.xml.bind.annotation.XmlElement;


public class Dial implements ResponseType {

    private String number;
    
    //no-arg constructor required for JAXB2
    public Dial() {
    }
    
    public Dial(String number) {
        this.number = number;
    }
    
    public void setNumber(String number) {
        this.number = number;
    }
    
    @XmlElement(name = "Number")
    public String getNumber() {
        return number;
    }
    
}
