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
    }
};
</script>

<style scoped>
/* Flexbox alapú, reszponzív navigáció */
.main-header {
    background-color: var(--color-card-bg);
    border-bottom: 1px solid var(--color-border);
    padding: clamp(0.5rem, 1.5vw, 0.75rem) 0;
    transition: background-color 0.3s;
    border: 1px solid var(--color-border);
    border-radius: 12px;
    margin: 0.25rem;
}

.header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1400px;
    width: 95%;
    margin: 0 auto;
    gap: 1rem;
}

.header-left {
    display: flex;
    align-items: center;
    gap: clamp(0.5rem, 2vw, 1rem);
}

.header-brand {
    font-size: clamp(1.1rem, 3vw, 1.5rem);
    font-weight: bold;
    color: var(--color-text);
    text-decoration: none;
    white-space: nowrap;
    transition: color 0.3s;
}

.header-brand:hover {
    color: var(--color-primary);
}

.header-nav {
    display: flex;
    align-items: center;
    gap: clamp(0.5rem, 2vw, 1rem);
    flex-wrap: wrap;
    justify-content: flex-end;
}

.theme-toggle-btn {
    background: none;
    border: none;
    font-size: clamp(1.2rem, 2.5vw, 1.5rem);
    cursor: pointer;
    color: var(--color-text);
    transition: transform 0.3s;
    padding: 0.25rem;
}

.theme-toggle-btn:hover {
    transform: scale(1.1);
}

.lang-switcher {
    display: flex;
    gap: 0.25rem;
    padding-left: clamp(0.5rem, 1.5vw, 0.75rem);
    border-left: 1px solid var(--color-border);
}

.lang-switcher button {
    background: none;
    border: none;
    cursor: pointer;
    font-size: clamp(0.8rem, 1.5vw, 0.9rem);
    color: var(--color-text);
    padding: 0.375rem 0.625rem;
    transition: background-color 0.3s, color 0.3s, transform 0.2s;
    border-radius: 6px;
    min-width: clamp(35px, 5vw, 40px);
    font-weight: 600;
}

.lang-switcher button:hover {
    transform: translateY(-1px);
}

.lang-switcher .lang-active {
    background-color: var(--color-primary);
    color: white;
}

.nav-link {
    color: var(--color-text);
    text-decoration: none;
    padding: 0.5rem 0.875rem;
    transition: color 0.3s, transform 0.2s;
    font-weight: 500;
    font-size: clamp(0.85rem, 1.5vw, 1rem);
    white-space: nowrap;
}

.nav-link:hover {
    color: var(--color-primary);
    transform: translateY(-1px);
}

.user-greeting {
    color: var(--color-text);
    font-weight: 600;
    font-size: clamp(0.8rem, 1.5vw, 0.95rem);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 150px;
}

.logout-btn {
    background-color: var(--color-primary);
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 6px;
    cursor: pointer;
    font-weight: 600;
    font-size: clamp(0.85rem, 1.5vw, 0.95rem);
    transition: background-color 0.3s, transform 0.2s;
    white-space: nowrap;
}

.logout-btn:hover {
    background-color: #0056b3;
    transform: translateY(-2px);
}

/* Tablet és kisebb */
@media (max-width: 1023px) {
    .header-container {
        gap: 0.5rem;
    }

    .header-nav {
        gap: 0.5rem;
    }
    
    .user-greeting {
        max-width: 120px;
    }
}

/* Mobil */
@media (max-width: 640px) {
    .main-header {
        margin: 0.25rem 0.125rem;
    }

    .header-container {
        width: 98%;
        flex-wrap: wrap;
        justify-content: center;
        gap: 0.5rem;
    }
    
    .header-left {
        flex: 1 1 100%;
        justify-content: center;
    }
    
    .header-nav {
        flex: 1 1 100%;
        justify-content: center;
        gap: 0.375rem;
    }

    .lang-switcher {
        border-left: none;
        padding-left: 0;
    }
    
    .user-greeting {
        max-width: 100px;
        font-size: 0.75rem;
    }
    
    .logout-btn {
        padding: 0.375rem 0.75rem;
        font-size: 0.8rem;
    }
    
    .nav-link {
        padding: 0.375rem 0.625rem;
        font-size: 0.8rem;
    }
}

/* Nagyon kis képernyők */
@media (max-width: 360px) {
    .header-brand {
        font-size: 1rem;
    }
    
    .theme-toggle-btn {
        font-size: 1.1rem;
    }
}
</style>