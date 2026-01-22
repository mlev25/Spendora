<template>
  <div class="faq-container">
    <div class="faq-header">
      <h1 class="page-title">{{ $t('faq.title') }}</h1>
      <p class="page-subtitle">{{ $t('faq.subtitle') }}</p>
    </div>

    <div class="faq-content">
      <div
        v-for="(item, index) in faqItems"
        :key="index"
        class="faq-item"
        :class="{ 'active': activeIndex === index }"
      >
        <div class="faq-question" @click="toggleItem(index)">
          <h3>{{ $t(item.question) }}</h3>
          <span class="toggle-icon">{{ activeIndex === index ? '−' : '+' }}</span>
        </div>
        <transition name="fade">
          <div v-show="activeIndex === index" class="faq-answer">
            <p>{{ $t(item.answer) }}</p>
          </div>
        </transition>
      </div>
    </div>

    <!-- További segítség -->
    <div class="help-section">
      <h2>{{ $t('faq.help.title') }}</h2>
      <p>{{ $t('faq.help.description') }}</p>
      <router-link to="/contact" class="contact-btn">
        {{ $t('faq.help.button') }}
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FAQView',
  data() {
    return {
      activeIndex: null,
      faqItems: [
        {
          question: 'faq.items.whatIs.question',
          answer: 'faq.items.whatIs.answer'
        },
        {
          question: 'faq.items.howToRegister.question',
          answer: 'faq.items.howToRegister.answer'
        },
        {
          question: 'faq.items.isFree.question',
          answer: 'faq.items.isFree.answer'
        },
        {
          question: 'faq.items.dataSecure.question',
          answer: 'faq.items.dataSecure.answer'
        },
        {
          question: 'faq.items.howToAddExpense.question',
          answer: 'faq.items.howToAddExpense.answer'
        },
        {
          question: 'faq.items.statistics.question',
          answer: 'faq.items.statistics.answer'
        },
        {
          question: 'faq.items.mobile.question',
          answer: 'faq.items.mobile.answer'
        },
        {
          question: 'faq.items.forgotPassword.question',
          answer: 'faq.items.forgotPassword.answer'
        },
        {
          question: 'faq.items.deleteAccount.question',
          answer: 'faq.items.deleteAccount.answer'
        }
      ]
    };
  },
  methods: {
    toggleItem(index) {
      this.activeIndex = this.activeIndex === index ? null : index;
    }
  }
};
</script>

<style scoped>
.faq-container {
  padding: clamp(1rem, 3vw, 2rem);
  max-width: 900px;
  margin: 0 auto;
}

.faq-header {
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

.faq-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 3rem;
}

.faq-item {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.faq-item.active {
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.2);
  border-color: var(--color-primary);
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: clamp(1rem, 2vw, 1.5rem);
  cursor: pointer;
  user-select: none;
  transition: background-color 0.3s;
}

.faq-question:hover {
  background-color: var(--color-background);
}

.faq-question h3 {
  font-size: clamp(1rem, 2vw, 1.25rem);
  color: var(--color-text);
  margin: 0;
  font-weight: 600;
  flex: 1;
  padding-right: 1rem;
}

.toggle-icon {
  font-size: 1.5rem;
  color: var(--color-primary);
  font-weight: bold;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.faq-answer {
  padding: 0 clamp(1rem, 2vw, 1.5rem) clamp(1rem, 2vw, 1.5rem);
  background-color: var(--color-background);
}

.faq-answer p {
  font-size: clamp(0.95rem, 1.5vw, 1.05rem);
  color: var(--color-text);
  line-height: 1.8;
  margin: 0;
  opacity: 0.9;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, max-height 0.3s;
  max-height: 500px;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  max-height: 0;
}

.help-section {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: clamp(1.5rem, 3vw, 2.5rem);
  text-align: center;
}

.help-section h2 {
  font-size: clamp(1.5rem, 3vw, 2rem);
  color: var(--color-text);
  margin-bottom: 1rem;
}

.help-section p {
  font-size: clamp(0.95rem, 1.5vw, 1.1rem);
  color: var(--color-text);
  opacity: 0.8;
  margin-bottom: 1.5rem;
}

.contact-btn {
  display: inline-block;
  padding: 0.75rem 2rem;
  background-color: var(--color-primary);
  color: white;
  text-decoration: none;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s;
  font-size: clamp(0.95rem, 1.5vw, 1.05rem);
}

.contact-btn:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

@media (max-width: 480px) {
  .faq-question {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .toggle-icon {
    align-self: flex-end;
  }
}
</style>
