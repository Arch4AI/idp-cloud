<template>
  <div class="content">
    <t-config-provider :global-config="getLocale">
      <router-view></router-view>
    </t-config-provider>
  </div>
</template>

<script setup lang="ts">
import { useLocale } from '@/locales/useLocal'

const { getLocale } = useLocale()
</script>

<style lang="less" scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}

.content {
  width: 100%;
  height: 100%;
  margin: auto;
}

#nprogress .bar {
  background: var(--td-brand-color) !important;
}
</style>
