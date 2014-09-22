package org.shaigor.rest.retro.service.security;
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


/**
 * @author Irena Shaigorodsky (ishaigorodsky@enservio.com)
 *	Represents requiest URI for testing perposes along with expected response
 */
public class URIInfo {

	private String uri;
	private String response;

	public URIInfo(String uri, String response) {
		this.uri = uri;
		this.response = response;
	}
	
	/**
	 * @return the uri to test
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @return the response to expect
	 */
	public String getResponse() {
		return response;
	}

}
