package org.girlscouts.vtk.osgi.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.girlscouts.vtk.osgi.cache.MulesoftContactsResponseCache;
import org.girlscouts.vtk.osgi.conf.SalesForceContactsResponseCacheConfig;
import org.girlscouts.vtk.osgi.service.impl.BasicGirlScoutsService;
import org.girlscouts.vtk.rest.entity.salesforce.ContactsInfoResponseEntity;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Component(service = {MulesoftContactsResponseCache.class}, immediate = true, name = "org.girlscouts.vtk.osgi.cache.impl.MulesoftContactsResponseCacheImpl")
@Designate(ocd = SalesForceContactsResponseCacheConfig.class)
public class MulesoftContactsResponseCacheImpl extends BasicGirlScoutsService implements MulesoftContactsResponseCache{

    private static Logger log = LoggerFactory.getLogger(MulesoftContactsResponseCacheImpl.class);

    private LoadingCache<String, ContactsInfoResponseEntity> cache;

    boolean isCacheEnabled;
    int maxSize;
    int expireAfter;

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        try {
            this.isCacheEnabled = Boolean.parseBoolean(getConfig("isCacheEnabled"));
        }catch(Exception e){
            log.error("Error occurred loading isCacheEnabled value from osgi config", e);
            this.isCacheEnabled = true;
        }
        try {
            this.maxSize = Integer.parseInt(getConfig("maxSize"));
        }catch(Exception e){
            log.error("Error occurred loading maxSize value from osgi config", e);
            this.maxSize = 500;
        }
        try {
            this.expireAfter = Integer.parseInt(getConfig("expireAfter"));
        }catch(Exception e){
            log.error("Error occurred loading expireAfter value from osgi config", e);
            this.expireAfter = 720;
        }
        if(this.isCacheEnabled) {
            cache = CacheBuilder.newBuilder().maximumSize(this.maxSize).expireAfterWrite(this.expireAfter, TimeUnit.MINUTES).build(new CacheLoader<String, ContactsInfoResponseEntity>() {
                public ContactsInfoResponseEntity load(String key) {
                    return null;
                }
            });
        }
        log.info("Girl Scouts VTK Contacts cache Activated.");
    }

    @Override
    public boolean contains(String key) {
        if (this.isCacheEnabled && key != null) {
            //log.debug("Looking up "+key+" in cache.");
            try {
                return this.cache.getIfPresent(key) != null;
            }catch(Exception e){
                log.error("Exception reading from cache contacts for " + key + " troop", e);
            }
        }
        return false;
    }

    @Override
    public ContactsInfoResponseEntity read(String key) {
        if(this.isCacheEnabled && key != null) {
            try{
                log.debug("Reading "+key+" from cache.");
                return this.cache.getIfPresent(key);
            }catch(Exception e){
                log.error("Exception reading from cache contacts for "+key+" troop", e);
            }
        }
        return null;
    }

    @Override
    public void write(String key, ContactsInfoResponseEntity entity) {
        if(this.isCacheEnabled && key != null && entity != null && entity.getContacts() != null && entity.getContacts().length > 0) {
            try {
                log.debug("Writing "+key+" to cache.");
                this.cache.put(key, entity);
            } catch (Exception e) {
                log.error("Exception occurred adding object to cache for key=" + key, e);

            }
        }
    }
}
