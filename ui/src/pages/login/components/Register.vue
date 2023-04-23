<script setup lang="ts">
import { computed, onMounted, reactive, ref, onBeforeUnmount, Ref } from 'vue'
import { MessagePlugin, SubmitContext } from 'tdesign-vue-next'
import { sendSms, checkCode } from '@/api/sms/sms'
import { register } from '@/api/login/login'
import {
  checkPhone,
  checkEmail,
  checkPassword,
  checkUserName
} from '@/utils/common'
import router from '@/router'
import SlideVerify, { SlideVerifyInstance } from 'vue3-slide-verify'
import 'vue3-slide-verify/dist/style.css'
import userStore from '@/store/user'
import externalInfoStore from '@/store/externalUser'

const block = ref<SlideVerifyInstance>()
const imgs = ref(['assets/1.jpeg', 'assets/2.jpeg', 'assets/3.jpeg'])
const stroeExternalInfo = externalInfoStore()
const formData: UserRegisterReq = reactive({
  userName: '',
  nickName: '',
  password: '',
  email: '',
  phone: '',
  gender: '1',
  language: 'ZH_CN',
  code: '',
  confirmPassword: '',
  uuid: stroeExternalInfo.info.uuid,
  sourceId: stroeExternalInfo.info.sourceId
})

// 判断手机号码
const phoneValidator = (val: string) => {
  if (!checkPhone(val)) {
    return { result: false, message: '手机号码格式不对', type: 'error' }
  }
  return { result: true }
}

// 检查验证码 这样检查会检查多次 不太行 理论上只要校验一次即可
const validateCode = async (val: string) => {
  const res = await checkCode({
    phone: formData.phone,
    code: val
  })
  if (res.code != 200) {
    return { result: false, message: '验证码不正确', type: 'error' }
  } else {
    return { result: true }
  }
}

// 判断邮箱地址
const validateEmail = (val: string) => {
  if (!checkEmail(val)) {
    return { result: false, message: '邮箱地址格式不对', type: 'error' }
  }
  return { result: true }
}

// 校验用户名
const validateUserName = (val: string) => {
  if (!checkUserName(val)) {
    return {
      result: false,
      message: '用户名格式必须是4到16位(字母、数字、_、-)',
      type: 'error'
    }
  }
  return { result: true }
}

//校验密码
const validatePassword = (val: string) => {
  if (!checkPassword(val)) {
    return {
      result: false,
      message:
        '密码最少6位,1个小写字母包括至少1个大写字母、1个数字、1个特殊字符',
      type: 'error'
    }
  }
  return { result: true }
}

// 校验密码是否一致
const checkConfirmPassword = (val: string) => {
  if (formData.password === formData.confirmPassword) {
    return { result: true }
  }
  return {
    result: false,
    message: '密码最少6位,1个小写字母包括至少1个大写字母、1个数字、1个特殊字符',
    type: 'error'
  }
}

const rules = {
  userName: [
    {
      required: true,
      message: '用户名不能为空',
      type: 'error',
      trigger: 'blur'
    },
    {
      validator: validateUserName,
      message: '用户名格式不对'
    }
  ],
  password: [
    {
      required: true,
      message: '密码不能为空',
      type: 'error',
      trigger: 'blur'
    },
    {
      validator: validatePassword,
      message: '密码强度不够'
    }
  ],
  confirmPassword: [
    {
      required: true,
      message: '确认密码不能为空',
      type: 'error',
      trigger: 'blur'
    },
    {
      validator: checkConfirmPassword,
      message: '密码不一致'
    }
  ],
  phone: [
    {
      required: true,
      message: '手机号码不能为空',
      type: 'error',
      trigger: 'blur'
    },
    {
      validator: phoneValidator,
      message: '手机号码格式不对'
    }
  ],
  email: [
    {
      required: true,
      message: '邮箱不能为空',
      type: 'error',
      trigger: 'blur'
    },
    {
      validator: validateEmail,
      message: '邮箱格式不对'
    }
  ],
  code: [
    {
      required: true,
      message: '验证码不能为空',
      type: 'error',
      trigger: 'blur'
    }
  ],
  nickName: [
    {
      required: true,
      message: '昵称不能为空',
      type: 'error',
      trigger: 'blur'
    }
  ]
}

const onReset = () => {
  MessagePlugin.success('重置成功')
}

const storeUser = userStore()
onMounted(async () => {
  await storeUser.checkLogin()
  const user = storeUser.user
  if (user.enabled) {
    router.push('dashboard')
  }
})

onBeforeUnmount(() => {
  if (timer.value) {
    clearInterval(timer.value)
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
  sendSms({ phone: formData.phone })
    .then((res) => {
      if (res.code == 200) {
        formData.code = res.data.code
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

const showVerify = ref(false)

const showIntervel = ref(false)

const showSendBtn = computed(() => {
  if (formData.phone) {
    //判断是不是手机号码
    const isPhone = checkPhone(formData.phone)
    return !(isPhone && !showIntervel.value)
  }
  return true
})

const value = ref('phone')

const handlerChange = (newValue: string) => {
  value.value = newValue
}

//注册用户
const registerCommit = (context: SubmitContext<FormData>) => {
  context.e?.preventDefault()
  if (context.validateResult === true) {
    checkCode({
      phone: formData.phone,
      code: formData.code ? formData.code : ''
    })
      .then((resp) => {
        if (resp.code === 200) {
          register(formData)
            .then((res) => {
              if (res.code === 200) {
                storeUser.user = res.data
                router.push('dashboard')
              } else {
                MessagePlugin.error(res.msg)
              }
            })
            .catch((err) => {
              MessagePlugin.error(err)
            })
        } else {
          MessagePlugin.error(resp.msg)
        }
      })
      .catch((err) => {
        MessagePlugin.error(err)
      })
  } else {
    const error: string = context.firstError ? context.firstError : '未知错误'
    MessagePlugin.error(error)
  }
}

const onChange = (val: string) => {
  formData.gender = val
}
</script>
<template>
  <div class="item-containe">
    <t-form
      :data="formData"
      label-align="right"
      :rules="rules"
      :label-width="80"
      @submit="registerCommit"
      @reset="onReset"
    >
      <t-form-item label="手机号" name="phone">
        <t-input
          autocomplete="off"
          placeholder="请输入手机号码"
          v-model="formData.phone"
        ></t-input>
      </t-form-item>
      <t-form-item v-show="showVerify">
        <slide-verify
          ref="block"
          slider-text="向右滑动"
          w="330"
          :imgs="imgs"
          accuracy="5"
          @again="onAgain"
          @success="onSuccess"
          @fail="onFail"
        ></slide-verify>
      </t-form-item>
      <t-form-item label="验证码" name="code">
        <t-input
          class="phoneCode"
          v-model="formData.code"
          :disabled="showSendBtn"
          placeholder="请输入验证码"
        />
        <t-button
          style="width: 100px"
          variant="outline"
          :disabled="showSendBtn"
          @click="showVerifyCode"
        >
          {{ text }}
        </t-button>
      </t-form-item>

      <t-form-item label="用户名" name="userName">
        <t-input
          autocomplete="off"
          placeholder="请输入用户名,格式4到16位(字母,数字,下划线,减号)"
          v-model="formData.userName"
        ></t-input>
      </t-form-item>
      <t-form-item label="邮箱" name="email">
        <t-input
          autocomplete="off"
          placeholder="请输入邮箱"
          v-model="formData.email"
        ></t-input>
      </t-form-item>
      <t-form-item label="昵称" name="nickName">
        <t-input
          autocomplete="off"
          placeholder="请输入昵称"
          v-model="formData.nickName"
        ></t-input>
      </t-form-item>
      <t-form-item label="密码" name="password">
        <t-input
          autocomplete="off"
          placeholder="请输入密码(最少6位,1个小写字母包括至少1个大写字母、1个数字、1个特殊字符)"
          v-model="formData.password"
          type="password"
        ></t-input>
      </t-form-item>
      <t-form-item label="确认密码" name="confirmPassword">
        <t-input
          autocomplete="off"
          placeholder="请再次输入密码"
          :disabled="!formData.password"
          v-model="formData.confirmPassword"
          type="password"
        ></t-input>
      </t-form-item>
      <t-form-item label="性别">
        <t-radio-group default-value="1" @change="onChange">
          <t-radio value="1">男</t-radio>
          <t-radio value="2">女</t-radio>
        </t-radio-group>
      </t-form-item>
      <t-form-item>
        <t-button theme="primary" type="submit" block>注册</t-button>
      </t-form-item>
    </t-form>
  </div>
</template>

<style lang="less" scoped>
@import url('../index.less');
</style>
