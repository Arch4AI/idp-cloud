package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudSpringSessionAttributes;
import com.dataease.cloud.base.domain.CloudSpringSessionAttributesExample;
import com.dataease.cloud.base.domain.CloudSpringSessionAttributesKey;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudSpringSessionAttributesMapper {
    long countByExample(CloudSpringSessionAttributesExample example);

    int deleteByExample(CloudSpringSessionAttributesExample example);

    int deleteByPrimaryKey(CloudSpringSessionAttributesKey key);

    int insert(CloudSpringSessionAttributes row);

    int insertSelective(CloudSpringSessionAttributes row);

    List<CloudSpringSessionAttributes> selectByExampleWithBLOBs(CloudSpringSessionAttributesExample example);

    List<CloudSpringSessionAttributes> selectByExample(CloudSpringSessionAttributesExample example);

    CloudSpringSessionAttributes selectByPrimaryKey(CloudSpringSessionAttributesKey key);

    int updateByExampleSelective(@Param("row") CloudSpringSessionAttributes row, @Param("example") CloudSpringSessionAttributesExample example);

    int updateByExampleWithBLOBs(@Param("row") CloudSpringSessionAttributes row, @Param("example") CloudSpringSessionAttributesExample example);

    int updateByExample(@Param("row") CloudSpringSessionAttributes row, @Param("example") CloudSpringSessionAttributesExample example);

    int updateByPrimaryKeySelective(CloudSpringSessionAttributes row);

    int updateByPrimaryKeyWithBLOBs(CloudSpringSessionAttributes row);
}