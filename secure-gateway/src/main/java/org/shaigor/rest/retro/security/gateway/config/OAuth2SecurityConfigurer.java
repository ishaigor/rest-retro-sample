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
package org.shaigor.rest.retro.security.gateway.config;

import static org.shaigor.rest.retro.security.gateway.oauth.CustomSecurityExpressionMethods.ROLE_WORDS_DEMO;
import static org.shaigor.rest.retro.security.gateway.oauth.CustomSecurityExpressionMethods.ROLE_WORDS_PRODUCTION;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages={"org.shaigor.rest.retro.security.gateway.oauth"})
/**
 * Web Security aspects of configuration
 * 
 * @author Irena Shaigorodsky
 */
public class OAuth2SecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Resource private DataSource securityDataSource;
	@Resource private String 	 groupAuthoritiesByUsernameSql;
	
	@Resource(name="demoOAuth2WebSecurityExpressionHandler")
	private SecurityExpressionHandler<FilterInvocation> expressionHandler;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.expressionHandler(expressionHandler)
			.ignoring().antMatchers("/oauth/uncache_approvals", "/oauth/cache_approvals");
	}

    
	@Bean(name="exposedAuthenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
    	.jdbcAuthentication()
        .dataSource(securityDataSource)
        .rolePrefix("ROLE_")
        .groupAuthoritiesByUsername(groupAuthoritiesByUsernameSql)
        .passwordEncoder(new StandardPasswordEncoder())
        ;
	}
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
		    .authorizeRequests().antMatchers("/login.jsp").permitAll().and()
	        .authorizeRequests()
	        	.expressionHandler(expressionHandler)
	            .anyRequest()
	            .hasRole("USER")
	            .and()
	        .authorizeRequests()
				.regexMatchers(HttpMethod.GET, "/word/list(\\?.*)?")
					.access("#oauth2.hasScope('words') and hasRole('ROLE_USER') " 
							//+ "and hasAnyRole('"+ ROLE_WORDS_DEMO +"','" + ROLE_WORDS_PRODUCTION +"') " 
							+ "and #wordsServiceAuthorizer.accessAllowed()")
				.and()
	        .exceptionHandling()
	            .accessDeniedPage("/login.jsp?authorization_error=true")
	            .and()
	        // TODO: put CSRF protection back into this endpoint
	        .csrf()
	            .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable()
	        .logout()
	            .logoutSuccessUrl("/index.jsp")
	            .logoutUrl("/logout.do")
	            .and()
	        .formLogin()
	                .usernameParameter("j_username")
	                .passwordParameter("j_password")
	                .failureUrl("/login.jsp?authentication_error=true")
	                .loginPage("/login.jsp")
	                .loginProcessingUrl("/j_spring_security_check");
    }
}