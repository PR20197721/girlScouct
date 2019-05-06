package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.rest.entity.vtk.AssetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetToAssetEntityMapper extends BaseModelToEntityMapper {
    private static Logger log = LoggerFactory.getLogger(AssetToAssetEntityMapper.class);

    public static AssetEntity map(Asset asset) {
        if (asset != null) {
            try {
                AssetEntity entity = new AssetEntity();
                entity.setCachable(asset.getIsCachable());
                entity.setDbUpdate(asset.isDbUpdate());
                entity.setDescription(asset.getDescription());
                entity.setDocType(asset.getDocType());
                entity.setOutdoorRelated(asset.getIsOutdoorRelated());
                entity.setPath(asset.getPath());
                entity.setRefId(asset.getRefId());
                entity.setTitle(asset.getTitle());
                entity.setType(asset.getType());
                entity.setUid(asset.getUid());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Asset to AssetEntity: ", e);
            }
        }
        return null;
    }
}
