<template>
    <div class="landing-main-content">

        <h1 class="main-title">Pénzügyeid, egyszerűen átláthatóan.</h1>
        <p class="subtitle">A Spendora segít nyomon követni a kiadásaidat, és mutatja, hol spórolhatsz.</p>

        <div class="card-grid">

            <div class="info-card">
                <h5 class="card-title">💵 Költségkövetés</h5>
                <p class="card-text">Vidd fel kiadásaidat másodpercek alatt, kategorizálva.</p>
            </div>

            <div class="cta-card">
                <h5 class="card-title">🚀 Kezdjük el!</h5>
                <p class="card-text">Ne várj tovább, regisztrálj és tedd átláthatóvá pénzügyeidet.</p>
                <router-link to="/register" class="cta-btn">
                    Regisztrálok Most!
                </router-link>
            </div>

            <div class="info-card">
                <h5 class="card-title">📊 Statisztikák</h5>
                <p class="card-text">Légy tudatos: nézd meg a kiadásaidat gyönyörű diagramokon.</p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'LandingView'
    // Nincs data, methods, stb., csak sima Vue komponens
}
</script>

<style scoped>
.landing-main-content {
    text-align: center;
    padding-top: 50px;
    padding-bottom: 50px;
    background-color: var(--color-background);
}

.main-title {
    font-size: 3rem;
    margin-bottom: 10px;
    /* A szöveg színe a globális --color-text-et használja */
    color: var(--color-text);
}

.subtitle {
    font-size: 1.25rem;
    margin-bottom: 50px;
    color: var(--color-text);
}

/* A 3 kártya elrendezése CSS Grid-del */
.card-grid {
    display: grid;
    /* Alapértelmezésben 1 oszlop mobilra, 20px távolsággal */
    grid-template-columns: 1fr; 
    gap: 20px;
}

/* Reszponzivitás: 768px felett 3 egyenlő oszlop */
@media (min-width: 768px) {
    .card-grid {
        grid-template-columns: repeat(3, 1fr); /* 3 egyenlő szélességű oszlop */
    }
}

/* Kártya alap stílus: HASZNÁLJA A TÉMA VÁLTOZÓKAT! */
.info-card, .cta-card {
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    height: 100%;
    transition: transform 0.3s, background-color 0.3s, box-shadow 0.3s;
    border: 1px solid var(--color-border); /* Téma-függő szegély */
    background-color: var(--color-card-bg); /* Téma-függő háttér */
    color: var(--color-text); /* Téma-függő szövegszín */
}

.info-card:hover {
    transform: translateY(-10px);
}

.cta-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 6px 20px color-mix(in srgb, var(--color-primary) 50%, black);
}

.card-title {
    font-size: 1.5rem;
    margin-bottom: 15px;
    font-weight: bold;
}

/* CTA kártya kiemelése */
.cta-card {
    background-color: var(--color-primary); /* Mindig a kék/kiemelő színt használja */
    color: white; /* A CTA kártyában a szöveg mindig fehér, a jobb olvashatóságért */
    box-shadow: 0 6px 20px color-mix(in srgb, var(--color-primary) 50%, black);
}

.cta-btn {
    display: inline-block;
    padding: 10px 20px;
    margin-top: 20px;
    background-color: white;
    color: var(--color-primary);
    text-decoration: none;
    font-weight: bold;
    border-radius: 6px;
    transition: background-color 0.3s;
}
.cta-btn:hover {
    background-color: rgb(7, 255, 205);
}
</style>