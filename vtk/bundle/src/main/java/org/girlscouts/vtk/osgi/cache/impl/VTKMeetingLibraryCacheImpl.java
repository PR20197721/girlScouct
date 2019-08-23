package org.girlscouts.vtk.osgi.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.girlscouts.vtk.ocm.JcrNode;
import org.girlscouts.vtk.osgi.cache.VTKMeetingLibraryCache;
import org.girlscouts.vtk.osgi.conf.VTKMeetingLibraryCacheConfig;
import org.girlscouts.vtk.osgi.service.impl.BasicGirlScoutsService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component(service = {VTKMeetingLibraryCache.class}, immediate = true, name = "org.girlscouts.vtk.osgi.cache.impl.VTKMeetingLibraryCacheImpl")
@Designate(ocd = VTKMeetingLibraryCacheConfig.class)
public class VTKMeetingLibraryCacheImpl extends BasicGirlScoutsService implements VTKMeetingLibraryCache {

    private static Logger log = LoggerFactory.getLogger(VTKMeetingLibraryCacheImpl.class);

    private LoadingCache<String, Object> cache;

    private LoadingCache<String, Object> listCache;

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
            cache = CacheBuilder.newBuilder().maximumSize(this.maxSize).expireAfterWrite(this.expireAfter, TimeUnit.MINUTES).build(new CacheLoader<String, Object>() {
                public Object load(String key) {
                    return null;
                }
            });
            listCache = CacheBuilder.newBuilder().maximumSize(this.maxSize).expireAfterWrite(this.expireAfter, TimeUnit.MINUTES).build(new CacheLoader<String, Object>() {
                public List<Object> load(String key) {
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
    public <T extends JcrNode> T read(String key) {
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
    public <T extends JcrNode> void write(String key, T entity) {
        if(this.isCacheEnabled && key != null && entity != null) {
            try {
                log.debug("Writing "+key+" to cache.");
                this.cache.put(key, entity);
            } catch (Exception e) {
                log.error("Exception occurred adding object to cache for key=" + key, e);
            }
        }
    }
    @Override
    public boolean containsList(String key) {
        if (this.isCacheEnabled && key != null) {
            log.debug("Looking up "+key+" in cache.");
            try {
                return this.listCache.getIfPresent(key) != null;
            }catch(Exception e){
                log.error("Exception reading object list from cache for " + key, e);
            }
        }
        return false;
    }
    @Override
    public <T extends JcrNode> List<T> readList(String key) {
        if(this.isCacheEnabled && key != null) {
            try {
                log.debug("Reading "+key+" from list cache.");
                return (List<T>) this.listCache.getIfPresent(key);
            }catch (Exception e) {
                log.error("Exception occurred reading list from cache for key=" + key, e);
            }
        }
        return null;
    }

    @Override
    public <T extends JcrNode> void writeList(String key, List<T> list) {
        if(this.isCacheEnabled && key != null && list != null && list.size() > 0) {
            try {
                log.debug("Writing "+key+" to list cache.");
                this.listCache.put(key, list);
            }catch (Exception e) {
                log.error("Exception occurred adding list to cache for key=" + key, e);

            }
        }
    }
}
