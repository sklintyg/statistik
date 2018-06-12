package se.inera.statistics.service.caching;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Used when caching is not enabled.
 */
@Profile("!caching-enabled")
@Configuration
public class FakeCacheConfiguration {

    @Bean
    public RedisTemplate<Object, Object> fakeRedisTemplate() {
        return new FakeRedisTemplate();
    }
}
