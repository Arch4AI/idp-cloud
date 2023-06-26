package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudOauth2Authorization;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationExample;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CloudOauth2AuthorizationMapper {
    long countByExample(CloudOauth2AuthorizationExample example);

    int deleteByExample(CloudOauth2AuthorizationExample example);

    int deleteByPrimaryKey(String id);

    int insert(CloudOauth2AuthorizationWithBLOBs row);

    int insertSelective(CloudOauth2AuthorizationWithBLOBs row);

    List<CloudOauth2AuthorizationWithBLOBs> selectByExampleWithBLOBs(CloudOauth2AuthorizationExample example);

    List<CloudOauth2Authorization> selectByExample(CloudOauth2AuthorizationExample example);

    CloudOauth2AuthorizationWithBLOBs selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("row") CloudOauth2AuthorizationWithBLOBs row, @Param("example") CloudOauth2AuthorizationExample example);

    int updateByExampleWithBLOBs(@Param("row") CloudOauth2AuthorizationWithBLOBs row, @Param("example") CloudOauth2AuthorizationExample example);

    int updateByExample(@Param("row") CloudOauth2Authorization row, @Param("example") CloudOauth2AuthorizationExample example);

    int updateByPrimaryKeySelective(CloudOauth2AuthorizationWithBLOBs row);

    int updateByPrimaryKeyWithBLOBs(CloudOauth2AuthorizationWithBLOBs row);

    int updateByPrimaryKey(CloudOauth2Authorization row);
}
