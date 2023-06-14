package com.epam.esm.config.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * This class is used for development data source configuration. Derived from {@link DataSourceConfig}.
 *
 * @author Danylo Proshyn
 */

@Configuration
@Profile("test")
@PropertySource("classpath:datasource-test.properties")
public class TestDataSourceConfig extends DataSourceConfig {

}
