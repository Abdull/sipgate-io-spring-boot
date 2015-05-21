package com.geeshenk.sipgateio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * following three annotations are all included through @SpringBootApplication:
 * @Configuration
 * @EnableAutoConfiguration
 * @ComponentScan
 */
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(Application.class, args);
        SpringApplication springApplication = new SpringApplication(Application.class);
        
        springApplication.addListeners( new ApplicationPidFileWriter() );
        springApplication.run(args);
    }

}
