import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import ECharts from 'vue-echarts';
import './style/animate.min.css';

const app = createApp(App)

app.component('v-chart', ECharts);


app.use(ElementPlus).use(router)
app.mount('#app')
