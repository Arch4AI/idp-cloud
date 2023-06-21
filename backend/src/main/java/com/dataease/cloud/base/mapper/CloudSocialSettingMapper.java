package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudSocialSetting;
import com.dataease.cloud.base.domain.CloudSocialSettingExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudSocialSettingMapper {
    long countByExample(CloudSocialSettingExample example);

    int deleteByExample(CloudSocialSettingExample example);

    int deleteByPrimaryKey(String id);

    int insert(CloudSocialSetting row);

    int insertSelective(CloudSocialSetting row);

    List<CloudSocialSetting> selectByExampleWithBLOBs(CloudSocialSettingExample example);

    List<CloudSocialSetting> selectByExample(CloudSocialSettingExample example);

    CloudSocialSetting selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("row") CloudSocialSetting row, @Param("example") CloudSocialSettingExample example);

    int updateByExampleWithBLOBs(@Param("row") CloudSocialSetting row, @Param("example") CloudSocialSettingExample example);

    int updateByExample(@Param("row") CloudSocialSetting row, @Param("example") CloudSocialSettingExample example);

    int updateByPrimaryKeySelective(CloudSocialSetting row);

    int updateByPrimaryKeyWithBLOBs(CloudSocialSetting row);

    int updateByPrimaryKey(CloudSocialSetting row);
}