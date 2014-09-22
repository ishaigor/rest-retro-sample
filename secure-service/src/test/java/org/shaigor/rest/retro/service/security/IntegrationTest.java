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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Rule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.test.BeforeOAuth2Context;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.util.MultiValueMap;

/**
 * @author Irena Shaigorodsky (ishaigorodsky@enservio.com)
 * Allows re-usage of OAuth test infrastructure
 */
public abstract class IntegrationTest {

	public abstract String getTokenUrl();

	public static int EXPIRY_SEC = 2100;

	protected IntegrationTestHelper helper = new IntegrationTestHelper();
	
    @Rule
    public OAuth2ContextSetup context = OAuth2ContextSetup.standard(helper);
    
	/**
	 * Access resource for given URI and user
	 * @param uri resource URI to access
	 * @param formData 
	 * @param pwd the password of the user
	 * @throws IOException 
	 */
	protected void testPostResourceAccess(TestURI testUri, MultiValueMap<String, String> formData) throws IOException {
		ResponseEntity<String> response =	helper.postForString(testUri.getUri(), formData);
		
        assertEquals(HttpStatus.OK, response.getStatusCode());
        int expiry = context.getAccessToken().getExpiresIn();
        assertTrue("Expiry not overridden in config: " + expiry, expiry > 1500 && expiry <= EXPIRY_SEC);
        assertEquals("Rigth method should be called.", testUri.getResponse(), response.getHeaders().getETag());
	}

	/**
	 * Access resource for given URI and user
	 * @param uri resource URI to access
	 * @param pwd the password of the user
	 * @throws IOException 
	 */
	protected void testResourceAccess(TestURI testUri) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<String> response =	helper.getForResponse(testUri.getUri(), headers);
		
        assertEquals(HttpStatus.OK, response.getStatusCode());
        int expiry = context.getAccessToken().getExpiresIn();
        assertTrue("Expiry not overridden in config: " + expiry, expiry > 1000 && expiry <= EXPIRY_SEC);
        assertEquals("Rigth method should be called.", testUri.getResponse(), response.getBody());
	}

    @BeforeOAuth2Context
    public void setupAccessTokenProvider() {
            ResourceOwnerPasswordAccessTokenProvider accessTokenProvider = helper.createAccessTokenProvider();
            context.setAccessTokenProvider(accessTokenProvider);
    }

	/**
	 * Logic to test unauthorized access to protected resource
	 * @param uri
	 */
	protected void testInvalidTokenErrorMessge(String uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer FOO");
		ResponseEntity<String> response = helper.getForResponse(uri, headers);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		String authenticate = response.getHeaders().getFirst("WWW-Authenticate");
		assertTrue("Wrong header: " + authenticate, authenticate.contains("error=\"invalid_token\""));
	}
}
