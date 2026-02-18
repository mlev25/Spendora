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
            path: '/statistics',
            name: "statistics",
            component: () => import('../views/StatisticsView.vue'),
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
        {
            path: '/admin',
            name: "admin",
            component: () => import('../views/AdminView.vue'),
            meta: { requiresAuth: true, requiresAdmin: true }
        },
    ]
});

// Navigation guard
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    
    // Admin oldal védelem
    if (to.meta.requiresAdmin) {
        const user = authStore.getUser;
        const isAdmin = user?.roles?.includes('ROLE_ADMIN');
        
        if (!authStore.isLoggedIn) {
            next('/login');
        } else if (!isAdmin) {
            alert('Hozzáférés megtagadva! Adminisztrátori jogosultság szükséges.');
            next('/home');
        } else {
            next();
        }
    }
    // Bejelentkezett felhasználó védelem
    else if (to.meta.requiresAuth && !authStore.isLoggedIn) {
        next('/login');
    }
    // Ha már be van jelentkezve, ne mehessen login/register-re
    else if (authStore.isLoggedIn && (to.name === 'login' || to.name === 'register')) {
        next('/home');
    }
    else {
        next();
    }
});

export default router;