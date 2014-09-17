package org.shaigor.rest.retro.service.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.support.MBeanServerFactoryBean;

@Configuration
public class JMXConfiguration {

	@Bean public MBeanServerFactoryBean mbeanServer() {
		MBeanServerFactoryBean server = new MBeanServerFactoryBean();
		server.setDefaultDomain(JMXConfiguration.class.getPackage().getName());
		server.setLocateExistingServerIfPossible(true);
		return server;
	}
}
