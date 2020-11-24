package org.girlscouts.vtk.osgi.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.girlscouts.vtk.osgi.cache.VTKMeetingIconCache;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Component(service = {VTKMeetingIconCache.class}, immediate = true, name = "org.girlscouts.vtk.osgi.cache.impl.VTKMeetingIconCacheImpl")
@Designate(ocd = VTKMeetingIconCacheImpl.Config.class)
public class VTKMeetingIconCacheImpl implements VTKMeetingIconCache {

    private static Logger log = LoggerFactory.getLogger(VTKMeetingIconCacheImpl.class);

    private LoadingCache<String, Object> cache;


    boolean isCacheEnabled;
    int maxSize;
    int expireAfter;

    @Activate
    private void activate(Config config) {
        try {
            this.isCacheEnabled = config.isCacheEnabled();
        }catch(Exception e){
            log.error("Error occurred loading isCacheEnabled value from osgi config", e);
            this.isCacheEnabled = true;
        }
        try {
            this.maxSize = config.maxSize();
        }catch(Exception e){
            log.error("Error occurred loading maxSize value from osgi config", e);
            this.maxSize = 500;
        }
        try {
            this.expireAfter = config.expireAfter();
        }catch(Exception e){
            log.error("Error occurred loading expireAfter value from osgi config", e);
            this.expireAfter = 720;
        }
        if(this.isCacheEnabled) {
            cache = CacheBuilder.newBuilder().maximumSize(this.maxSize).expireAfterWrite(this.expireAfter, TimeUnit.MINUTES).build(new CacheLoader<String, Object>() {
                public Object load(String key) {
                    return null;
                }
            });
        }
        log.info("Girl Scouts VTK Meeting Library cache is Activated.");
    }

    @Override
    public boolean contains(String key) {
        if (this.isCacheEnabled && key != null) {
            log.debug("Looking up "+key+" in cache.");
            try {
                return this.cache.getIfPresent(key) != null;
            }catch(Exception e){
                log.error("Exception reading object from cache for " + key, e);
            }
        }
        return false;
    }

    @Override
    public <T extends String> T read(String key) {
        if(this.isCacheEnabled && key != null) {
            try{
                log.debug("Reading "+key+" from cache.");
                return (T) this.cache.getIfPresent(key);
            }catch(Exception e){
                log.error("Exception reading object from cache for "+key, e);
            }
        }
        return null;
    }

    @Override
    public <T extends String> void write(String key, T entity) {
        if(this.isCacheEnabled && key != null && entity != null) {
            try {
                log.debug("Writing "+key+" to cache.");
                this.cache.put(key, entity);
            } catch (Exception e) {
                log.error("Exception occurred adding object to cache for key=" + key, e);
            }
        }
    }
    @ObjectClassDefinition(name = "Girl Scouts VTK Meeting Icon cache configuration", description = "Girl Scouts VTK Meeting Icon cache configuration")
    public @interface Config {
        @AttributeDefinition(name = "Enable Caching", description = "Check to enable caching", type = AttributeType.BOOLEAN) boolean isCacheEnabled() default true;

        @AttributeDefinition(name = "Max Size", description = "Specifies the maximum number of entries the cache may contain.", type = AttributeType.INTEGER) int maxSize() default 3000;

        @AttributeDefinition(name = "Expiration Period (in min)", description = "Specifies that each entry should be automatically removed from the cache once a fixed duration has elapsed after the entry's creation, or the most recent replacement of its value.", type = AttributeType.INTEGER) int expireAfter() default 60;
    }
}
