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
            <a href="#" class="social-link">GitHub</a>
            <a href="#" class="social-link">LinkedIn</a>
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
        </form>
      </div>
    </div>
  </div>
</template>

<script>
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
      successMessage: ''
    };
  },
  methods: {
    handleSubmit() {
      this.isSubmitting = true;
      // Szimuláljuk az elküldést
      setTimeout(() => {
        this.successMessage = this.$t('contact.form.success');
        this.formData = { name: '', email: '', subject: '', message: '' };
        this.isSubmitting = false;
        
        setTimeout(() => {
          this.successMessage = '';
        }, 5000);
      }, 1000);
    }
  }
};
</script>

<style scoped>
.contact-container {
  padding: clamp(1rem, 3vw, 2rem);
  max-width: 1200px;
  margin: 0 auto;
}

.contact-header {
  text-align: center;
  margin-bottom: clamp(2rem, 4vw, 3rem);
}

.page-title {
  font-size: clamp(2rem, 5vw, 3rem);
  color: var(--color-text);
  margin-bottom: 0.5rem;
  font-weight: 800;
}

.page-subtitle {
  font-size: clamp(1rem, 2vw, 1.25rem);
  color: var(--color-text);
  opacity: 0.8;
}

.contact-content {
  display: grid;
  gap: clamp(2rem, 4vw, 3rem);
}

.contact-info-section {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: 1fr;
}

@media (min-width: 768px) {
  .contact-info-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

.info-card {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  transition: transform 0.3s;
}

.info-card:hover {
  transform: translateY(-5px);
}

.info-card h3 {
  font-size: 1.25rem;
  color: var(--color-text);
  margin-bottom: 0.75rem;
}

.info-card p {
  color: var(--color-text);
  opacity: 0.8;
}

.social-links {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.social-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.social-link:hover {
  color: #0056b3;
  text-decoration: underline;
}

.contact-form-section {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: clamp(1.5rem, 3vw, 2.5rem);
}

.contact-form-section h2 {
  font-size: clamp(1.5rem, 3vw, 2rem);
  color: var(--color-text);
  margin-bottom: 1.5rem;
  text-align: center;
}

.contact-form {
  max-width: 600px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--color-text);
  font-weight: 600;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 1rem;
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

textarea.form-control {
  resize: vertical;
  font-family: inherit;
}

.submit-btn {
  width: 100%;
  padding: 1rem;
  font-size: 1.1rem;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  background-color: var(--color-primary);
  color: white;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.alert {
  padding: 1rem;
  border-radius: 6px;
  margin-top: 1rem;
  text-align: center;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}
</style>
