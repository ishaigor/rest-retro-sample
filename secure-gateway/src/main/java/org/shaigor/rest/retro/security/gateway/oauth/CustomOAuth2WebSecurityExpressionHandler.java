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
package org.shaigor.rest.retro.security.gateway.oauth;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

/**
 * Allows plugging in custom authorization checker
 * @author Irena Shaigorodsky
 *
 */
@Component("demoOAuth2WebSecurityExpressionHandler")
public class CustomOAuth2WebSecurityExpressionHandler extends
	OAuth2WebSecurityExpressionHandler {

	@Override
	/**
	 * Calculates role out of the request path info
	 * Puts relative path info out for re-use
	 * @param authentication
	 * @param invocation
	 * @see org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler.createEvaluationContextInternal(Authentication, FilterInvocation)
	 * @return evaluation context with role and path variables defined
	 */ 
	public StandardEvaluationContext createEvaluationContextInternal(Authentication authentication,
			FilterInvocation invocation) {
		StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, invocation);
		ec.setVariable("wordsServiceAuthorizer", new CustomSecurityExpressionMethods(authentication) );
		return ec;
	}


}
