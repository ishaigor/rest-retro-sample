package org.shaigor.rest.retro.security.gateway.config;

import static org.shaigor.rest.retro.security.gateway.oauth.CustomSecurityExpressionMethods.ROLE_WORDS_DEMO;
import static org.shaigor.rest.retro.security.gateway.oauth.CustomSecurityExpressionMethods.ROLE_WORDS_PRODUCTION;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.FilterInvocation;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfigurer extends
		ResourceServerConfigurerAdapter {

	private static final String SERVICE_RESOURCE_ID = "rest-retro";

	@Resource(name="demoOAuth2WebSecurityExpressionHandler")
	private SecurityExpressionHandler<FilterInvocation> expressionHandler;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(SERVICE_RESOURCE_ID);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests().expressionHandler(expressionHandler)
				.regexMatchers(HttpMethod.GET, "/word/list(\\?.*)?")
					.access("#oauth2.hasScope('words') and hasRole('ROLE_USER') and hasAnyRole('"
							+ ROLE_WORDS_DEMO +"','" + ROLE_WORDS_PRODUCTION +"')")
				;
	}
}
