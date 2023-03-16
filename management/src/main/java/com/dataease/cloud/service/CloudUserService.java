package com.dataease.cloud.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.dataease.cloud.base.domain.CloudUser;
import com.dataease.cloud.base.domain.CloudUserExample;
import com.dataease.cloud.base.domain.CloudUserRole;
import com.dataease.cloud.base.domain.CloudUserRoleExample;
import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.base.mapper.CloudUserRoleMapper;
import com.dataease.cloud.constant.GenderEnum;
import com.dataease.cloud.constant.LanguageEnum;
import com.dataease.cloud.constant.RegxContants;
import com.dataease.cloud.constant.RoleEnum;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.exception.SecurityException;
import com.dataease.cloud.security.profile.request.UserUpdateProfileRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息操作类
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 6:21 PM
 **/
@Service
@Transactional(rollbackFor = Exception.class)
@Lazy
@Slf4j
public class CloudUserService {

    @Resource
    private CloudUserMapper cloudUserMapper;

    @Resource
    private CloudUserRoleMapper cloudUserRoleMapper;

    /**
     * 根据手机号码获取用户
     * @param phone
     * @return
     */
    public LocalUser getUserByPhone(String phone){
        CloudUserExample cloudUserExample = new CloudUserExample();
        cloudUserExample.createCriteria().andPhoneEqualTo(phone);
        Optional<CloudUser> userOptional = cloudUserMapper.selectByExample(cloudUserExample)
                .stream()
                .findFirst();
        if(!userOptional.isPresent())
            SecurityException.throwException("user not found!");
        CloudUser cloudUser = userOptional.get();
        Set<SimpleGrantedAuthority> grantedAuthoritySet = getSimpleGrantedAuthorities(cloudUser);
        LocalUser localUser = new LocalUser();
        BeanUtil.copyProperties(cloudUser,localUser);
        localUser.setAuthorities(grantedAuthoritySet);
        return localUser;
    }

    /**
     * 根据名称获取用户
     * 可以是username email or phone
     * @param name
     * @return
     */
    public LocalUser getUserByName(String name){
        CloudUserExample cloudUserExample = new CloudUserExample();
        cloudUserExample.or().andUserNameEqualTo(name);
        cloudUserExample.or().andPhoneEqualTo(name);
        cloudUserExample.or().andEmailEqualTo(name);
        CloudUser cloudUser = cloudUserMapper.selectByExample(cloudUserExample).stream().findFirst().orElse(null);
        if(cloudUser == null)
            SecurityException.throwException("user not found!");
        Set<SimpleGrantedAuthority> grantedAuthoritySet = getSimpleGrantedAuthorities(cloudUser);
        LocalUser localUser = new LocalUser();
        BeanUtil.copyProperties(cloudUser,localUser);
        localUser.setAuthorities(grantedAuthoritySet);
        return localUser;
    }

    /**
     * 获取角色
     * @param cloudUser
     * @return
     */
    private Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(CloudUser cloudUser) {
        CloudUserRoleExample roleExample = new CloudUserRoleExample();
        roleExample.createCriteria().andUserIdEqualTo(cloudUser.getUserId());
        List<CloudUserRole> cloudUserRoles = cloudUserRoleMapper.selectByExample(roleExample);
        Set<SimpleGrantedAuthority> grantedAuthoritySet = cloudUserRoles.stream().map(cloudUserRole -> new SimpleGrantedAuthority("ROLE_" + cloudUserRole.getRole())).collect(Collectors.toSet());
        return grantedAuthoritySet;
    }

    /**
     * 根据ID获取信息
     * @param userId
     * @return
     */
    public LocalUser getUserById(String userId){
        CloudUser cloudUser = cloudUserMapper.selectByPrimaryKey(userId);
        if(cloudUser == null){
            SecurityException.throwException("User Not Found!");
        }
        Set<SimpleGrantedAuthority> grantedAuthoritySet = getSimpleGrantedAuthorities(cloudUser);
        LocalUser localUser = new LocalUser();
        BeanUtil.copyProperties(cloudUser,localUser);
        localUser.setAuthorities(grantedAuthoritySet);
        return localUser;
    }

    /**
     * 注册用户 默认给一个角色 就是用户角色
     * @param cloudUser
     * @return
     */
    public LocalUser registerUser(CloudUser cloudUser){
        if(StringUtils.hasText(cloudUser.getUserId()))
            SecurityException.throwException("User Already exist!");
        //判断参数
        validateUser(cloudUser);
        cloudUser.setUserId(IdUtil.randomUUID());
        cloudUser.setEnabled(true);
        cloudUser.setCreateTime(Calendar.getInstance().getTime());
        cloudUser.setUpdateTime(Calendar.getInstance().getTime());
        if(cloudUser.getGender() == null)
            cloudUser.setGender(GenderEnum.MAN.getKey());
        if(cloudUser.getLanguage() == null)
            cloudUser.setLanguage(LanguageEnum.ZH_CN.name());
        if(StringUtils.hasText(cloudUser.getPassword()))
            cloudUser.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(cloudUser.getPassword()));
         cloudUserMapper.insertSelective(cloudUser);
        //创建一个新的角色
        CloudUserRole cloudUserRole = new CloudUserRole();
        cloudUserRole.setRole(RoleEnum.USER.name());
        cloudUserRole.setUserId(cloudUser.getUserId());
        cloudUserRole.setCreateTime(Calendar.getInstance().getTime());
        cloudUserRole.setUpdateTime(Calendar.getInstance().getTime());
         cloudUserRoleMapper.insertSelective(cloudUserRole);
        Set<SimpleGrantedAuthority> grantedAuthoritySet = new HashSet<>(){
            {
                add(new SimpleGrantedAuthority("ROLE_" + cloudUserRole.getRole()));
            }
        };
        LocalUser localUser = new LocalUser();
        BeanUtil.copyProperties(cloudUser,localUser);
        localUser.setAuthorities(grantedAuthoritySet);
        return localUser;
    }

    /**
     * 判断用户基本信息
     * @param cloudUser
     */
    public void validateUser(CloudUser cloudUser){
        //非空判断
        if(!StringUtils.hasText(cloudUser.getUserName()))
            SecurityException.throwException("User userName is required!");
        if(!StringUtils.hasText(cloudUser.getNickName()))
            SecurityException.throwException("User nickName is required!");
        if(!StringUtils.hasText(cloudUser.getEmail()))
            SecurityException.throwException("User email is required!");
        if(!StringUtils.hasText(cloudUser.getPhone()))
            SecurityException.throwException("User phone is required!");
        //正则判断
        if(!ReUtil.isMatch(RegxContants.PHONE_NUM_REGX,cloudUser.getPhone()))
            SecurityException.throwException("The format of the phone is incorrect!");
        if(!ReUtil.isMatch(RegxContants.USER_NAME_REGX,cloudUser.getUserName()))
            SecurityException.throwException("The format of the username is incorrect!");
        if(!ReUtil.isMatch(RegxContants.EMAIL_REGX,cloudUser.getEmail()))
            SecurityException.throwException("The format of the email is incorrect!");
        if(StringUtils.hasText(cloudUser.getPassword()) && !ReUtil.isMatch(RegxContants.PASSWORD_REGX,cloudUser.getPassword()))
            SecurityException.throwException("The format of the password is incorrect!");
        //判断用户是否已经存在
        CloudUserExample cloudUserExample = new CloudUserExample();
        if(cloudUser.getUserId() != null){
            cloudUserExample.or().andUserNameEqualTo(cloudUser.getUserName()).andUserIdNotEqualTo(cloudUser.getUserId());
            cloudUserExample.or().andPhoneEqualTo(cloudUser.getPhone()).andUserIdNotEqualTo(cloudUser.getUserId());
            cloudUserExample.or().andEmailEqualTo(cloudUser.getEmail()).andUserIdNotEqualTo(cloudUser.getUserId());
        }else{
            cloudUserExample.or().andUserNameEqualTo(cloudUser.getUserName());
            cloudUserExample.or().andPhoneEqualTo(cloudUser.getPhone());
            cloudUserExample.or().andEmailEqualTo(cloudUser.getEmail());
        }
        if(cloudUserMapper.countByExample(cloudUserExample) > 0)
            SecurityException.throwException("User Already exist!");
    }

    /**
     * 注册补充用户信息
     * @param userUpdateProfileRequest
     * @return
     */
    public LocalUser registerUserByProfile(UserUpdateProfileRequest userUpdateProfileRequest){
        //直接返回注册用户的逻辑
        LocalUser localUser = registerUser(userUpdateProfileRequest);
        return localUser;
    }

    /**
     * 修改用户信息
     * 这个暂时没有管是否有第三方的情况
     * @param userUpdateProfileRequest
     */
    public LocalUser updateProfile(UserUpdateProfileRequest userUpdateProfileRequest){
        if(userUpdateProfileRequest.getUserId() == null)
            SecurityException.throwException("User Id required!");
        //判断用户是否存在
        CloudUser cloudUser = cloudUserMapper.selectByPrimaryKey(userUpdateProfileRequest.getUserId());
        if(cloudUser == null)
            SecurityException.throwException("User not Found!");
        if(!cloudUser.getEnabled())
            SecurityException.throwException("User not enable!");
        //直接把数据拷贝过来 忽略空值
        BeanUtil.copyProperties(userUpdateProfileRequest,cloudUser, CopyOptions.create().ignoreNullValue());
        //将传过来的密码加密
        if(StringUtils.hasText(userUpdateProfileRequest.getPassword())){
            cloudUser.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userUpdateProfileRequest.getPassword()));
        }
        validateUser(cloudUser);
        cloudUser.setUpdateTime(new Date());
        cloudUserMapper.updateByPrimaryKeySelective(cloudUser);

        cloudUser = cloudUserMapper.selectByPrimaryKey(cloudUser.getUserId());
        Set<SimpleGrantedAuthority> grantedAuthoritySet = getSimpleGrantedAuthorities(cloudUser);
        LocalUser localUser = new LocalUser();
        BeanUtil.copyProperties(cloudUser,localUser);
        localUser.setAuthorities(grantedAuthoritySet);

        return localUser;
    }

}
