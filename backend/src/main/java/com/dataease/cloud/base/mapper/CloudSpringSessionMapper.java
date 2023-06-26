package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudSpringSession;
import com.dataease.cloud.base.domain.CloudSpringSessionExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CloudSpringSessionMapper {
    long countByExample(CloudSpringSessionExample example);

    int deleteByExample(CloudSpringSessionExample example);

    int deleteByPrimaryKey(String primaryId);

    int insert(CloudSpringSession row);

    int insertSelective(CloudSpringSession row);

    List<CloudSpringSession> selectByExample(CloudSpringSessionExample example);

    CloudSpringSession selectByPrimaryKey(String primaryId);

    int updateByExampleSelective(@Param("row") CloudSpringSession row, @Param("example") CloudSpringSessionExample example);

    int updateByExample(@Param("row") CloudSpringSession row, @Param("example") CloudSpringSessionExample example);

    int updateByPrimaryKeySelective(CloudSpringSession row);

    int updateByPrimaryKey(CloudSpringSession row);
}
