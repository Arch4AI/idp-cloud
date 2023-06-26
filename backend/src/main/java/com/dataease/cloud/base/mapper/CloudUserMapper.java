package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudUser;
import com.dataease.cloud.base.domain.CloudUserExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CloudUserMapper {
    long countByExample(CloudUserExample example);

    int deleteByExample(CloudUserExample example);

    int deleteByPrimaryKey(String userId);

    int insert(CloudUser row);

    int insertSelective(CloudUser row);

    List<CloudUser> selectByExample(CloudUserExample example);

    CloudUser selectByPrimaryKey(String userId);

    int updateByExampleSelective(@Param("row") CloudUser row, @Param("example") CloudUserExample example);

    int updateByExample(@Param("row") CloudUser row, @Param("example") CloudUserExample example);

    int updateByPrimaryKeySelective(CloudUser row);

    int updateByPrimaryKey(CloudUser row);
}
