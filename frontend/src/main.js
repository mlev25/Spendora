import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap.min.css';
import { createPinia } from 'pinia';

import { createI18n } from 'vue-i18n';
import hu from './locales/hu.json';
import en from './locales/en.json';

const i18n = createI18n({
  locale: localStorage.getItem('locale') || 'hu',
  fallbackLocale: 'en',
  legacy: false,
  globalInjection: true,
  messages: {
    hu,
    en
  }
});

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);

// Auth store inicializálása (token visszaállítása localStorage-ból)
// FONTOS: Ez az app.mount() ELŐTT történik, hogy az App.vue setup-ban már elérhető legyen
import { useAuthStore } from './stores/auth.js';
const authStore = useAuthStore();
authStore.initializeAuth();

app.use(router);
app.use(i18n);
app.mount('#app');