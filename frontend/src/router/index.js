import { createRouter, createWebHistory } from "vue-router";

//need to create some views first, then import them here
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),

    routes: [
        {
            path: '/',
            name: "landing",
            component: () => import('../views/LandingView.vue')
        },
        
    ]
});

export default router;