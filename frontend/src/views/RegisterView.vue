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
import './styles/RegisterView.css';
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