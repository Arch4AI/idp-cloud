package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudRedisSession;
import com.dataease.cloud.base.domain.CloudRedisSessionExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudRedisSessionMapper {
    long countByExample(CloudRedisSessionExample example);

    int deleteByExample(CloudRedisSessionExample example);

    int deleteByPrimaryKey(String sessionId);

    int insert(CloudRedisSession row);

    int insertSelective(CloudRedisSession row);

    List<CloudRedisSession> selectByExample(CloudRedisSessionExample example);

    CloudRedisSession selectByPrimaryKey(String sessionId);

    int updateByExampleSelective(@Param("row") CloudRedisSession row, @Param("example") CloudRedisSessionExample example);

    int updateByExample(@Param("row") CloudRedisSession row, @Param("example") CloudRedisSessionExample example);

    int updateByPrimaryKeySelective(CloudRedisSession row);

    int updateByPrimaryKey(CloudRedisSession row);
}