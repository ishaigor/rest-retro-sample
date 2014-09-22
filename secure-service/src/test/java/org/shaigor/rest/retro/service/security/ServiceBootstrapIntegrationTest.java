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
package org.shaigor.rest.retro.service.security;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.shaigor.rest.retro.service.security.IntegrationTestHelper.ResourceOwner;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test bootstrapping
 * @author Irena Shaigorodsky
 * @since  1.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigWebContextLoader.class)
@ComponentScan("org.shaigor.rest.retro.service.security")
@WebAppConfiguration
@Configurable
@ActiveProfiles("prod")
public class ServiceBootstrapIntegrationTest extends IntegrationTest {

//    private MockMvc mockMvc;
//
//    @Resource
//    private WebApplicationContext wac;
//
//    @Before
//    public void setup() throws Exception {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//    }

    @Test
    @OAuth2ContextConfiguration(ResourceOwnerAuthorityInScope.class)
	public void testRootContext() throws Exception{
    	testResourceAccess(new URIInfo("/secure/service/v1.0/word/list?from=1&to=2", "[\"a\"]"));
	}

	static class ResourceOwnerAuthorityInScope extends ResourceOwner {
        public ResourceOwnerAuthorityInScope(Object targetObject) {
                super(targetObject);
                setClientSecret("3c0dfe9a-39ee-11e4-9346-bb86709fcb1d");
                setUsername("Ashlie");
                setPassword("123456");
                setScope(Arrays.asList("words"));
        }
    }

	@Override
	public String getTokenUrl() {
		return "http://localhost:8080/secure/service/v1.0/oauth/token";
	}
}
