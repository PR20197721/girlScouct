package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.rest.entity.vtk.LocationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationToLocationEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(LocationToLocationEntityMapper.class);

    public static LocationEntity map(Location location) {
        if (location != null) {
            try {
                LocationEntity entity = new LocationEntity();
                entity.setAddress(location.getAddress());
                entity.setCity(location.getCity());
                entity.setDbUpdate(location.isDbUpdate());
                entity.setLocatinName(location.getLocatinName());
                entity.setLocationAddress(location.getLocationAddress());
                entity.setName(location.getName());
                entity.setPath(location.getPath());
                entity.setState(location.getState());
                entity.setUid(location.getUid());
                entity.setZip(location.getZip());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Location to LocationEntity ", e);
            }
        }
        return null;
    }
}
