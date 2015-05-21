package com.geeshenk.sipgateio.model;

import javax.xml.bind.annotation.XmlElement;

public class Play {

    private String url;
    
    public Play() {
    }
    
    public Play(String url) {
        this.url = url;
    }

    @XmlElement(name = "Url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
