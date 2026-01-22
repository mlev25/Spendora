import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth.js";

//need to create some views first, then import them here
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),

    routes: [
        {
            path: '/',
            name: "landing",
            component: () => import('../views/LandingView.vue')
        },
        {
            path: '/login',
            name: "login",
            component: () => import('../views/LoginView.vue')
        },
        {
            path: '/register',
            name: "register",
            component: () => import('../views/RegisterView.vue')
        },
    ]
});

// Navigation guard - redirect authenticated users away from login/register
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    
    // Ha a felhasználó be van jelentkezve és login/register oldalra megy, átirányítjuk
    if (authStore.isLoggedIn && (to.name === 'login' || to.name === 'register')) {
        next('/');
    } else {
        next();
    }
});

export default router;