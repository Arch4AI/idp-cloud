package com.dataease.cloud.base.mapper;

import com.dataease.cloud.base.domain.CloudOauth2AuthorizationConsent;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationConsentExample;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationConsentKey;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CloudOauth2AuthorizationConsentMapper {
    long countByExample(CloudOauth2AuthorizationConsentExample example);

    int deleteByExample(CloudOauth2AuthorizationConsentExample example);

    int deleteByPrimaryKey(CloudOauth2AuthorizationConsentKey key);

    int insert(CloudOauth2AuthorizationConsent row);

    int insertSelective(CloudOauth2AuthorizationConsent row);

    List<CloudOauth2AuthorizationConsent> selectByExample(CloudOauth2AuthorizationConsentExample example);

    CloudOauth2AuthorizationConsent selectByPrimaryKey(CloudOauth2AuthorizationConsentKey key);

    int updateByExampleSelective(@Param("row") CloudOauth2AuthorizationConsent row, @Param("example") CloudOauth2AuthorizationConsentExample example);

    int updateByExample(@Param("row") CloudOauth2AuthorizationConsent row, @Param("example") CloudOauth2AuthorizationConsentExample example);

    int updateByPrimaryKeySelective(CloudOauth2AuthorizationConsent row);

    int updateByPrimaryKey(CloudOauth2AuthorizationConsent row);
}