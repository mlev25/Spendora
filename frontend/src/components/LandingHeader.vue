<template>
  <header class="main-header">
    <div class="header-container">

      <div class="header-left">
        <router-link to="/" class="header-brand">Spendora</router-link>
        <button @click="toggleDarkMode" class="theme-toggle-btn">
          {{ darkModeIcon }}
        </button>
      </div>

      <nav class="header-nav">
        <div class="lang-switcher">
          <button @click="setLang('HU')" :class="{ 'lang-active': currentLang === 'HU' }">HU</button>
          <button @click="setLang('EN')" :class="{ 'lang-active': currentLang === 'EN' }">EN</button>
        </div>

        <router-link to="/login" class="nav-link">Bejelentkezés</router-link>
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
            return this.isDark ? '🌙' : '☀️'; 
        }
    },
    methods: {
        toggleDarkMode() {
            this.isDark = !this.isDark;
            const theme = this.isDark ? 'dark' : 'light';
            // Itt állítjuk be a globális CSS változók számára a sémát
            document.documentElement.setAttribute('data-theme', theme);
        },
        setLang(lang) {
            this.currentLang = lang;
            console.log("Nyelv váltva:", lang);
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
    gap: 15px;
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

.nav-link{
    color: var(--color-text);
    text-decoration: none;
    font-size: 1rem;
    transition: color 0.3s;
}
</style>