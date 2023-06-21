import 'tdesign-vue-next/es/style/index.css'
// import './style.css'
import '@/style/tailwind/tailwind.css'
import '@/style/index.less'
import './permission'
import TDesign from 'tdesign-vue-next'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index'
import store from '@/store'

const app = createApp(App)

app.use(TDesign)
app.use(store)
app.use(router)
app.mount('#app')
