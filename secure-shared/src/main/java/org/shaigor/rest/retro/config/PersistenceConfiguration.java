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

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@Configuration
/**
 * Persistence aspects of configuration
 * 
 * @author Irena Shaigorodsky
 */
@WebListener
public class PersistenceConfiguration implements ServletContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceConfiguration.class);
	
	private static final String GROUP_AUTHORITIES_BY_USERNAME_SQL = "select g.id, g.group_name, ga.authority " + 
			"from groups g, group_members gm, group_authorities ga " + 
			"where gm.username = ? and g.id = ga.group_id and g.id = gm.group_id;";
	
	private BasicDataSource securityDataSource;

	
	@PostConstruct
	/**
	 * Creates security data-source to be used with the sample DB 
	 */
	public void init() {
        securityDataSource = new BasicDataSource();
        securityDataSource.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
        securityDataSource.setUrl("jdbc:mysql://localhost:3306/java_one_2014");
        securityDataSource.setUsername("java_one");
        securityDataSource.setPassword("");
		securityDataSource.setInitialSize(5);
        securityDataSource.setMaxTotal(30);
        securityDataSource.setMaxIdle(15);
        securityDataSource.setMaxWaitMillis(3000);
        securityDataSource.setLogAbandoned(true);
        securityDataSource.setTestWhileIdle(true);
        securityDataSource.setTestOnBorrow(true);
        securityDataSource.setValidationQuery("select 1");
	}
	
	/**
	 * Proper dispose of the data source and MySQL threads
	 * @throws SQLException
	 */
	@PreDestroy
	public void destroy() {
		if (securityDataSource != null) {
			try {
				securityDataSource.close();
			} catch (SQLException e) {
				logger.error("Failure to dispose of the data source: ", e);
			}
		}
	}
	/**
	 * @return the securityDataSource
	 */
	@Bean
	public DataSource securityDataSource() {
		return securityDataSource;
	}
	
	/**
	 * @return group authorities by user name SQL
	 */
	@Bean public String groupAuthoritiesByUsernameSql() {
		return GROUP_AUTHORITIES_BY_USERNAME_SQL;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {}

	@Override
	/**
	 * Proper disposal of JDBC Drivers and MySQL in particular
	 */
	public void contextDestroyed(ServletContextEvent sce) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();     

        Driver driver = null;

        // clear drivers
        while(drivers.hasMoreElements()) {
            try {
                driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);

            } catch (SQLException ex) {
            	logger.error("Failed to de-register Driver", ex);
            }
        }

        // MySQL driver leaves around a thread. This static method cleans it up.
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
        	logger.error("Failed to stop AbandonedConnectionCleanupThread", e);
        }
	}
}
