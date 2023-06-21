<script setup lang="ts">
import DefaultIcon from '@/assets/icon/external_login.svg?component'

//组件传值
const props = defineProps({
  type: String, //类型
  icon: String, //图片的地址
  displayName: String, //显示名称
  click: Function //回调函数点击的
})

//这个是导出 父组件可以通过ref调用
defineExpose({})

const onClick = () => {
  if (props.click) {
    props.click()
  }
}
</script>

<template>
  <div class="icon_item" @click="onClick" v-if="icon" :title="displayName">
    <t-icon size="25px" :name="icon"></t-icon>
  </div>
  <div v-else>
    <div class="icon_item" @click="onClick" :title="displayName">
      <default-icon class="login_icon" />
    </div>
  </div>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}

.icon_item {
  cursor: pointer;
}

.login_icon {
  width: 25px;
  height: 25px;
}
</style>
