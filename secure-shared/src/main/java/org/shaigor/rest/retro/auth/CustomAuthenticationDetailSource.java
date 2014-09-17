package org.shaigor.rest.retro.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class CustomAuthenticationDetailSource extends
		WebAuthenticationDetailsSource {

	public CustomAuthenticationDetails buildDetails(HttpServletRequest context) {
		CustomAuthenticationDetails detail = new CustomAuthenticationDetails(context);
        return detail;
	}
}
