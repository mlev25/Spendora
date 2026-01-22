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
          <a href="#" class="creator-link">GitHub</a>
          <a href="#" class="creator-link">LinkedIn</a>
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
export default {
  name: 'AboutView'
};
</script>

<style scoped>
.about-container {
  padding: clamp(1rem, 3vw, 2rem);
  max-width: 1200px;
  margin: 0 auto;
}

.about-header {
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

.about-content {
  display: flex;
  flex-direction: column;
  gap: clamp(2rem, 4vw, 3rem);
}

.content-section {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: clamp(1.5rem, 3vw, 2.5rem);
}

.content-section h2 {
  font-size: clamp(1.5rem, 3vw, 2rem);
  color: var(--color-text);
  margin-bottom: 1rem;
  text-align: center;
}

.content-section p {
  font-size: clamp(0.95rem, 1.5vw, 1.1rem);
  color: var(--color-text);
  line-height: 1.8;
  margin-bottom: 1rem;
  opacity: 0.9;
}

.features-grid {
  display: grid;
  gap: 1.5rem;
  margin-top: 2rem;
}

@media (min-width: 640px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.feature-card {
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  font-size: 1.1rem;
  color: var(--color-text);
  margin-bottom: 0.5rem;
}

.feature-card p {
  font-size: 0.95rem;
  opacity: 0.8;
  margin: 0;
}

.tech-stack {
  display: grid;
  gap: 2rem;
  margin-top: 2rem;
}

@media (min-width: 768px) {
  .tech-stack {
    grid-template-columns: repeat(2, 1fr);
  }
}

.tech-group {
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1.5rem;
}

.tech-group h3 {
  font-size: 1.25rem;
  color: var(--color-primary);
  margin-bottom: 1rem;
  text-align: center;
}

.tech-group ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tech-group li {
  padding: 0.5rem;
  color: var(--color-text);
  text-align: center;
  border-bottom: 1px solid var(--color-border);
}

.tech-group li:last-child {
  border-bottom: none;
}

.creator-section {
  text-align: center;
}

.creator-links {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1.5rem;
  flex-wrap: wrap;
}

.creator-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
  padding: 0.5rem 1rem;
  border: 2px solid var(--color-primary);
  border-radius: 6px;
  transition: all 0.3s;
}

.creator-link:hover {
  background-color: var(--color-primary);
  color: white;
  transform: translateY(-2px);
}

.license-section {
  text-align: center;
  padding: 1rem;
}

.license-text {
  font-size: 0.9rem;
  opacity: 0.7;
  margin: 0;
}
</style>
