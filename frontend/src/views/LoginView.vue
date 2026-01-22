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

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: clamp(1rem, 3vw, 2rem);
  background-color: var(--color-background);
}

.login-card {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: clamp(1.5rem, 4vw, 2.5rem);
  width: 100%;
  max-width: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: clamp(1.5rem, 3vw, 2rem);
  color: var(--color-text);
  font-size: clamp(1.5rem, 4vw, 2rem);
  font-weight: bold;
}

.form-group {
  margin-bottom: clamp(1rem, 2vw, 1.5rem);
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--color-text);
  font-weight: 600;
  font-size: clamp(0.9rem, 1.5vw, 1rem);
}

.form-control {
  width: 100%;
  padding: clamp(0.75rem, 2vw, 1rem);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: clamp(0.9rem, 1.5vw, 1rem);
  background-color: var(--color-background);
  color: var(--color-text);
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.captcha-container {
  margin-bottom: 1.5rem;
}

.captcha-container label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--color-text);
  font-weight: 600;
  font-size: clamp(0.9rem, 1.5vw, 1rem);
}

.captcha-image-wrapper {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.captcha-image {
  border: 1px solid var(--color-border);
  border-radius: 6px;
  max-width: 100%;
  height: auto;
}

.captcha-refresh {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1.2rem;
}

.login-btn {
  width: 100%;
  padding: clamp(0.75rem, 2vw, 1rem);
  font-size: clamp(1rem, 2vw, 1.1rem);
  font-weight: bold;
  border: none;
  border-radius: 6px;
  background-color: var(--color-primary);
  color: white;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.login-btn:hover:not(:disabled) {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-link {
  color: var(--color-text);
  font-size: clamp(0.85rem, 1.5vw, 0.95rem);
  margin-top: 1rem;
}

.register-link a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: bold;
}

.register-link a:hover {
  text-decoration: underline;
}

.alert {
  padding: 0.75rem;
  border-radius: 6px;
  margin-bottom: 1.5rem;
  font-size: clamp(0.85rem, 1.5vw, 0.95rem);
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
  margin-right: 0.5rem;
  border-width: 0.15em;
}

/* Mobil optimalizálás */
@media (max-width: 480px) {
  .login-container {
    padding: 1rem 0.5rem;
  }
  
  .login-card {
    padding: 1.5rem 1rem;
  }
  
  .captcha-image-wrapper {
    justify-content: center;
  }
}
</style>
