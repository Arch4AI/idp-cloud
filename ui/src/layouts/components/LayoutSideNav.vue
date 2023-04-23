<template>
  <l-side-nav
    :theme="theme"
    :layout="layout"
    :is-fixed="true"
    :is-compact="false"
    :menu="sideMenu"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'

import { usePermissionStore } from '@/store'

import LSideNav from './SideNav.vue'

const layout = ref('mix')
const theme = ref('light')
const permissionStore = usePermissionStore()
const { routers: menuRouters } = storeToRefs(permissionStore)

const sideMenu = computed(() => {
  let newMenuRouters = menuRouters.value
  return newMenuRouters
})
</script>
