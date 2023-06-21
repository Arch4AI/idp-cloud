package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudUserAttribute;
import com.dataease.cloud.base.domain.CloudUserAttributeExample;
import com.dataease.cloud.base.domain.CloudUserAttributeKey;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudUserAttributeMapper {
    long countByExample(CloudUserAttributeExample example);

    int deleteByExample(CloudUserAttributeExample example);

    int deleteByPrimaryKey(CloudUserAttributeKey key);

    int insert(CloudUserAttribute row);

    int insertSelective(CloudUserAttribute row);

    List<CloudUserAttribute> selectByExample(CloudUserAttributeExample example);

    CloudUserAttribute selectByPrimaryKey(CloudUserAttributeKey key);

    int updateByExampleSelective(@Param("row") CloudUserAttribute row, @Param("example") CloudUserAttributeExample example);

    int updateByExample(@Param("row") CloudUserAttribute row, @Param("example") CloudUserAttributeExample example);

    int updateByPrimaryKeySelective(CloudUserAttribute row);

    int updateByPrimaryKey(CloudUserAttribute row);
}