<template>
  <footer class="app-footer">
    <div class="footer-container">
        <nav class="footer-nav">
            <router-link to="/contact" class="footer-link">{{ $t('footer.contact') }}</router-link>
            <router-link to="/about" class="footer-link">{{ $t('footer.about') }}</router-link>
            <router-link to="/faq" class="footer-link">{{ $t('footer.q&a') }}</router-link>
        </nav>

        <span class="copyright">
            {{ $t('footer.span') }}
        </span>
    </div>
  </footer>
</template>

<script>
import './styles/LandingFooter.css';

export default {
    name: 'LandingFooter'
}
</script>