<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { MessagePlugin, SubmitContext } from 'tdesign-vue-next'
import { loginByPwd, loginByPhone } from '@/api/login/login'
import { getMe } from '@/api/user/user'
import { sendSms } from '@/api/sms/sms'
import { checkPhone } from '@/utils/common'
import router from '@/router'
import SlideVerify, { SlideVerifyInstance } from 'vue3-slide-verify'
import 'vue3-slide-verify/dist/style.css'
import userStore from '@/store/user'

const block = ref<SlideVerifyInstance>()
const imgs = ref(['assets/1.jpeg', 'assets/2.jpeg', 'assets/3.jpeg'])

const formData: PasswordLoginReq = reactive({
  userName: '',
  password: ''
})

const phoneFormData = reactive({
  phone: '',
  code: ''
})

const phoneValidator = (val: string) => {
  if (!checkPhone(val)) {
    return { result: false, message: '手机号码格式不对', type: 'error' }
  }
  return { result: true }
}

const passwordRules = {
  userName: [
    {
      required: true,
      message: '手机号码/用户名/邮箱不能为空',
      type: 'error',
      trigger: 'blur'
    }
  ],
  password: [
    {
      required: true,
      message: '用户密码不能为空',
      type: 'error',
      trigger: 'blur'
    }
  ]
}

const phoneRules = {
  phone: [
    { required: true, message: '手机号码必填', type: 'error', trigger: 'blur' },
    { validator: phoneValidator, message: '手机号码格式不对' }
  ],
  code: [
    { required: true, message: '验证码必填', type: 'error', trigger: 'blur' }
  ]
}

const onReset = () => {
  MessagePlugin.success('重置成功')
}

// 账号密码登录
const loginPassword = (context: SubmitContext<FormData>) => {
  context.e?.preventDefault()
  if (context.validateResult) {
    loginByPwd(formData)
      .then((res) => {
        if (res.code === 200) {
          storeUser.user = res.data
          router.push('home')
          MessagePlugin.success(res.data.nickName + '登录成功')
        } else {
          MessagePlugin.error(res.msg)
        }
      })
      .catch((err) => {
        MessagePlugin.error('请求失败:' + err.message)
      })
  } else {
    const error: string = context.firstError ? context.firstError : '未知错误'
    MessagePlugin.error(error)
  }
}

// 短信方式登录
const loginPhone = (context: SubmitContext<FormData>) => {
  context.e?.preventDefault()
  if (context.validateResult === true) {
    loginByPhone(phoneFormData).then((res) => {
      if (res.code === 200) {
        router.push('home')
        storeUser.user = res.data
        MessagePlugin.success(res.data.nickName + '登录成功')
      } else {
        MessagePlugin.error(res.msg)
      }
    })
  } else {
    const error: string = context.firstError ? context.firstError : '未知错误'
    MessagePlugin.error(error)
  }
}

const storeUser = userStore()
onMounted(async () => {
  // 
  await storeUser.checkLogin()
  const user = storeUser.user
  if(user.enabled){
    router.push('/home')
  }
})

const onAgain = () => {
  // 再来一次
  block.value?.refresh()
}

const timer = ref()

const onSuccess = (times: number) => {
  showVerify.value = false
  //这里去发送短信
  sendSms({ phone: phoneFormData.phone })
    .then((res) => {
      if (res.code == 200) {
        phoneFormData.code = res.data.code
        //开启倒计时
        showIntervel.value = true
        //去发送一个短信验证码 并且后面的那个发送的时间需要改成一个倒计时
        let i = 60
        timer.value = setInterval(() => {
          if (i === 60) {
            text.value = '01:00'
          } else if (i < 60 && i > 9) {
            text.value = '00:' + i
          } else {
            text.value = '00:0' + i
          }
          if (i == 0) {
            showIntervel.value = false
            clearInterval(timer.value)
            text.value = '发送验证码'
          }
          i--
        }, 1000)
      } else {
        MessagePlugin.error('短信发送失败:' + res.msg)
      }
    })
    .catch((err) => {
      MessagePlugin.error('短信发送失败:' + err)
    })
}

const text = ref('发送验证码')

const onFail = () => {
  // 验证失败
  onAgain()
}

const showVerifyCode = () => {
  showVerify.value = true
  // 重新触发一下刷新
  onAgain()
}

const loginType = ref('phone')

const showVerify = ref(false)

const showIntervel = ref(false)

const showSendBtn = computed(() => {
  if (phoneFormData.phone) {
    //判断是不是手机号码
    const isPhone = checkPhone(phoneFormData.phone)
    return !(isPhone && !showIntervel.value)
  }
  return true
})
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
      :rules="phoneRules"
      :colon="true"
      :label-width="0"
      @submit="loginPhone"
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

      <t-form-item v-show="showVerify">
        <slide-verify
          ref="block"
          slider-text="向右滑动"
          w="400"
          :imgs="imgs"
          accuracy="5"
          @again="onAgain"
          @success="onSuccess"
          @fail="onFail"
        ></slide-verify>
      </t-form-item>

      <t-form-item name="code" v-show="!showVerify">
        <t-input
          class="phoneCode"
          v-model="phoneFormData.code"
          placeholder="请输入验证码"
        >
          <template #prefix-icon>
            <t-icon name="mail" />
          </template>
          <template #suffix>
            <t-button
              class="send-btn"
              :disabled="showSendBtn"
              @click="showVerifyCode"
              >{{ text }}</t-button
            >
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
      :rules="passwordRules"
      :label-width="0"
      @reset="onReset"
      @submit="loginPassword"
    >
      <t-form-item name="userName">
        <t-input
          v-model="formData.userName"
          clearable
          placeholder="请输入账户名/手机号码/邮箱地址"
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
  /* height: 400px; */
  padding: 40px;
  padding-bottom: 100px;
  position: absolute;
  top: 50%;
  left: 50%;
  background: #fafafa;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 3px rgba(1, 6, 13, 0.15) !important;
}

.login-header {
  margin-bottom: 10px;
  width: 100%;
}

:deep.phoneCode .t-input {
  padding-right: 0px;
}

.send-btn {
  width: 108px;
}
</style>
