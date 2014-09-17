package org.shaigor.rest.retro.service.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(10)
public class OAuth2ResourcesConfigurer extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.requestMatchers().regexMatchers(HttpMethod.GET, "/word/list(\\?.*)?")
		.and()
			.authorizeRequests()
				.regexMatchers(HttpMethod.GET, "/word/list(\\?.*)?").access("hasRole('ROLE_USER')");
	}
}
