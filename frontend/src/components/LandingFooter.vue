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
export default {
    name: 'LandingFooter'
}
</script>

<style scoped>
.app-footer {
    border-top: 1px solid var(--color-border);
    background-color: var(--color-card-bg);
    padding: 15px 0;
    transition: background-color 0.3s;
    border-radius: 12px;
    margin-right: 5px;
    margin-left: 5px;
    margin-bottom: 5px;
}

.footer-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 1rem;
}

.footer-nav {
    display: flex;
    gap: 28px;
}

.footer-link {
    flex-shrink: 0;
    color: var(--color-text);
    text-decoration: none;
    font-size: 0.9rem;
    transition: color 0.3s, transform 0.2s;
    display: inline-block;
}

.footer-link:hover {
    color: var(--color-primary);
    transform: translateY(-2px);
}

.copyright {
    font-size: 0.9rem;
    color: var(--color-text);
}

@media (max-width: 600px) {
    .footer-container {
        flex-direction: column;
        gap: 12px;
    }

    .footer-nav {
        flex-direction: column;
        align-items: center;
        gap: 12px;
        width: 100%;
    }

    .copyright {
        text-align: center;
    }
}
</style>