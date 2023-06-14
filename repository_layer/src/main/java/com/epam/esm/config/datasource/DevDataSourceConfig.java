package com.epam.esm.config.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * This class is used for production data source configuration. Derived from {@link DataSourceConfig}.
 *
 * @author Danylo Proshyn
 */

@Configuration
@Profile("dev")
@PropertySource("classpath:datasource-dev.properties")
public class DevDataSourceConfig extends DataSourceConfig {

}
