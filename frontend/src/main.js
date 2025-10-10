import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap.min.css';

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
app.use(router);
app.use(i18n);
app.mount('#app');