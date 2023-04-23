<template>
  <div :class="layoutCls">
    <t-head-menu
      :class="menuCls"
      :theme="menuTheme"
      expand-type="popup"
      :value="active"
    >
      <template #logo>
        <span
          class="header-logo-container"
          @click="handleNav('/dashboard/base')"
        >
          <logo-full class="t-logo" />
        </span>
      </template>
      <template #operations>
        <div class="operations-container">
          <!-- 全局通知 Notice-->

          <t-dropdown :min-column-width="120" trigger="click">
            <template #dropdown>
              <t-dropdown-menu>
                <t-dropdown-item
                  class="operations-dropdown-container-item"
                  @click="handleNav('/user/index')"
                >
                  <t-icon name="user-circle"></t-icon>个人中心
                </t-dropdown-item>
                <t-dropdown-item
                  class="operations-dropdown-container-item"
                  @click="handleLogout"
                >
                  <t-icon name="poweroff"></t-icon>退出登录
                </t-dropdown-item>
              </t-dropdown-menu>
            </template>
            <t-button class="header-user-btn" theme="default" variant="text">
              <template #icon>
                <t-icon class="header-user-avatar" name="user-circle" />
              </template>
              <div class="header-user-account">{{ storeUser.user.nickName }}</div>
              <template #suffix><t-icon name="chevron-down" /></template>
            </t-button>
          </t-dropdown>
        </div>
      </template>
    </t-head-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

import LogoFull from '@/assets/images/login.svg?component'
import { prefix } from '@/config/global'
import { getActive } from '@/router'
import { logout } from '@/api/logout/logout'
import { MessagePlugin } from 'tdesign-vue-next'
import userStore from "@/store/user";

const props = defineProps({
  theme: {
    type: String,
    default: 'light'
  },
  layout: {
    type: String,
    default: 'mix'
  },
  isFixed: {
    type: Boolean,
    default: false
  },
  isCompact: {
    type: Boolean,
    default: false
  },
  maxLevel: {
    type: Number,
    default: 3
  }
})

const storeUser = userStore()

const router = useRouter()

const active = computed(() => getActive())

const layoutCls = computed(() => [`${prefix}-header-layout`])

const menuCls = computed(() => {
  const { isFixed } = props
  return [
    {
      [`${prefix}-header-menu`]: !isFixed,
      [`${prefix}-header-menu-fixed`]: isFixed,
      [`${prefix}-header-menu-fixed-side`]: !isFixed,
      [`${prefix}-header-menu-fixed-side-compact`]: !isFixed
    }
  ]
})
const menuTheme = computed(() => props.theme as 'light' | 'dark')

const handleNav = (url) => {
  router.push(url)
}

const handleLogout = () => {
  logout().then((res) => {
    if (res.code === 200) {
      MessagePlugin.success('用户退出成功')
      router.push('/home')
    } else {
      MessagePlugin.error(res.msg)
    }
  })
}
</script>
<style lang="less" scoped>
.@{starter-prefix}-header {
  &-menu-fixed {
    position: fixed;
    top: 0;
    z-index: 1001;
    background-color: var(--td-bg-color-page);

    :deep(.t-head-menu__inner) {
      padding-right: var(--td-comp-margin-xl);
    }

    &-side {
      left: 232px;
      right: 0;
      z-index: 10;
      width: auto;
      transition: all 0.3s;
      &-compact {
        left: 64px;
      }
    }
  }

  &-logo-container {
    cursor: pointer;
    display: inline-flex;
  }
}
.header-menu {
  flex: 1 1 1;
  display: inline-flex;

  :deep(.t-menu__item) {
    min-width: unset;
  }
}

.operations-container {
  display: flex;
  align-items: center;

  .t-popup__reference {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .t-button {
    margin-left: var(--td-comp-margin-l);
  }
}

.header-operate-left {
  display: flex;
  align-items: normal;
  line-height: 0;
  padding-left: var(--td-comp-margin-xl);
}

.header-logo-container {
  width: 184px;
  height: 160px;
  display: flex;
  margin-left: 24px;
  color: var(--td-text-color-primary);

  .t-logo {
    width: 100%;
    height: 100%;
    &:hover {
      cursor: pointer;
    }
  }

  &:hover {
    cursor: pointer;
  }
}

.header-user-account {
  display: inline-flex;
  align-items: center;
  color: var(--td-text-color-primary);
}

.t-menu--light {
  .header-user-account {
    color: var(--td-text-color-primary);
  }
}
.t-menu--dark {
  .t-head-menu__inner {
    border-bottom: 1px solid var(--td-gray-color-10);
  }
  .header-user-account {
    color: rgba(255, 255, 255, 0.55);
  }
}

.operations-dropdown-container-item {
  width: 100%;
  display: flex;
  align-items: center;

  :deep(.t-dropdown__item-text) {
    display: flex;
    align-items: center;
  }

  .t-icon {
    font-size: var(--td-comp-size-xxxs);
    margin-right: var(--td-comp-margin-s);
  }

  :deep(.t-dropdown__item) {
    width: 100%;
    margin-bottom: 0px;
  }
  &:last-child {
    :deep(.t-dropdown__item) {
      margin-bottom: 8px;
    }
  }
}
</style>

<!-- eslint-disable-next-line vue-scoped-css/enforce-style-type -->
<style lang="less">
.operations-dropdown-container-item {
  .t-dropdown__item-text {
    display: flex;
    align-items: center;
  }
}
</style>
