<template>
  <footer class="app-footer">
    <div class="footer-container">
        <nav class="footer-nav">
            <router-link to="#" class="footer-link">Rólunk (About)</router-link>
            <router-link to="#" class="footer-link">Licenc</router-link>
            <router-link to="#" class="footer-link">Gyakori kérdések</router-link>
        </nav>

        <span class="copyright">
            &copy; 2025 Spendora - Szakdolgozat
        </span>
    </div>
  </footer>
</template>

<script>
export default {
    name: 'LandingFooter' // Fontos a név!
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
    max-width: 1200px;
    width: 90%;
    margin: 0 auto;
}

.footer-nav {
    display: flex;
    gap: 15px;
}

.footer-link {
    color: var(--color-text);
    text-decoration: none;
    font-size: 0.9rem;
    transition: color 0.3s;
}

.copyright {
    font-size: 0.9rem;
    color: var(--color-text);
}

/* ... a korábban megadott AppFooter.vue (most már app-footer) stílusok kerülnek ide ... */
</style>