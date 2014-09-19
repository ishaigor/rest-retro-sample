package org.shaigor.rest.retro.security.gateway.config.integration;

import static org.fest.assertions.Assertions.assertThat;
import static org.shaigor.rest.retro.security.gateway.config.integration.RangeEnforcer.ROLE_MAX_WORDS;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

public class RangeEnforcerTest {
	
	private RangeEnforcer enforcer = new RangeEnforcer();
	private SecurityContextImpl context = null;
	
	@Before
	public void init() {
		context = new SecurityContextImpl();
		User principal = new User("USER1", "USER1", Arrays.asList(new SimpleGrantedAuthority(ROLE_MAX_WORDS + 100)));
		Authentication authentication = new TestingAuthenticationToken(principal , null,ROLE_MAX_WORDS + 100);
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	@Test
	public void testAddRange() {
		String queryString = "?";
		String result = enforcer.enrichQueryString(queryString);
		assertThat(result).isEqualTo("?to=100");
	}

	@Test
	public void testReplaceRange() {
		String queryString = "?from=50&to=150";
		String result = enforcer.enrichQueryString(queryString);
		assertThat(result).isEqualTo("?from=50&to=100");
	}

	@Test
	public void testRangeNoChange() {
		String queryString = "?from=1&to=50";
		String result = enforcer.enrichQueryString(queryString);
		assertThat(result).isEqualTo("?from=1&to=50");
	}

	@Test
	public void testRangeNoInfo() {
		context = new SecurityContextImpl();
		User principal = new User("USER1", "USER1", new ArrayList<GrantedAuthority>());
		Authentication authentication = new TestingAuthenticationToken(principal , null);
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		String queryString = "?from=50&to=150";
		String result = enforcer.enrichQueryString(queryString);
		assertThat(result).isEqualTo("?from=50&to=150");
	}
	
}
