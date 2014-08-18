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
package org.shaigor.rest.retro.config;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.shaigor.rest.retro.service.impl.WordsSereviceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Spring alternative to web.xml
 * 
 * @author Irena Shaigorodsky
 */
public class WebAppInitializer implements WebApplicationInitializer {

	private static Logger LOG = LoggerFactory
			.getLogger(WebAppInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) {
		WebApplicationContext rootContext = createRootContext(servletContext);

		configureSpringMvc(servletContext, rootContext);
	}

	/**
	 * Creates web application context 
	 * @param servletContext to be used during creation and registration
	 * @return web application context for the application
	 */
	private WebApplicationContext createRootContext(
			ServletContext servletContext) {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.getEnvironment().setActiveProfiles("prod");
		Class<?>[] annotatedClasseses = configurations2Register();
		if (annotatedClasseses != null && annotatedClasseses.length > 0) {
			rootContext.register(annotatedClasseses);
		}
		rootContext.refresh();

		servletContext.addListener(new ContextLoaderListener(rootContext));
		servletContext.setInitParameter("defaultHtmlEscape", "true");

		return rootContext;
	}

	/**
	 * Added for an ability to override with additional Spring configurations to be register
	 * in the sample 
	 * @return additional configurations to be register
	 */
	protected Class<?>[] configurations2Register() {
		return null;
	}

	/**
	 * Registers dispatcher servlet that will make MVC tick
	 * @param servletContext to be used during creation and registration
	 * @param rootContext web application context to be used during creation and registration
	 */
	private void configureSpringMvc(ServletContext servletContext,
			WebApplicationContext rootContext) {
		AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
		mvcContext.register(WordsSereviceConfig.class);

		mvcContext.setParent(rootContext);

		ServletRegistration.Dynamic appServlet = servletContext.addServlet(
				"webservice", new DispatcherServlet(mvcContext));
		appServlet.setLoadOnStartup(1);
		Set<String> mappingConflicts = appServlet.addMapping("/");

		if (!mappingConflicts.isEmpty()) {
			for (String s : mappingConflicts) {
				LOG.error("Mapping conflict: " + s);
			}
			throw new IllegalStateException(
					"'webservice' cannot be mapped to '/'");
		}
	}
}
