<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">{{ $t('login.title') }}</h2>
      
      <!-- Hiba üzenet -->
      <div v-if="errorMessage" class="alert alert-danger" role="alert">
        {{ errorMessage }}
      </div>

      <!-- Sikeres bejelentkezés üzenet -->
      <div v-if="successMessage" class="alert alert-success" role="alert">
        {{ successMessage }}
      </div>

      <form @submit.prevent="handleLogin">
        <!-- Felhasználónév -->
        <div class="form-group">
          <label for="username">{{ $t('login.username') }}</label>
          <input
            type="text"
            id="username"
            v-model="formData.username"
            class="form-control"
            :placeholder="$t('login.usernamePlaceholder')"
            required
          />
        </div>

        <!-- Jelszó -->
        <div class="form-group">
          <label for="password">{{ $t('login.password') }}</label>
          <input
            type="password"
            id="password"
            v-model="formData.password"
            class="form-control"
            :placeholder="$t('login.passwordPlaceholder')"
            required
          />
        </div>

        <!-- CAPTCHA megjelenítése, ha szükséges -->
        <div v-if="showCaptcha" class="captcha-container">
          <label>{{ $t('login.captcha') }}</label>
          <div class="captcha-image-wrapper">
            <img 
              v-if="captchaImage" 
              :src="'data:image/jpeg;base64,' + captchaImage" 
              alt="CAPTCHA" 
              class="captcha-image"
            />
            <button 
              type="button" 
              @click="refreshCaptcha" 
              class="btn btn-sm btn-secondary captcha-refresh"
              :disabled="loading"
            >
              🔄
            </button>
          </div>
          <input
            type="text"
            v-model="formData.captchaAnswer"
            class="form-control mt-2"
            :placeholder="$t('login.captchaPlaceholder')"
            required
          />
        </div>

        <!-- Bejelentkezés gomb -->
        <button 
          type="submit" 
          class="btn btn-primary btn-block login-btn"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm" role="status"></span>
          {{ loading ? $t('login.loggingIn') : $t('login.loginButton') }}
        </button>
      </form>

      <!-- Regisztráció link -->
      <div class="text-center mt-3">
        <p class="register-link">
          {{ $t('login.noAccount') }}
          <router-link to="/register">{{ $t('login.registerLink') }}</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import './styles/LoginView.css';
import { authService } from '../services/api.js';
import { useAuthStore } from '../stores/auth.js';

export default {
  name: 'LoginView',
  data() {
    return {
      formData: {
        username: '',
        password: '',
        captchaAnswer: '',
      },
      showCaptcha: false,
      captchaImage: null,
      errorMessage: '',
      successMessage: '',
      loading: false,
    };
  },
  methods: {
    async handleLogin() {
      this.errorMessage = '';
      this.successMessage = '';
      this.loading = true;

      try {
        const response = await authService.login(this.formData);
        
        // Sikeres bejelentkezés
        const authStore = useAuthStore();
        authStore.setToken(response.token);
        authStore.setUser({
          id: response.id,
          username: response.username,
          name: response.name,
          roles: response.roles,
          lastLoginDate: response.lastLoginDate,
        });

        this.successMessage = this.$t('login.success');
        
        // Átirányítás a főoldalra vagy dashboard-ra
        setTimeout(() => {
          this.$router.push('/home');
        }, 1500);

      } catch (error) {
        this.loading = false;

        if (error.response) {
          const errorData = error.response.data;

          // CAPTCHA szükséges
          if (errorData.code === 'CAPTCHA_REQUIRED') {
            this.showCaptcha = true;
            this.errorMessage = this.$t('login.captchaRequired');
            await this.loadCaptcha();
          } 
          // Hibás felhasználónév vagy jelszó
          else if (errorData.code === 'LOGIN_FAILED') {
            this.errorMessage = this.$t('login.loginFailed');
          } 
          else {
            this.errorMessage = this.$t('login.unknownError');
          }
        } else {
          this.errorMessage = this.$t('login.networkError');
        }
      }
    },

    async loadCaptcha() {
      try {
        this.captchaImage = await authService.getCaptcha();
      } catch (error) {
        console.error('CAPTCHA betöltési hiba:', error);
        this.errorMessage = this.$t('login.captchaLoadError');
      }
    },

    async refreshCaptcha() {
      await this.loadCaptcha();
    },
  },
};
</script>
