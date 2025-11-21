import { createRouter, createWebHistory } from 'vue-router'
// 引入刚才拆分出来的组件
import StationMgt from '../views/StationMgt.vue'
import GatewayMgt from '../views/GatewayMgt.vue'
import SensorMgt from '../views/SensorMgt.vue'
import LogMgt from '../views/LogMgt.vue'
import DataMonitor from '../views/DataMonitor.vue'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue' // 引入

const routes = [
    {
        path: '/',
        redirect: '/login' // 默认跳转登录
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/station',
        name: 'Station',
        component: StationMgt
    },
    {
        path: '/gateway',
        name: 'Gateway',
        component: GatewayMgt
    },
    {
        path: '/sensor',
        name: 'Sensor',
        component: SensorMgt
    },
    {
        path: '/log',
        name: 'Log',
        component: LogMgt
    },
    {
        path: '/data',
        name: 'Data',
        component: DataMonitor
    },
    {
        path: '/home',   // 新增 home 路由
        name: 'Home',
        component: Home
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router