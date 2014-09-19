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
package org.shaigor.rest.retro.client.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.shaigor.rest.retro.auth.CustomAuthenticationDetailSource;
import org.shaigor.rest.retro.config.PersistenceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@EnableWebSecurity
/**
 * Web Security aspects of configuration
 * 
 * @author Irena Shaigorodsky
 */
@ComponentScan("org.shaigor.rest.retro.client.oauth")
@Import({PersistenceConfiguration.class})
public class ClientSecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Resource private DataSource securityDataSource;
	@Resource private String 	 groupAuthoritiesByUsernameSql;

	@Override
    public void configure( AuthenticationManagerBuilder auth) throws Exception {
        auth.
        	eraseCredentials(false) // Credentials will be erased once OAuth token is obtained
        	.jdbcAuthentication()
            .dataSource(securityDataSource)
            .rolePrefix("ROLE_")
            .groupAuthoritiesByUsername(groupAuthoritiesByUsernameSql)
            .passwordEncoder(new StandardPasswordEncoder())
            ;
    }

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/js/**");
	}

    @Override
    @Bean public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    

    @Override
	protected void configure(HttpSecurity http) throws Exception {
    	http
    		.authorizeRequests().antMatchers("/login.jsp").permitAll().and()
            .authorizeRequests()
                .anyRequest().hasRole("USER")
                .and()
            .exceptionHandling()
                .accessDeniedPage("/login.jsp?authorization_error=true")
                .and()
            .csrf(). // TODO CSRF token repository
            	and()
            .logout()
                .logoutSuccessUrl("/login.jsp")
                .logoutUrl("/logout.do")
                .permitAll()
                .and()
            .formLogin()
            	.authenticationDetailsSource(new CustomAuthenticationDetailSource())
                .loginPage("/login.jsp")
                .loginProcessingUrl("/j_spring_security_check")
                .failureUrl("/login.jsp?authentication_error=true")
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .permitAll()
                ;
	}
}