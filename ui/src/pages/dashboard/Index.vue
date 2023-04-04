<script setup lang="ts">
import { logout } from '@/api/logout/logout'
import router from '@/router'
import { DropdownOption, MessagePlugin } from 'tdesign-vue-next'
import userStore from '@/store/user'
import { Ref, ref } from 'vue'

const storeUser = userStore()

const options: Ref<Array<DropdownOption>> = ref([
  {
    content: '修改信息',
    value: 1,
    onClick: (dropdownItem: DropdownOption, context: { e: MouseEvent })=>{
      MessagePlugin.error('暂未实现')
    }
  },
  {
    content: '退出',
    value: 2,
    onClick: (dropdownItem: DropdownOption, context: { e: MouseEvent })=>{
      userLogout()
    }
  }
])

const userLogout = () => {
  logout().then((res) => {
    if (res.code === 200) {
      MessagePlugin.success('用户退出成功')
      router.push('/')
    } else {
      MessagePlugin.error(res.msg)
    }
  })
}
</script>
<template>
  <t-layout>
    <t-header>
      <t-head-menu value="item1" height="120px">
        <template #logo>
          <img
            width="136"
            class="logo"
            src="@/assets/images/login.svg"
            alt="logo"
          />
        </template>
        <template #operations>
          <t-dropdown :options="options" hideAfterItemClick>
            <t-button theme="defalut">{{ storeUser.user.nickName }}</t-button>
          </t-dropdown>
        </template>
      </t-head-menu>
    </t-header>
    <t-layout>
      <t-aside style="border-top: 1px solid var(--component-border)">
        <t-menu
          theme="dark"
          value="user"
          style="margin-right: 50px"
          height="550px"
        >
          <t-menu-item value="user">
            <template #icon>
              <t-icon name="user" />
            </template>
            用户管理
          </t-menu-item>
          <t-menu-item value="session">
            <template #icon>
              <t-icon name="usergroup" />
            </template>
            会话管理
          </t-menu-item>
          <t-menu-item value="log">
            <template #icon>
              <t-icon name="calendar" />
            </template>
            日志管理
          </t-menu-item>
        </t-menu>
      </t-aside>
      <t-layout>
        <t-content>
          <div>这里可以包含内容</div>
          <router-view></router-view>
        </t-content>
        <t-footer>底部未居中</t-footer>
      </t-layout>
    </t-layout>
  </t-layout>
</template>

<style scoped>
:deep.t-layout {
  height: 100%;
}
</style>
