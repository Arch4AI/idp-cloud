package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudUserSocial;
import com.dataease.cloud.base.domain.CloudUserSocialExample;
import com.dataease.cloud.base.domain.CloudUserSocialKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CloudUserSocialMapper {
    long countByExample(CloudUserSocialExample example);

    int deleteByExample(CloudUserSocialExample example);

    int deleteByPrimaryKey(CloudUserSocialKey key);

    int insert(CloudUserSocial row);

    int insertSelective(CloudUserSocial row);

    List<CloudUserSocial> selectByExampleWithBLOBs(CloudUserSocialExample example);

    List<CloudUserSocial> selectByExample(CloudUserSocialExample example);

    CloudUserSocial selectByPrimaryKey(CloudUserSocialKey key);

    int updateByExampleSelective(@Param("row") CloudUserSocial row, @Param("example") CloudUserSocialExample example);

    int updateByExampleWithBLOBs(@Param("row") CloudUserSocial row, @Param("example") CloudUserSocialExample example);

    int updateByExample(@Param("row") CloudUserSocial row, @Param("example") CloudUserSocialExample example);

    int updateByPrimaryKeySelective(CloudUserSocial row);

    int updateByPrimaryKeyWithBLOBs(CloudUserSocial row);

    int updateByPrimaryKey(CloudUserSocial row);
}
