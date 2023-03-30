import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src') //添加@识别
    },
  },
  plugins: [vue()],
  server: {
    port: 4000,
    host: '0.0.0.0',
    open: false,
    proxy: {
      '/dev-api': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/dev-api/, '')
      }
    }
  }
})
