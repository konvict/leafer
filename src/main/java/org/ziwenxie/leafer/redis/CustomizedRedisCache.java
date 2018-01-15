package org.ziwenxie.leafer.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisOperations;
import org.ziwenxie.leafer.util.ApplicationContextHelper;
import org.ziwenxie.leafer.util.ThreadTaskHelper;

public class CustomizedRedisCache extends RedisCache {

    private static final Logger logger = LoggerFactory.getLogger(CustomizedRedisCache.class);

    private RedisOperations redisOperations;

    private long preloadSecondTime = 0;

    // TODO: not working?
    // @Autowired
    // @Qualifier("cacheSupportImpl")
    private CacheSupport cacheSupport;

    public CustomizedRedisCache(String name, byte[] prefix,
            RedisOperations<? extends Object, ? extends Object> redisOperations,
            long expiration,long preloadSecondTime) {
        super(name, prefix, redisOperations, expiration);
        this.redisOperations = redisOperations;
        this.preloadSecondTime = preloadSecondTime;
    }

    public CustomizedRedisCache(String name, byte[] prefix,
            RedisOperations<? extends Object, ? extends Object> redisOperations,
            long expiration,long preloadSecondTime, boolean allowNullValues) {
        super(name, prefix, redisOperations, expiration, allowNullValues);
        this.redisOperations = redisOperations;
        this.preloadSecondTime = preloadSecondTime;
    }

    @Override
    public ValueWrapper get(final Object key) {
        ValueWrapper valueWrapper = super.get(key);

        if (null != valueWrapper) {
            Long ttl = this.redisOperations.getExpire(key);

            if (null != ttl && ttl <= this.preloadSecondTime) {
                logger.info("key:{} ttl:{} preloadSecondTime:{}", key, ttl, preloadSecondTime);

                ThreadTaskHelper.run(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("refresh key:{}", key);
                        String cacheName = getName();
                        String keyName = key.toString();
                        cacheSupport = ApplicationContextHelper.getApplicationContext().getBean(CacheSupportImpl.class);
                        cacheSupport.refreshCacheByKey(cacheName, keyName);
                    }
                });

            }
        }

        return valueWrapper;
    }

}
