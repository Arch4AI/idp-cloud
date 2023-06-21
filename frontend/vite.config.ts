import { ConfigEnv, defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import svgLoader from 'vite-svg-loader'

import path from 'path'

export default defineConfig((mode: ConfigEnv) => {
  const env = loadEnv(mode.mode, process.cwd())
  return {
    root: process.cwd(),
    base: env.VITE_APP_BASE_URL,
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src') //添加@识别
      }
    },
    css: {
      preprocessorOptions: {
        less: {
          modifyVars: {
            hack: `true; @import (reference) "${path.resolve(
              'src/style/variables.less'
            )}";`
          },
          math: 'strict',
          javascriptEnabled: true
        }
      }
    },
    plugins: [vue(), svgLoader()],
    server: {
      port: 4000,
      cors: false,
      host: '0.0.0.0',
      open: false,
      proxy: {
        '/dev-api': {
          target: 'http://127.0.0.1:8090',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/dev-api/, '')
        },
        '/oauth2': {
          target: 'http://127.0.0.1:8090/oauth2',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/oauth2/, '')
        }
      }
    }
  }
})
