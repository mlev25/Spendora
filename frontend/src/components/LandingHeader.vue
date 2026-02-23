<template>
  <header class="main-header">
    <div class="header-container">

      <div class="header-left">
        <router-link to="/" class="header-brand">{{ $t('header.appName') }}</router-link>
        <button @click="toggleDarkMode" class="theme-toggle-btn">
          {{ darkModeIcon }}
        </button>
      </div>

      <nav class="header-nav">
        <!-- Ha nincs bejelentkezve -->
        <template v-if="!isLoggedIn">
          <router-link to="/login" class="nav-link">{{ $t('header.login') }}</router-link>
          <router-link to="/register" class="nav-link">{{ $t('header.register') }}</router-link>
        </template>

        <!-- Ha be van jelentkezve -->
        <template v-else>
          <router-link to="/home" class="nav-link">{{ $t('header.home') }}</router-link>
          <router-link to="/profile" class="nav-link">{{ $t('header.profile') }}</router-link>
          <router-link v-if="isAdmin" to="/admin" class="nav-link admin-link">Admin</router-link>

          <button @click="handleLogout" class="logout-btn">{{ $t('header.logout') }}</button>
        </template>

        <div class="lang-switcher">
          <button @click="setLang('hu')" :class="{ 'lang-active': currentLang === 'HU' }">HU</button>
          <button @click="setLang('en')" :class="{ 'lang-active': currentLang === 'EN' }">EN</button>
        </div>
      </nav>
    </div>
  </header>
</template>

<script>
import { useAuthStore } from '../stores/auth.js';

export default {
    name: 'LandingHeader',
    data() {
        return {
            isDark: false,
            currentLang: 'HU'
        };
    },
    computed: {
        darkModeIcon() {
            return this.isDark ? '🌙' : '🔆';
        },
        isLoggedIn() {
            const authStore = useAuthStore();
            return authStore.isLoggedIn;
        },
        userName() {
            const authStore = useAuthStore();
            return authStore.user?.name || authStore.user?.username || 'User';
        },
        isAdmin() {
            const authStore = useAuthStore();
            return authStore.user?.roles?.includes('ROLE_ADMIN') || false;
        }
    },
    methods: {
        toggleDarkMode() {
            this.isDark = !this.isDark;
            const theme = this.isDark ? 'dark' : 'light';
            document.documentElement.setAttribute('data-theme', theme);
        },
        setLang(localeCode) {
            this.$i18n.locale = localeCode;
            localStorage.setItem('locale', localeCode);
            this.currentLang = localeCode.toUpperCase();
        },
        handleLogout() {
            const authStore = useAuthStore();
            authStore.logout();
            this.$router.push('/');
        }
    },
    mounted() {
        const initialTheme = this.isDark ? 'dark' : 'light';
        document.documentElement.setAttribute('data-theme', initialTheme);
        
        // Betöltjük a tárolt nyelvet
        const savedLocale = localStorage.getItem('locale') || 'hu';
        this.currentLang = savedLocale.toUpperCase();
        this.$i18n.locale = savedLocale;
    }
};
</script>

<style scoped>
@import './styles/LandingHeader.css';
</style>
