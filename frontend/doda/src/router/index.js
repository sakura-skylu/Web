import Vue from 'vue'
import VueRouter from 'vue-router'

import Start from '@/views/Start.vue'
import Expression from '@/views/Expression.vue'
import Mutation from '@/views/Mutation.vue'
import Immune from '@/views/Immune.vue'
import Drug from '@/views/Drug.vue'
import Help from '@/views/Help.vue'
Vue.use(VueRouter)

const router = new VueRouter({
    // 指定hash属性与组件的对应关系
    routes: [
        { path: '/', component:Start },
        { path: '/expression', component:Expression },
        { path: '/mutation', component:Mutation },
        { path: '/immune', component:Immune },
        { path: '/drug', component:Drug },
        { path: '/help', component:Help },
    ]
})

export default router