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
package org.shaigor.rest.retro.client.oauth;

import java.util.Arrays;

import org.shaigor.rest.retro.auth.CustomAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
/**
 * Piggybacks on authentication to acquire OAuth token with the credentials
 * @author Irena Shaigorodsky
 *
 */
public class OAuthPostAuthListener implements ApplicationListener<AuthenticationSuccessEvent>{

	private static Logger log = LoggerFactory.getLogger(OAuthPostAuthListener.class);

	private AccessTokenProviderChain accessTokenProvider = new AccessTokenProviderChain(
  			Arrays.<AccessTokenProvider> asList(new ResourceOwnerPasswordAccessTokenProvider())); 

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		Authentication authentication = event.getAuthentication();

		  ResourceOwnerPasswordResourceDetails resource = getResourceOwnerPasswordResourceDetails();
		  resource.setScope(Arrays.asList("words"));
		  resource.setUsername(authentication.getName());
		  resource.setPassword(authentication.getCredentials().toString());

		  try {
			  OAuth2AccessToken accessToken = accessTokenProvider.obtainAccessToken(resource, new DefaultAccessTokenRequest());
			  log.debug("Access token request succeeded for user: '{}', new token is '{}'"
					  , resource.getUsername() 
					  , accessToken.getValue());
			  if (authentication instanceof AbstractAuthenticationToken && authentication.getDetails() instanceof CustomAuthenticationDetails) {
				  ((CustomAuthenticationDetails) ((AbstractAuthenticationToken) authentication).getDetails())
				  	.setBearer(accessToken.getValue());
				  log.debug("Access token was added to authentication as details");
			  } else if (log.isDebugEnabled()) {
				  log.debug("Access token could not be added to authentication as details");
			  }
		  } catch (Exception e) {
			  log.error("Access token request failed for user: '" + resource.getUsername() + "'", e);
		  }
		
		if (authentication instanceof CredentialsContainer) {
            // Authentication is complete. Remove credentials and other secret data from authentication
            ((CredentialsContainer)authentication).eraseCredentials();
        }
		
	}

    /**
     * @return {@link org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails} 
     * with token URL, client id and secret set
     */
	private ResourceOwnerPasswordResourceDetails getResourceOwnerPasswordResourceDetails() {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
	
		resource.setAccessTokenUri("http://localhost:8080/secure/service/v1.0/oauth/token");
		resource.setClientId("35221af8-39ee-11e4-9346-bb86709fcb1d");
		resource.setClientSecret("3c0dfe9a-39ee-11e4-9346-bb86709fcb1d");
		return resource;
	}
}
