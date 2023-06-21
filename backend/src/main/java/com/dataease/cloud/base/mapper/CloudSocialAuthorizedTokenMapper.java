package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudSocialAuthorizedToken;
import com.dataease.cloud.base.domain.CloudSocialAuthorizedTokenExample;
import com.dataease.cloud.base.domain.CloudSocialAuthorizedTokenKey;
import com.dataease.cloud.base.domain.CloudSocialAuthorizedTokenWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudSocialAuthorizedTokenMapper {
    long countByExample(CloudSocialAuthorizedTokenExample example);

    int deleteByExample(CloudSocialAuthorizedTokenExample example);

    int deleteByPrimaryKey(CloudSocialAuthorizedTokenKey key);

    int insert(CloudSocialAuthorizedTokenWithBLOBs row);

    int insertSelective(CloudSocialAuthorizedTokenWithBLOBs row);

    List<CloudSocialAuthorizedTokenWithBLOBs> selectByExampleWithBLOBs(CloudSocialAuthorizedTokenExample example);

    List<CloudSocialAuthorizedToken> selectByExample(CloudSocialAuthorizedTokenExample example);

    CloudSocialAuthorizedTokenWithBLOBs selectByPrimaryKey(CloudSocialAuthorizedTokenKey key);

    int updateByExampleSelective(@Param("row") CloudSocialAuthorizedTokenWithBLOBs row, @Param("example") CloudSocialAuthorizedTokenExample example);

    int updateByExampleWithBLOBs(@Param("row") CloudSocialAuthorizedTokenWithBLOBs row, @Param("example") CloudSocialAuthorizedTokenExample example);

    int updateByExample(@Param("row") CloudSocialAuthorizedToken row, @Param("example") CloudSocialAuthorizedTokenExample example);

    int updateByPrimaryKeySelective(CloudSocialAuthorizedTokenWithBLOBs row);

    int updateByPrimaryKeyWithBLOBs(CloudSocialAuthorizedTokenWithBLOBs row);

    int updateByPrimaryKey(CloudSocialAuthorizedToken row);
}