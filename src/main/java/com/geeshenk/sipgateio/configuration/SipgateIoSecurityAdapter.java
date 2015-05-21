package com.geeshenk.sipgateio.configuration;

import javax.inject.Inject;

import com.geeshenk.sipgateio.configuration.ServingResourcesFromFileSystemAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
public class SipgateIoSecurityAdapter extends WebSecurityConfigurerAdapter {
    
    private final static Logger logger = LoggerFactory.getLogger(SipgateIoSecurityAdapter.class);
    
    /*
     * How come we don't do constructor injection? That is because the Spring framework somehow requires
     * us to have a default constructor for this class.
     */
    @Inject
    private ServingResourcesFromFileSystemAdapter fsAdapter;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        String allExtResourcesPath = fsAdapter.getExternalResourcesHttpRootPath() + "**";
        
        logger.info("allExtResourcesPath: {}", allExtResourcesPath);
        http
          .authorizeRequests()
            .antMatchers("/", "/api/**", fsAdapter.getExternalResourcesHttpRootPath() + "**").permitAll()
            .anyRequest().authenticated() // including all the actuator and metrics endpoints
            
            .and()
              .httpBasic()
              
            .and()
              .csrf().disable();
    }
}
