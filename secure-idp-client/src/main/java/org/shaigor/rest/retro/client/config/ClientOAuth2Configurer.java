package org.shaigor.rest.retro.client.config;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Client
@ComponentScan("org.shaigor.rest.retro.client.oauth")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ClientOAuth2Configurer {

	private AccessTokenProviderChain accessTokenProvider = new AccessTokenProviderChain(
  			Arrays.<AccessTokenProvider> asList(new AuthorizationCodeAccessTokenProvider())); 

//	@Value("${accessTokenUri}")
	private String accessTokenUri = "http://localhost:8080/secure/gateway/v1.0/oauth/token";

//	@Value("${userAuthorizationUri}")
	private String userAuthorizationUri = "http://localhost:8080/secure/gateway/v1.0/oauth/authorize";

	@Resource
	@Qualifier("accessTokenRequest")
	private AccessTokenRequest accessTokenRequest;

	@Bean
	public OAuth2ProtectedResourceDetails gatewayDetails() {
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setId("rest/retro");
		details.setClientId("42be4a10-39ee-11e4-9346-bb86709fcb1d");
		details.setClientSecret("ff02f49a-4052-11e4-9346-bb86709fcb1d");
		details.setAccessTokenUri(accessTokenUri);
		details.setUserAuthorizationUri(userAuthorizationUri);
		details.setScope(Arrays.asList("words"));
		details.setUseCurrentUri(false);
		return details;
	}

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public OAuth2RestTemplate gatewayRestTemplate() {
		OAuth2RestTemplate template  =
				new OAuth2RestTemplate(gatewayDetails(), new DefaultOAuth2ClientContext(accessTokenRequest));
		template.setAccessTokenProvider(accessTokenProvider);
		return template;
	}

//	@Bean
//	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
//	protected AccessTokenRequest accessTokenRequest(@Value("#{request.parameterMap}")
//	Map<String, String[]> parameters, @Value("#{request.getAttribute('currentUri')}")
//	String currentUri) {
//		DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(parameters);
//		request.setCurrentUri(currentUri);
//		return request;
//	}
//	
//	@Configuration
//	protected static class OAuth2ClientContextConfiguration {
//		
//		@Resource
//		@Qualifier("accessTokenRequest")
//		private AccessTokenRequest accessTokenRequest;
//		
//		@Bean
//		@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
//		public OAuth2ClientContext oauth2ClientContext() {
//			return new DefaultOAuth2ClientContext(accessTokenRequest);
//		}
//		
//	}
}
