import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router/index'
import TDesign from 'tdesign-vue-next'
import 'tdesign-vue-next/es/style/index.css'
import store from '@/store'


const app = createApp(App)


app.use(store)
app.use(router)
app.use(TDesign)
app.mount('#app')
