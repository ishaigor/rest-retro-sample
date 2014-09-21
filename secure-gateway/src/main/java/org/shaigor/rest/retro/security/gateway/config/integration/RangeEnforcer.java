package org.shaigor.rest.retro.security.gateway.config.integration;

import static org.shaigor.rest.retro.security.gateway.oauth.CustomSecurityExpressionMethods.ROLE_WORDS_DEMO;

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
	private static final int MAX_DEMO_WORDS = 1000;
	private static Logger log = LoggerFactory.getLogger(RangeEnforcer.class);
	protected static final String ROLE_MAX_WORDS = "ROLE_MAX_WORDS_";
	private static Pattern toRangePattern = Pattern.compile("(.*[\\?&]?to=)([1-9][0-9]*)(.*)");
	private static Pattern fromRangePattern = Pattern.compile("(.*[\\?&]?from=)([1-9][0-9]*)(.*)");

	/**
	 * Adds user id as a request parameter
	 * @param queryString
	 * @return the result of the enrichment
	 */
	public String enrichQueryString(String queryString) {
		int maxRange = getMaxRange();
		return enforceRange(enforceRange(queryString
				, maxRange, fromRangePattern, "from", false), maxRange, toRangePattern, "to");
	  }

	/**
	 * The logic of range auto-corrections
	 * @param queryString original string to correct
	 * @param maxRange - the cap on the range
	 * @param rangePattern - the pattern to locate the appearance of the range
	 * @param verb - the attribute verb
	 * @return auto-corrected string
	 */
	private String enforceRange(String queryString, int maxRange, Pattern rangePattern, String verb) {
		return enforceRange(queryString, maxRange, rangePattern, verb, true);
	}

	/**
	 * The logic of range auto-corrections
	 * @param queryString original string to correct
	 * @param maxRange - the cap on the range
	 * @param rangePattern - the pattern to locate the appearance of the range
	 * @param verb - the attribute verb
	 * @param addMissing - should the correction be added if not specified
	 * @return auto-corrected string
	 */
	private String enforceRange(String queryString, int maxRange, Pattern rangePattern, String verb, boolean addMissing) {
		Matcher matcher = rangePattern.matcher(queryString);
		if (matcher != null && matcher.matches()) {
			int requestedRange = Integer.parseInt(matcher.group(2));
			
			return matcher.group(1) + Math.min(maxRange, requestedRange) + matcher.group(3);
		} else if (maxRange != Integer.MAX_VALUE) {
			if (!StringUtils.isEmpty(queryString) && !"?".equals(queryString)) {
				return queryString + "&"+ verb + "=" + maxRange;
			} else if (addMissing){
				return "?" + verb + "=" + maxRange;
			} else {
				return queryString;
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
				if (role.getAuthority() != null) {
					if (role.getAuthority().startsWith(ROLE_MAX_WORDS)) {
						try {
							maxRange = Integer.parseInt(role.getAuthority().substring(ROLE_MAX_WORDS.length()));
						} catch (NumberFormatException e) {
							log.error("Unparsable range: " + role.getAuthority());
						}
					} else if (ROLE_WORDS_DEMO.equals(role.getAuthority())) {
						maxRange = MAX_DEMO_WORDS;
					}
				}
			}
			
		}
		return maxRange;
	}

}
