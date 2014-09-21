package org.shaigor.rest.retro.service.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfigurer extends
		ResourceServerConfigurerAdapter {

	private static final String SERVICE_RESOURCE_ID = "rest-retro";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(SERVICE_RESOURCE_ID);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.regexMatchers(HttpMethod.GET, "/word/list(\\?.*)?")
					.access("hasIpAddress('127.0.0.1') or (#oauth2.hasScope('words') and hasRole('ROLE_USER'))")
				;
	}
}
