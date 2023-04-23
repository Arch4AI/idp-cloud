<template>
    <div class="login">
        <div class="login-form">
            <t-divider>
                <h1 class="title">{{ type == 'register' ? '注册' : 'Any Login' }}</h1>
            </t-divider>
            <login v-if="type === 'login'" />
            <register v-else @register-success="switchType('login')" />
            <div class="sub-title">
                <p class="tip" @click="switchType(type == 'register' ? 'login' : 'register')">
                    {{ type == 'register' ? '已有账号?登录' : '没有账号吗?点击注册' }}
                </p>
            </div>
        </div>
        <!--  底部  -->
        <div class="login-footer">
            <span>Copyright @ 2021-2023 凌霞. All Rights Reserved.</span>
        </div>
    </div>
</template>
<script lang="ts">
export default {
    name: 'LoginIndex',
};
</script>
<script setup lang="ts">
import { ref } from 'vue';

import Login from './components/Login.vue';
import Register from './components/Register.vue';

const type = ref('login');
const switchType = (val: string) => {
    type.value = val;
};
</script>

<style lang="less" scoped>
@import url('./index.less');
</style>

<style lang="less" scoped>

</style>
