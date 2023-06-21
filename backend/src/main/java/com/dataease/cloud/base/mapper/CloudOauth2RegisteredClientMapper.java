package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudOauth2RegisteredClient;
import com.dataease.cloud.base.domain.CloudOauth2RegisteredClientExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudOauth2RegisteredClientMapper {
    long countByExample(CloudOauth2RegisteredClientExample example);

    int deleteByExample(CloudOauth2RegisteredClientExample example);

    int deleteByPrimaryKey(String id);

    int insert(CloudOauth2RegisteredClient row);

    int insertSelective(CloudOauth2RegisteredClient row);

    List<CloudOauth2RegisteredClient> selectByExample(CloudOauth2RegisteredClientExample example);

    CloudOauth2RegisteredClient selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("row") CloudOauth2RegisteredClient row, @Param("example") CloudOauth2RegisteredClientExample example);

    int updateByExample(@Param("row") CloudOauth2RegisteredClient row, @Param("example") CloudOauth2RegisteredClientExample example);

    int updateByPrimaryKeySelective(CloudOauth2RegisteredClient row);

    int updateByPrimaryKey(CloudOauth2RegisteredClient row);
}