<template>
  <div class="about-container">
    <div class="about-header">
      <h1 class="page-title">{{ $t('about.title') }}</h1>
      <p class="page-subtitle">{{ $t('about.subtitle') }}</p>
    </div>

    <div class="about-content">
      <!-- Projekt bemutató -->
      <section class="content-section">
        <h2>{{ $t('about.project.title') }}</h2>
        <p>{{ $t('about.project.description1') }}</p>
        <p>{{ $t('about.project.description2') }}</p>
      </section>

      <!-- Funkciók -->
      <section class="content-section features-section">
        <h2>{{ $t('about.features.title') }}</h2>
        <div class="features-grid">
          <div class="feature-card">
            <h3>{{ $t('about.features.expense.title') }}</h3>
            <p>{{ $t('about.features.expense.description') }}</p>
          </div>
          <div class="feature-card">
            <h3>{{ $t('about.features.statistics.title') }}</h3>
            <p>{{ $t('about.features.statistics.description') }}</p>
          </div>
          <div class="feature-card">
            <h3>{{ $t('about.features.categories.title') }}</h3>
            <p>{{ $t('about.features.categories.description') }}</p>
          </div>
          <div class="feature-card">
            <h3>{{ $t('about.features.chatbot.title') }}</h3>
            <p>{{ $t('about.features.chatbot.description') }}</p>
          </div>
        </div>
      </section>

      <!-- Technológiák -->
      <section class="content-section tech-section">
        <h2>{{ $t('about.tech.title') }}</h2>
        <div class="tech-stack">
          <div class="tech-group">
            <h3>Frontend</h3>
            <ul>
              <li>Vue 3</li>
              <li>Vite</li>
              <li>Pinia</li>
              <li>Vue Router</li>
              <li>Vue i18n</li>
              <li>Axios</li>
            </ul>
          </div>
          <div class="tech-group">
            <h3>Backend</h3>
            <ul>
              <li>Spring Boot</li>
              <li>Spring Security</li>
              <li>JWT Authentication</li>
              <li>PostgreSQL</li>
              <li>JPA/Hibernate</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Készítő -->
      <section class="content-section creator-section">
        <h2>{{ $t('about.creator.title') }}</h2>
        <p>{{ $t('about.creator.description') }}</p>
        <p>{{ $t('about.creator.thesis') }}</p>
        <div class="creator-links">
          <a href="https://github.com/mlev25/Spendora" class="creator-link">GitHub</a>
          <a href="https://www.linkedin.com/in/levente-m%C3%A9sz%C3%A1ros-327454361/" class="creator-link">LinkedIn</a>
          <a href="/contact" class="creator-link">{{ $t('about.creator.contact') }}</a>
        </div>
      </section>

      <!-- Licensz -->
      <section class="content-section license-section">
        <p class="license-text">© 2026 Spendora. {{ $t('about.license') }}</p>
      </section>
    </div>
  </div>
</template>

<script>
import './styles/AboutView.css';

export default {
  name: 'AboutView'
};
</script>
