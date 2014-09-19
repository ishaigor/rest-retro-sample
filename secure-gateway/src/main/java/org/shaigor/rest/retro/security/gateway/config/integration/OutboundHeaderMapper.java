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
package org.shaigor.rest.retro.security.gateway.config.integration;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @author Irena Shaigorodsky
 * Removes host header that is no longer accurate after the redirect
 */
@Component("outboundHeaderMapper")
public class OutboundHeaderMapper implements
		HeaderMapper<HttpHeaders> {
	
	private static final String HOST = "Host";
	private static String[] HTTP_REQUEST_HEADER_NAMES_OUTBOUND_EXCLUSIONS = new String[] {HOST};

	DefaultHttpHeaderMapper mapper;
	
	public OutboundHeaderMapper() {
		mapper = DefaultHttpHeaderMapper.outboundMapper();
		mapper.setExcludedOutboundStandardRequestHeaderNames(HTTP_REQUEST_HEADER_NAMES_OUTBOUND_EXCLUSIONS);
	}
	

	@Override
	public void fromHeaders(MessageHeaders headers, HttpHeaders target) {
		mapper.fromHeaders(headers, target);
	}

	@Override
	public Map<String, Object> toHeaders(HttpHeaders source) {
		return mapper.toHeaders(source);
	}

}
