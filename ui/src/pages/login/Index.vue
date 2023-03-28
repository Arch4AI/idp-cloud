<script setup lang="ts">
import apiRequest from '@/request/request'
import { Ref, onMounted, reactive, ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { loginByPwd } from '@/api/login/login'
import { getMe } from '@/api/user/user'
import router from '@/router'

const formData: PasswordLoginReq = reactive({
  userName: '',
  password: ''
})

const phoneFormData = reactive({
  phone: '',
  code: ''
})

const onReset = () => {
  MessagePlugin.success('重置成功')
}

const onSubmit = () => {
  loginByPwd(formData)
    .then((res) => {
      if (res.code === 200) {
        MessagePlugin.success('用户' + res.data.nickName + '登录成功')
      } else {
        MessagePlugin.error(res.msg)
      }
    })
    .catch((err) => {
      MessagePlugin.error('请求失败:' + err.message)
    })
}

onMounted(() => {
  getMe().then(res=>{
    const role = res.data.userId
    router.push('home')
  }).catch(err=>{
    console.log('用户未登录')
  })
})

const loginType = ref('phone')
</script>
<template>
  <div class="login-form">
    <t-divider>登录入口</t-divider>
    <t-space class="login-header">
      <t-radio-group v-model="loginType" variant="default-filled">
        <t-radio-button value="phone">手机登录</t-radio-button>
        <t-radio-button value="password">密码登录</t-radio-button>
      </t-radio-group>
    </t-space>
    <t-form
      v-show="loginType === 'phone'"
      ref="phoneForm"
      :data="phoneFormData"
      :colon="true"
      :label-width="0"
      @reset="onReset"
    >
      <t-form-item name="phone">
        <t-input
          v-model="phoneFormData.phone"
          clearable
          placeholder="请输入手机号"
        >
          <template #prefix-icon>
            <t-icon name="mobile" />
          </template>
        </t-input>
      </t-form-item>

      <t-form-item name="code">
        <t-input
          class="phoneCode"
          v-model="phoneFormData.code"
          clearable
          placeholder="请输入验证码"
        >
          <template #prefix-icon>
            <t-icon name="mail" />
          </template>
          <template #suffix>
            <t-button>发送验证码</t-button>
          </template>
        </t-input>
      </t-form-item>

      <t-form-item>
        <t-button theme="primary" type="submit" block>登录</t-button>
      </t-form-item>
    </t-form>
    <t-form
      v-show="loginType === 'password'"
      ref="form"
      :data="formData"
      :colon="true"
      :label-width="0"
      @reset="onReset"
      @submit="onSubmit"
    >
      <t-form-item name="userName">
        <t-input
          v-model="formData.userName"
          clearable
          placeholder="请输入账户名"
        >
          <template #prefix-icon>
            <t-icon name="user" />
          </template>
        </t-input>
      </t-form-item>

      <t-form-item name="password">
        <t-input
          v-model="formData.password"
          type="password"
          clearable
          placeholder="请输入密码"
        >
          <template #prefix-icon>
            <t-icon name="lock-on" />
          </template>
        </t-input>
      </t-form-item>

      <t-form-item>
        <t-button theme="primary" type="submit" block>登录</t-button>
      </t-form-item>
    </t-form>
  </div>
</template>

<style scoped>
.login-form {
  min-width: 400px;
  padding: 10px;
  position: absolute;
  top: 50%;
  left: 50%;
  background: #fafafa;
  transform: translate(-50%, -50%);
}

.login-header {
  margin-bottom: 10px;
  width: 100%;
}

:deep.phoneCode .t-input {
  padding-right: 0px;
}
</style>
