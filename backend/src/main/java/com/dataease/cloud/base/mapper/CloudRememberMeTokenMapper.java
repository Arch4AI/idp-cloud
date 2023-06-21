package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudRememberMeToken;
import com.dataease.cloud.base.domain.CloudRememberMeTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudRememberMeTokenMapper {
    long countByExample(CloudRememberMeTokenExample example);

    int deleteByExample(CloudRememberMeTokenExample example);

    int deleteByPrimaryKey(String series);

    int insert(CloudRememberMeToken row);

    int insertSelective(CloudRememberMeToken row);

    List<CloudRememberMeToken> selectByExample(CloudRememberMeTokenExample example);

    CloudRememberMeToken selectByPrimaryKey(String series);

    int updateByExampleSelective(@Param("row") CloudRememberMeToken row, @Param("example") CloudRememberMeTokenExample example);

    int updateByExample(@Param("row") CloudRememberMeToken row, @Param("example") CloudRememberMeTokenExample example);

    int updateByPrimaryKeySelective(CloudRememberMeToken row);

    int updateByPrimaryKey(CloudRememberMeToken row);
}