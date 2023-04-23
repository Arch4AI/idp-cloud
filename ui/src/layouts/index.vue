<template>
  <div>
    <t-layout key="no-side">
      <t-header><layout-header /> </t-header>
      <t-layout class="t-layout--with-sider">
        <layout-side-nav />
        <layout-content />
      </t-layout>
    </t-layout>
  </div>
</template>

<script setup lang="ts">
import '@/style/layout.less'

import LayoutContent from './components/LayoutContent.vue'
import LayoutHeader from './components/LayoutHeader.vue'
import LayoutSideNav from './components/LayoutSideNav.vue'
</script>

<style lang="less" scoped></style>
