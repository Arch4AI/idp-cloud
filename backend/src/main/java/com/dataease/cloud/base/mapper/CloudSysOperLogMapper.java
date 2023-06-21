package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudSysOperLog;
import com.dataease.cloud.base.domain.CloudSysOperLogExample;
import com.dataease.cloud.base.domain.CloudSysOperLogWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudSysOperLogMapper {
    long countByExample(CloudSysOperLogExample example);

    int deleteByExample(CloudSysOperLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CloudSysOperLogWithBLOBs row);

    int insertSelective(CloudSysOperLogWithBLOBs row);

    List<CloudSysOperLogWithBLOBs> selectByExampleWithBLOBs(CloudSysOperLogExample example);

    List<CloudSysOperLog> selectByExample(CloudSysOperLogExample example);

    CloudSysOperLogWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CloudSysOperLogWithBLOBs row, @Param("example") CloudSysOperLogExample example);

    int updateByExampleWithBLOBs(@Param("row") CloudSysOperLogWithBLOBs row, @Param("example") CloudSysOperLogExample example);

    int updateByExample(@Param("row") CloudSysOperLog row, @Param("example") CloudSysOperLogExample example);

    int updateByPrimaryKeySelective(CloudSysOperLogWithBLOBs row);

    int updateByPrimaryKeyWithBLOBs(CloudSysOperLogWithBLOBs row);

    int updateByPrimaryKey(CloudSysOperLog row);
}