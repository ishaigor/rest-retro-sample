/**
 * Copyright 2014 the original author or authors.
 * 
 * This file is part of Retro-fitting Security into REST web services sample (the sample).
 * The sample is part of the talk presented at Java One 2014 
 * 	https://oracleus.activeevents.com/2014/connect/sessionDetail.ww?SESSION_ID=1765&amp;tclass=popup
 * 
 * The sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the sample.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shaigor.rest.retro.service.impl;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * Provides test configuration to be used
 * @author Irena Shaigorodsky
 */
@Configuration
@ComponentScan(basePackages="org.shaigor.rest.retro.service.impl")
public class WordsSereviceTestConfig {
	
	private static final String WORDS_SERVICE_PROPERTIES = "words.service.properties";
	private static final String SRC_PATH 				 = "target/test-classes/";
	private static final String JAR_PATH 				 = ":resource:";

	@Bean public PropertySourcesPlaceholderConfigurer properties(){
	   PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
	   ppc.setLocalOverride(true);
	   String filenamePrefix = determineFilename("words").startsWith(SRC_PATH) ? "src." : "classes.";
	   org.springframework.core.io.Resource[] resources = new ClassPathResource[ ]
	      { new ClassPathResource( filenamePrefix + WORDS_SERVICE_PROPERTIES ) };
	   ppc.setLocations( resources );
	   return ppc;
	}

    /**
     * The location of the.sqlite file will differ depending on
     * what project maven is being run from, either the parent project or a child
     * project. Do a quick test to determine the correct path to the sqlite file.
     *
     * @param filename
     * @return
     */
    private String determineFilename( String filename )
    {
    	if ( new File( SRC_PATH + filename ).exists() ) {
            return SRC_PATH + filename;
        }

        String temp = "../" + SRC_PATH + filename;
        if ( new File( temp ).exists() ) {
            return temp;
        } else if( this.getClass().getClassLoader().getResource(filename) != null) {
        	return JAR_PATH + filename;
        }
        throw new RuntimeException(String.format("File %s doesn't exist anywhere", filename));
    }
 }
