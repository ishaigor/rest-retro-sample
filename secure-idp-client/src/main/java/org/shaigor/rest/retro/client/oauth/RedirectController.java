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
package org.shaigor.rest.retro.client.oauth;

import javax.annotation.Resource;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
/**
 * The redirect callback to launch the JavaScript
 * @author Irena Shaigorodsky
 *
 */
public class RedirectController {

	@Resource
	private OAuth2RestTemplate oauthRestTemplate;

	@RequestMapping("/oauth/redirect")
	/**
	 * Action on grant code
	 * @param code - grant code
	 * @param state - oauth state
	 * @param request - http request just in case
	 * @return view name
	 */
	public String redirect(@RequestParam("code") String code
			, @RequestParam("state") String state) {
		if (!StringUtils.isEmpty(code)) {
			oauthRestTemplate.getOAuth2ClientContext().getAccessTokenRequest().setAuthorizationCode(code);
		}
		if (!StringUtils.isEmpty(state)) {
			oauthRestTemplate.getOAuth2ClientContext().getAccessTokenRequest().setStateKey(state);
		}
		return "index";
		
	}
}
