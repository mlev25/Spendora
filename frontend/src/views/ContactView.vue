<template>
  <div class="contact-container">
    <div class="contact-header">
      <h1 class="page-title">{{ $t('contact.title') }}</h1>
      <p class="page-subtitle">{{ $t('contact.subtitle') }}</p>
    </div>

    <div class="contact-content">
      <!-- Kapcsolati információk -->
      <div class="contact-info-section">
        <div class="info-card">
          <h3>{{ $t('contact.email.title') }}</h3>
          <p>spendora.support@example.com</p>
        </div>

        <div class="info-card">
          <h3>{{ $t('contact.social.title') }}</h3>
          <div class="social-links">
            <a href="https://github.com/mlev25/Spendora" class="social-link">GitHub</a>
            <a href="https://www.linkedin.com/in/levente-m%C3%A9sz%C3%A1ros-327454361/" class="social-link">LinkedIn</a>
          </div>
        </div>
      </div>

      <!-- Kapcsolatfelvételi űrlap -->
      <div class="contact-form-section">
        <h2>{{ $t('contact.form.title') }}</h2>
        <form @submit.prevent="handleSubmit" class="contact-form">
          <div class="form-group">
            <label for="name">{{ $t('contact.form.name') }}</label>
            <input
              type="text"
              id="name"
              v-model="formData.name"
              class="form-control"
              required
            />
          </div>

          <div class="form-group">
            <label for="email">{{ $t('contact.form.email') }}</label>
            <input
              type="email"
              id="email"
              v-model="formData.email"
              class="form-control"
              required
            />
          </div>

          <div class="form-group">
            <label for="subject">{{ $t('contact.form.subject') }}</label>
            <input
              type="text"
              id="subject"
              v-model="formData.subject"
              class="form-control"
              required
            />
          </div>

          <div class="form-group">
            <label for="message">{{ $t('contact.form.message') }}</label>
            <textarea
              id="message"
              v-model="formData.message"
              class="form-control"
              rows="6"
              required
            ></textarea>
          </div>

          <button type="submit" class="submit-btn" :disabled="isSubmitting">
            {{ isSubmitting ? $t('contact.form.sending') : $t('contact.form.send') }}
          </button>

          <div v-if="successMessage" class="alert alert-success">
            {{ successMessage }}
          </div>

          <div v-if="errorMessage" class="alert alert-error">
            {{ errorMessage }}
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { contactService } from '../services/contactService';
import './styles/ContactView.css';

export default {
  name: 'ContactView',
  data() {
    return {
      formData: {
        name: '',
        email: '',
        subject: '',
        message: ''
      },
      isSubmitting: false,
      successMessage: '',
      errorMessage: ''
    };
  },
  methods: {
    async handleSubmit() {
      this.isSubmitting = true;
      this.successMessage = '';
      this.errorMessage = '';
      try {
        await contactService.sendMessage(this.formData);
        this.successMessage = this.$t('contact.form.success');
        this.formData = { name: '', email: '', subject: '', message: '' };
        setTimeout(() => { this.successMessage = ''; }, 5000);
      } catch (error) {
        this.errorMessage = this.$t('contact.form.error');
      } finally {
        this.isSubmitting = false;
      }
    }
  }
};
</script>
