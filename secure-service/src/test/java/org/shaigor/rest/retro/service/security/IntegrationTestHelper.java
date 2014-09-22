/**
 * (C) Copyright Enservio, Inc., 2013. All Rights Reserved.
 * This file is a proprietary, confidential trade secret of Enservio, Inc.
 *
 * @author Irena Shaigorodsky (ishaigorodsky@enservio.com)
 * @created January 09, 2014
 */
package org.shaigor.rest.retro.service.security;


import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.test.RestTemplateHolder;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Irena Shaigorodsky (ishaigorodsky@enservio.com)
 * Collection of auxilary functions for integration testing 
 */
public class IntegrationTestHelper implements RestTemplateHolder {

	private static final String DEFAULT_HOST = "localhost";
	private static final String DEFAULT_PORT = "8080";
	private RestOperations client;
	private String hostName;
	private String port;
	public static String CLIENT_ID = "35221af8-39ee-11e4-9346-bb86709fcb1d";
	public static String CLIENT_SECRET = "3c0dfe9a-39ee-11e4-9346-bb86709fcb1d";
	public static final String TOKEN_URI = "/oauth/token";

    private ClientHttpResponse tokenEndpointResponse;
	
	public IntegrationTestHelper() {
		this(DEFAULT_HOST, System.getProperty("jetty.port", DEFAULT_PORT));
	}
	public IntegrationTestHelper(String hostName, String port) {
		this.hostName = hostName;
		this.port = port;
		 client = createRestTemplate();
	}
	
	
	/**
	 * @see org.springframework.security.oauth2.client.test.RestTemplateHolder#setRestTemplate(org.springframework.web.client.RestOperations)
	 */	
	@Override
	public void setRestTemplate(RestOperations restTemplate) {
		client = restTemplate;
	}
	
	/**
	 * @see org.springframework.security.oauth2.client.test.RestTemplateHolder#getRestTemplate()
	 */
	@Override
	public RestOperations getRestTemplate() {
		return client;
	}

	public RestOperations createRestTemplate() {
		RestTemplate client = new RestTemplate();
		client.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createMinimal()) {
			@Override
			public HttpClient getHttpClient() {
				HttpClient client = super.getHttpClient();
				client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
				return client;
			}
		});
		client.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response)
					throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		return client;
	}

	public HttpStatus getStatusCode(String path, final HttpHeaders headers) {
        ResponseEntity<String> response = getForResponse(path, headers);
        return response.getStatusCode();
	}

	public HttpStatus getStatusCode(String path) {
	        return getStatusCode(getUrl(path), null);
	}

    public ResponseEntity<String> getForResponse(String path, final HttpHeaders headers, Map<String, String> uriVariables) {
        HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
        return client.exchange(getUrl(path), HttpMethod.GET, request, String.class, uriVariables);
	}
	
	public ResponseEntity<String> getForResponse(String path, HttpHeaders headers) {
	        return getForResponse(path, headers, Collections.<String, String> emptyMap());
	}
	
	public String getUrl(String path) {
        if (path.startsWith("http")) {
                return path;
        }
        if (!path.startsWith("/")) {
                path = "/" + path;
        }
        return "http://" + hostName + ":" + port + path;
	}

	public ResponseEntity<String> postForString(String path, MultiValueMap<String, String> formData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return client.exchange(getUrl(path), HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData,
                        headers), String.class);
	}

    public static class ResourceOwnerNoSecretProvided extends ResourceOwner {
        public ResourceOwnerNoSecretProvided(Object target) {
                super(target);
        }
    }

    public static class ResourceOwnerSecretProvided extends ResourceOwner {
        public ResourceOwnerSecretProvided(Object targetObject) {
                super(targetObject);
                setClientSecret(CLIENT_SECRET);
        }
    }

    public static class ResourceOwner extends ResourceOwnerPasswordResourceDetails {
        public ResourceOwner(Object targetObject) {
        	IntegrationTest target = (IntegrationTest) targetObject;
                setClientId(CLIENT_ID);
                setId(getClientId());
                setAccessTokenUri(target.getTokenUrl());
        }
    }

	/**
	 * @return AccessTokenProvider
	 */
	public ResourceOwnerPasswordAccessTokenProvider createAccessTokenProvider() {
		return new ResourceOwnerPasswordAccessTokenProvider() {

		        private ResponseExtractor<OAuth2AccessToken> extractor = super.getResponseExtractor();

		        private ResponseErrorHandler errorHandler = super.getResponseErrorHandler();
		        

		        @Override
		        protected ResponseErrorHandler getResponseErrorHandler() {
		                return new DefaultResponseErrorHandler() {
		                        public void handleError(ClientHttpResponse response) throws IOException {
		                                response.getHeaders();
		                                response.getStatusCode();
		                                setTokenEndpointResponse(response);
//		                                errorHandler.handleError(response);
		                        }
		                };
		        }

		        @Override
		        protected ResponseExtractor<OAuth2AccessToken> getResponseExtractor() {
		                return new ResponseExtractor<OAuth2AccessToken>() {

		                        public OAuth2AccessToken extractData(ClientHttpResponse response) throws IOException {
		                                response.getHeaders();
		                                response.getStatusCode();
		                                setTokenEndpointResponse(response);
		                                return extractor.extractData(response);
		                        }

		                };
		        }
		};
	}
	/**
	 * @return the tokenEndpointResponse
	 */
	public ClientHttpResponse getTokenEndpointResponse() {
		return tokenEndpointResponse;
	}
	/**
	 * @param tokenEndpointResponse the tokenEndpointResponse to set
	 */
	private void setTokenEndpointResponse(ClientHttpResponse tokenEndpointResponse) {
		this.tokenEndpointResponse = tokenEndpointResponse;
	}
	
	/**
	 * 
	 * @return String with the body of the response
	 * @throws IOException
	 */
	public String getResponseBody() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(tokenEndpointResponse.getBody(), writer, "UTF-8");
		return writer.toString();
	}
	
	public String getResponseETag() {
		return tokenEndpointResponse.getHeaders().getETag();
	}

}
