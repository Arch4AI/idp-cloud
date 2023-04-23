<template>
  <div :class="sideNavCls">
    <t-menu
      :class="menuCls"
      :theme="theme"
      :value="active"
      :default-expanded="defaultExpanded"
    >
      <menu-content :nav-data="menu" />
    </t-menu>
  </div>
</template>

<script setup lang="ts">
import union from 'lodash/union'
import type { PropType } from 'vue'
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { prefix } from '@/config/global'
import { getActive, getRoutesExpanded } from '@/router'
import type { MenuRoute } from '@/api/common/types'

import MenuContent from './MenuContent.vue'

const MIN_POINT = 992 - 1

const props = defineProps({
  menu: {
    type: Array as PropType<MenuRoute[]>,
    default: () => []
  },
  isFixed: {
    type: Boolean as PropType<boolean>,
    default: true
  },
  layout: {
    type: String as PropType<string>,
    default: ''
  },
  headerHeight: {
    type: String as PropType<string>,
    default: '64px'
  },
  theme: {
    type: String as PropType<'light' | 'dark'>,
    default: 'light'
  },
  isCompact: {
    type: Boolean as PropType<boolean>,
    default: false
  }
})

const active = computed(() => getActive())

const defaultExpanded = computed(() => {
  const path = getActive()
  const parentPath = path.substring(0, path.lastIndexOf('/'))
  const expanded = getRoutesExpanded()
  return union(expanded, parentPath === '' ? [] : [parentPath])
})

const sideNavCls = computed(() => {
  const { isCompact } = props
  return [
    `${prefix}-sidebar-layout`,
    {
      [`${prefix}-sidebar-compact`]: isCompact
    }
  ]
})

const menuCls = computed(() => {
  const { isFixed } = props
  return [
    `${prefix}-side-nav`,
    {
      [`${prefix}-side-nav-no-logo`]: isFixed,
      [`${prefix}-side-nav-no-fixed`]: isFixed,
      [`${prefix}-side-nav-mix-fixed`]: !isFixed
    }
  ]
})

const router = useRouter()

const goHome = () => {
  router.push('/dashboard/base')
}
</script>

<style lang="less" scoped></style>
