<template>
    <div class="landing-main-content">

        <h1 class="main-title">{{ $t('landing.title') }}</h1>
        <p class="subtitle">{{ $t('landing.subtitle') }}</p>

        <div class="card-grid">

            <div class="info-card">
                <h5 class="card-title">{{ $t('landing.card-left.title') }}</h5>
                <p class="card-text">{{ $t('landing.card-left.text') }}</p>
            </div>

            <div class="cta-card">
                <h5 class="card-title">{{ $t('landing.card-center.title') }}</h5>
                <p class="card-text">{{ $t('landing.card-center.text') }}</p>
                <router-link to="/register" class="cta-btn">
                    {{ $t('landing.card-center.button') }}
                </router-link>
            </div>

            <div class="info-card">
                <h5 class="card-title">{{ $t('landing.card-right.title') }}</h5>
                <p class="card-text">{{ $t('landing.card-right.text') }}</p>
            </div>
        </div>
    </div>
</template>

<script>
import './styles/LandingView.css';

export default {
    name: 'LandingView'
    // Nincs data, methods, stb., csak sima Vue komponens
}
</script>