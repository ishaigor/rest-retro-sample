package org.shaigor.rest.retro.security.gateway.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigurer extends
		AuthorizationServerConfigurerAdapter {

	@Resource private TokenStore 				tokenStore;
	@Resource private UserApprovalHandler 		userApprovalHandler;
	@Resource private DataSource 				securityDataSource;
	@Resource private ClientDetailsService 		clientDetailsService;
	@Resource private AuthorizationCodeServices authorizationCodeServices;

	@Resource(name="exposedAuthenticationManager") 
	private AuthenticationManager authenticationManager;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		
		clients
			.jdbc(securityDataSource)
			;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		if (clientDetailsService instanceof JdbcClientDetailsService) {
			((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(new StandardPasswordEncoder());
		}
		endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
				.authenticationManager(authenticationManager)
				.clientDetailsService(clientDetailsService)
				.authorizationCodeServices(authorizationCodeServices)
				;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer
			.realm("rest/retro")
			.allowFormAuthenticationForClients()
			;
	}

}
