package com.dataease.cloud.security.jackson2;

import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.external.ExternalAuthenticationToken;
import com.dataease.cloud.security.jackson2.serializer.LocalUserMixin;
import com.dataease.cloud.security.jackson2.serializer.external.ExternalAuthenticationTokenMixin;
import com.dataease.cloud.security.jackson2.serializer.number.LongMixin;
import com.dataease.cloud.security.jackson2.serializer.password.PasswordAuthenticationTokenMixin;
import com.dataease.cloud.security.jackson2.serializer.phone.PhoneAuthenticationTokenMixin;
import com.dataease.cloud.security.jackson2.serializer.profile.ProfileAuthenticationTokenMixin;
import com.dataease.cloud.security.jackson2.serializer.register.RegisterAuthenticationTokenMixin;
import com.dataease.cloud.security.password.PasswordAuthenticationToken;
import com.dataease.cloud.security.phone.PhoneAuthenticationToken;
import com.dataease.cloud.security.profile.ProfileAuthenticationToken;
import com.dataease.cloud.security.register.RegisterAuthenticationToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * 自定义的jackson2 反序列化
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 4:53 PM
 **/
public class CustomJackson2Module extends SimpleModule {

    public CustomJackson2Module() {
        super(CustomJackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(LocalUser.class, LocalUserMixin.class);
        context.setMixInAnnotations(PasswordAuthenticationToken.class, PasswordAuthenticationTokenMixin.class);
        context.setMixInAnnotations(ProfileAuthenticationToken.class, ProfileAuthenticationTokenMixin.class);
        context.setMixInAnnotations(PhoneAuthenticationToken.class, PhoneAuthenticationTokenMixin.class);
        context.setMixInAnnotations(RegisterAuthenticationToken.class, RegisterAuthenticationTokenMixin.class);
        context.setMixInAnnotations(ExternalAuthenticationToken.class, ExternalAuthenticationTokenMixin.class);
        context.setMixInAnnotations(Long.class, LongMixin.class);
    }
}
