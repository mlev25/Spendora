<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="register-title">{{ $t('register.title') }}</h2>

      <!-- Hiba üzenet -->
      <div v-if="errorMessage" class="alert alert-danger" role="alert">
        {{ errorMessage }}
      </div>

      <!-- Sikeres regisztráció üzenet -->
      <div v-if="successMessage" class="alert alert-success" role="alert">
        {{ successMessage }}
      </div>

      <form @submit.prevent="handleRegister">
        <!-- Név -->
        <div class="form-group">
          <label for="name">{{ $t('register.name') }}</label>
          <input
            type="text"
            id="name"
            v-model="formData.name"
            class="form-control"
            :placeholder="$t('register.namePlaceholder')"
            required
          />
        </div>

        <!-- Felhasználónév -->
        <div class="form-group">
          <label for="username">{{ $t('register.username') }}</label>
          <input
            type="text"
            id="username"
            v-model="formData.username"
            class="form-control"
            :placeholder="$t('register.usernamePlaceholder')"
            required
          />
        </div>

        <!-- Email -->
        <div class="form-group">
          <label for="email">{{ $t('register.email') }}</label>
          <input
            type="email"
            id="email"
            v-model="formData.email"
            class="form-control"
            :placeholder="$t('register.emailPlaceholder')"
            required
          />
        </div>

        <!-- Jelszó -->
        <div class="form-group">
          <label for="password">{{ $t('register.password') }}</label>
          <input
            type="password"
            id="password"
            v-model="formData.password"
            class="form-control"
            :placeholder="$t('register.passwordPlaceholder')"
            required
            minlength="6"
          />
          <small class="form-text">{{ $t('register.passwordHint') }}</small>
        </div>

        <!-- Jelszó megerősítése -->
        <div class="form-group">
          <label for="confirmPassword">{{ $t('register.confirmPassword') }}</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="confirmPassword"
            class="form-control"
            :placeholder="$t('register.confirmPasswordPlaceholder')"
            required
          />
        </div>

        <!-- Regisztráció gomb -->
        <button 
          type="submit" 
          class="btn btn-primary btn-block register-btn"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm" role="status"></span>
          {{ loading ? $t('register.registering') : $t('register.registerButton') }}
        </button>
      </form>

      <!-- Bejelentkezés link -->
      <div class="text-center mt-3">
        <p class="login-link">
          {{ $t('register.hasAccount') }}
          <router-link to="/login">{{ $t('register.loginLink') }}</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { authService } from '../services/api.js';

export default {
  name: 'RegisterView',
  data() {
    return {
      formData: {
        name: '',
        username: '',
        email: '',
        password: '',
        role: 'USER', // Alapértelmezett szerepkör
      },
      confirmPassword: '',
      errorMessage: '',
      successMessage: '',
      loading: false,
    };
  },
  methods: {
    async handleRegister() {
      this.errorMessage = '';
      this.successMessage = '';

      // Jelszó ellenőrzés
      if (this.formData.password !== this.confirmPassword) {
        this.errorMessage = this.$t('register.passwordMismatch');
        return;
      }

      // Jelszó hossz ellenőrzés
      if (this.formData.password.length < 6) {
        this.errorMessage = this.$t('register.passwordTooShort');
        return;
      }

      this.loading = true;

      try {
        const response = await authService.register(this.formData);
        
        // Sikeres regisztráció
        this.successMessage = this.$t('register.success');
        
        // Form ürítése
        this.formData = {
          name: '',
          username: '',
          email: '',
          password: '',
          role: 'USER',
        };
        this.confirmPassword = '';

        // Átirányítás a bejelentkezéshez
        setTimeout(() => {
          this.$router.push('/login');
        }, 2000);

      } catch (error) {
        this.loading = false;

        if (error.response) {
          const errorData = error.response.data;

          // Felhasználónév már foglalt
          if (error.response.status === 409) {
            // Ellenőrizzük, hogy username vagy email a probléma
            if (errorData.message && errorData.message.includes('email')) {
              this.errorMessage = this.$t('register.emailTaken');
            } else {
              this.errorMessage = this.$t('register.usernameTaken');
            }
          } 
          // Egyéb backend hiba
          else {
            this.errorMessage = this.$t('register.unknownError');
          }
        } else {
          this.errorMessage = this.$t('register.networkError');
        }
      }
    },
  },
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: clamp(1rem, 3vw, 2rem);
  background-color: var(--color-background);
}

.register-card {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: clamp(1.5rem, 4vw, 2.5rem);
  width: 100%;
  max-width: 550px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.register-title {
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

.form-text {
  display: block;
  margin-top: 0.375rem;
  font-size: clamp(0.8rem, 1.2vw, 0.85rem);
  color: #6c757d;
}

.register-btn {
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

.register-btn:hover:not(:disabled) {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  color: var(--color-text);
  font-size: clamp(0.85rem, 1.5vw, 0.95rem);
  margin-top: 1rem;
}

.login-link a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: bold;
}

.login-link a:hover {
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

select.form-control {
  cursor: pointer;
}

/* Mobil optimalizálás */
@media (max-width: 480px) {
  .register-container {
    padding: 1rem 0.5rem;
  }
  
  .register-card {
    padding: 1.5rem 1rem;
  }
}
</style>
