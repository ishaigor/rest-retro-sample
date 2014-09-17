package org.shaigor.rest.retro.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomAuthenticationDetails extends WebAuthenticationDetails {
	private static final long serialVersionUID = 1L;
	
	private String bearer;

	public CustomAuthenticationDetails(HttpServletRequest request) {
		super(request);
	}

	public String getBearer() {
		return bearer;
	}

	public void setBearer(String bearer) {
		this.bearer = bearer;
	}

}
