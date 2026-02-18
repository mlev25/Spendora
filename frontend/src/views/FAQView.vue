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
import './styles/FAQView.css';

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
