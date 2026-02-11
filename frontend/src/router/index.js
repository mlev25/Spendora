import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth.js";

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
        {
            path: '/home',
            name: "home",
            component: () => import('../views/HomeView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/profile',
            name: "profile",
            component: () => import('../views/ProfileView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/contact',
            name: "contact",
            component: () => import('../views/ContactView.vue')
        },
        {
            path: '/about',
            name: "about",
            component: () => import('../views/AboutView.vue')
        },
        {
            path: '/faq',
            name: "faq",
            component: () => import('../views/FAQView.vue')
        },
    ]
});

// Navigation guard
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    
    // Ha védett oldalra megy és nincs bejelentkezve
    if (to.meta.requiresAuth && !authStore.isLoggedIn) {
        next('/login');
    }
    // Ha be van jelentkezve és login/register oldalra megy
    else if (authStore.isLoggedIn && (to.name === 'login' || to.name === 'register')) {
        next('/home');
    }
    else {
        next();
    }
});

export default router;