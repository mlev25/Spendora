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
        <div class="lang-switcher">
          <button @click="setLang('hu')" :class="{ 'lang-active': currentLang === 'HU' }">HU</button>
          <button @click="setLang('en')" :class="{ 'lang-active': currentLang === 'EN' }">EN</button>
        </div>

        <router-link to="/login" class="nav-link">{{ $t('header.login') }}</router-link>
      </nav>
    </div>
  </header>
</template>

<script>
export default {
    name: 'LandingHeader', // Fontos a név!
    data() {
        return {
            isDark: false,
            currentLang: 'HU'
        };
    },
    computed: {
        darkModeIcon() {
            return this.isDark ? '🌙' : '💡'; 
        }
    },
    methods: {
        toggleDarkMode() {
            this.isDark = !this.isDark;
            const theme = this.isDark ? 'dark' : 'light';
            // Itt állítjuk be a globális CSS változók számára a sémát
            document.documentElement.setAttribute('data-theme', theme);
        },
        setLang(localeCode) {
            this.$i18n.locale = localeCode;
            localStorage.setItem('locale', localeCode);
            this.currentLang = localeCode.toUpperCase();
            console.log("Nyelv váltva:", this.currentLang);
        }
    },
    mounted() {
        // Alapértelmezett téma beállítása a betöltéskor
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
    padding: 10px 0;
    transition: background-color 0.3s;
    border: 1px solid var(--color-border);
    border-radius: 12px;
    margin-right: 5px;
    margin-left: 5px;
    margin-top: 5px;
}

.header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1200px;
    width: 90%;
    margin: 0 auto;
}

.header-left {
    display: flex;
    align-items: center;
}

.header-brand {
    font-size: 1.5rem;
    font-weight: bold;
    color: var(--color-text);
    text-decoration: none;
    margin-right: 20px;
}
.header-brand:hover {
    color: var(--color-primary);
}

.header-nav {
    display: flex;
    align-items: center;
}
.theme-toggle-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: var(--color-text);
    transition: color 0.3s;
}

.lang-switcher button {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 0.9rem;
    color: var(--color-text);
    margin: 0 5px;
    padding: 5px 10px;
    transition: background-color 0.3s, color 0.3s;
}

.lang-switcher .lang-active {
    background-color: var(--color-primary);
    color: white;
    border-radius: 5px;
}

.nav-link:hover {
    color: var(--color-primary);
}



.nav-link {
    /* Rögzített minimális szélesség, ami elfér a "Bejelentkezés" szövegnek */
    min-width: 110px; 
    text-align: center; /* Középre igazítja a szöveget */

    color: var(--color-text);
    text-decoration: none;
    padding: 8px 0; /* A magasság megőrzése a gomb mellett */
    transition: color 0.3s;
}

@media (max-width: 768px) {

    /* ... a többi globális mobilos beállítás ... */

    /* Nyelvváltó gombok (HU/EN) */
    .lang-switcher button {
        /* 1. JAVÍTÁS: Még kisebb betűméret */
        font-size: 0.8rem; 
        
        /* 2. JAVÍTÁS: Még kisebb párnázás (padding) */
        padding: 2px 5px; 
        
        /* Opcionális: Szűkebb keret (szegély) */
        border-width: 1px; 
    }
    
    /* Nyelvváltó konténer (szorosabbra fogás) */
    .lang-switcher {
        gap: 3px; /* Még kisebb rés a HU és EN között */
        flex-shrink: 0;
    }

    /* Regisztráció/Login gombok is kisebbek lehetnek */
    .nav-link, .nav-btn {
        padding: 4px 8px; /* Linkek is kisebbek lesznek */
        font-size: 0.9rem;
        min-width: unset;
        flex-grow: 1; 
        flex-shrink: 1;
    }
}
</style>