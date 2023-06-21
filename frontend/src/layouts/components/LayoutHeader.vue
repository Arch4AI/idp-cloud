<template>
  <l-header
    :theme="theme"
    :layout="layout"
    :is-fixed="true"
    :is-compact="false"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
const layout = ref('mix')
const theme = ref('light')

import LHeader from './Header.vue'
</script>
