package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudUserRole;
import com.dataease.cloud.base.domain.CloudUserRoleExample;
import com.dataease.cloud.base.domain.CloudUserRoleKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CloudUserRoleMapper {
    long countByExample(CloudUserRoleExample example);

    int deleteByExample(CloudUserRoleExample example);

    int deleteByPrimaryKey(CloudUserRoleKey key);

    int insert(CloudUserRole row);

    int insertSelective(CloudUserRole row);

    List<CloudUserRole> selectByExample(CloudUserRoleExample example);

    CloudUserRole selectByPrimaryKey(CloudUserRoleKey key);

    int updateByExampleSelective(@Param("row") CloudUserRole row, @Param("example") CloudUserRoleExample example);

    int updateByExample(@Param("row") CloudUserRole row, @Param("example") CloudUserRoleExample example);

    int updateByPrimaryKeySelective(CloudUserRole row);

    int updateByPrimaryKey(CloudUserRole row);
}
