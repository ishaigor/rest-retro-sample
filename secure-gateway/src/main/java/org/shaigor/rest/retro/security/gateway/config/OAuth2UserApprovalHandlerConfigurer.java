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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;

/**
 * User approvals aspects of configuration
 * 
 * @author Irena Shaigorodsky
 */
@Configuration
public class OAuth2UserApprovalHandlerConfigurer {

	
	@Resource private ClientDetailsService 	clientDetailsService;
	@Resource private DataSource 			securityDataSource;
	
	@Bean public ApprovalStore approvalStore() throws Exception {
		JdbcApprovalStore store = new JdbcApprovalStore(securityDataSource);
		return store;
	}

	
	@Lazy
	@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
	@Bean public ApprovalStoreUserApprovalHandler userApprovalHandler() throws Exception {
		ApprovalStoreUserApprovalHandler handler = new ApprovalStoreUserApprovalHandler();
		handler.setApprovalStore(approvalStore());
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}
}
