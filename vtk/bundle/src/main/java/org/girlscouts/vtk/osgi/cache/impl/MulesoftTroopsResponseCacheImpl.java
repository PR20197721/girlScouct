package org.girlscouts.vtk.osgi.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.girlscouts.vtk.osgi.cache.MulesoftTroopsResponseCache;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopInfoResponseEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Component(service = {MulesoftTroopsResponseCache.class}, immediate = true, name = "org.girlscouts.vtk.osgi.cache.impl.MulesoftTroopsResponseCacheImpl")
@Designate(ocd = MulesoftTroopsResponseCacheImpl.Config.class)
public class MulesoftTroopsResponseCacheImpl implements MulesoftTroopsResponseCache {

    private static Logger log = LoggerFactory.getLogger(MulesoftTroopsResponseCacheImpl.class);

    private LoadingCache<String, TroopInfoResponseEntity> cache;

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
            cache = CacheBuilder.newBuilder().maximumSize(this.maxSize).expireAfterWrite(this.expireAfter, TimeUnit.MINUTES).build(new CacheLoader<String, TroopInfoResponseEntity>() {
                public TroopInfoResponseEntity load(String key) {
                    return null;
                }
            });
        }
        log.info("Girl Scouts VTK Troop cache Activated.");
    }

    @Override
    public boolean contains(String key) {
        if (this.isCacheEnabled && key != null) {
            //log.debug("Looking up "+key+" in cache.");
            try {
                return this.cache.getIfPresent(key) != null;
            }catch(Exception e){
                log.error("Exception reading from cache troops for " + key + " troop", e);
            }
        }
        return false;
    }

    @Override
    public TroopInfoResponseEntity read(String key) {
        if(this.isCacheEnabled && key != null) {
            try{
                log.debug("Reading "+key+" from cache.");
                return this.cache.getIfPresent(key);
            }catch(Exception e){
                log.error("Exception reading from cache troops for "+key+" troop", e);
            }
        }
        return null;
    }

    @Override
    public void write(String key, TroopInfoResponseEntity entity) {
        if(this.isCacheEnabled && key != null && entity != null && entity.getTroops() != null && entity.getTroops().size()>0) {
            try {
                log.debug("Writing "+key+" to cache.");
                this.cache.put(key, entity);
            } catch (Exception e) {
                log.error("Exception occurred adding object to cache for key=" + key, e);

            }
        }
    }

    @ObjectClassDefinition(name = "Girl Scouts VTK Contacts cache configuration", description = "Girl Scouts VTK Contacts cache configuration")
    public @interface Config {
        @AttributeDefinition(name = "Enable Caching", description = "Check to enable caching", type = AttributeType.BOOLEAN) boolean isCacheEnabled() default true;

        @AttributeDefinition(name = "Max Size", description = "Specifies the maximum number of entries the cache may contain.", type = AttributeType.INTEGER) int maxSize() default 2000;

        @AttributeDefinition(name = "Expiration Period (in min)", description = "Specifies that each entry should be automatically removed from the cache once a fixed duration has elapsed after the entry's creation, or the most recent replacement of its value.", type = AttributeType.INTEGER) int expireAfter() default 60;
    }
}
