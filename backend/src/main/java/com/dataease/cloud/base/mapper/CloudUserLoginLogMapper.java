package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudUserLoginLog;
import com.dataease.cloud.base.domain.CloudUserLoginLogExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CloudUserLoginLogMapper {
    long countByExample(CloudUserLoginLogExample example);

    int deleteByExample(CloudUserLoginLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CloudUserLoginLog row);

    int insertSelective(CloudUserLoginLog row);

    List<CloudUserLoginLog> selectByExample(CloudUserLoginLogExample example);

    CloudUserLoginLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CloudUserLoginLog row, @Param("example") CloudUserLoginLogExample example);

    int updateByExample(@Param("row") CloudUserLoginLog row, @Param("example") CloudUserLoginLogExample example);

    int updateByPrimaryKeySelective(CloudUserLoginLog row);

    int updateByPrimaryKey(CloudUserLoginLog row);
}
