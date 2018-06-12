package se.inera.statistics.service.caching;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Activates infra cache configuration.
 */
@Configuration
@ImportResource({"classpath:basic-cache-config.xml"})
public class CacheConfiguration {
}
