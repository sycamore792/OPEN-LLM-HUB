import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Main',
    component: () => import('@/views/Main.vue'),
    redirect: '/home',
    children:[
        {
      path: '/home',
      name: 'home',
      component: () => import('@/views/Home.vue')
    },  {
      path: '/model',
      name: 'model',
      component: () => import('@/views/Model.vue')
    }, {
      path: '/modelCompany',
      name: 'modelCompany',
      component: () => import('@/views/ModelCompany.vue')
    },{
      path: '/apiKey',
      name: 'apiKey',
      component: () => import('@/views/ApiKey.vue')
    },
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
