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

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.shaigor.rest.retro.config.PersistenceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.client.token.JdbcClientTokenServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Persistence aspects of OAuth 2.0 configuration
 * 
 * @author Irena Shaigorodsky
 */
@Configuration
@Import({PersistenceConfiguration.class})
public class OAuth2PersistenceConfiguration {
	@Resource private DataSource securityDataSource;

	/**
	 * The token store is part of common database 
	 * @return JDBC based token store
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(securityDataSource);
	}

	/**
	 * 
	 * @return JDBC based client token service
	 */
	@Bean public ClientTokenServices customClientTokenServices() {
		return new JdbcClientTokenServices(securityDataSource);
	}
	
	/**
	 * 
	 * @return JDBC based authorization code service
	 */
	@Bean public AuthorizationCodeServices customAuthorizationCodeService() {
		return new JdbcAuthorizationCodeServices(securityDataSource);
	}
	
}
