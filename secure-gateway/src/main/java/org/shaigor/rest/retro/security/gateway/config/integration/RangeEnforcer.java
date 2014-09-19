package org.shaigor.rest.retro.security.gateway.config.integration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("rangeEnforcer")
public class RangeEnforcer {
	private static Logger log = LoggerFactory.getLogger(RangeEnforcer.class);
	protected static final String ROLE_MAX_WORDS = "ROLE_MAX_WORDS_";
	private static Pattern rangePattern = Pattern.compile("(.*[\\?&]?to=)([1-9][0-9]*)(.*)");

	/**
	 * Adds user id as a request parameter
	 * @param queryString
	 * @return the result of the enrichment
	 */
	public String enrichQueryString(String queryString) {
		int maxRange = getMaxRange();
		Matcher matcher = rangePattern.matcher(queryString);
		if (matcher != null && matcher.matches()) {
			int requestedRange = Integer.parseInt(matcher.group(2));
			
			return matcher.group(1) + Math.min(maxRange, requestedRange) + matcher.group(3);
		} else if (maxRange != Integer.MAX_VALUE) {
			if (!StringUtils.isEmpty(queryString) && !"?".equals(queryString)) {
				return queryString + "&to=" + maxRange;
			} else {
				return "?to=" + maxRange;
			}
		} else {
			return queryString;
		}
	  }

	/**
	 * Returns max range for the request based on the role assignment 
	 * @return maximum range allowed
	 */
	private int getMaxRange() {
		int maxRange = Integer.MAX_VALUE;
		if (SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			for (GrantedAuthority role: auth.getAuthorities()) {
				if ((role.getAuthority() != null) && role.getAuthority().startsWith(ROLE_MAX_WORDS)) {
					try {
						maxRange = Integer.parseInt(role.getAuthority().substring(ROLE_MAX_WORDS.length()));
					} catch (NumberFormatException e) {
						log.error("Unparsable range: " + role.getAuthority());
					}
					return maxRange;
				}
			}
			
		}
		return maxRange;
	}

}
