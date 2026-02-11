<template>
  <div class="home-container">
    <div class="welcome-section">
      <h1 class="welcome-title">{{ $t('home.welcome') }}, {{ userName }}!</h1>
      <p class="welcome-subtitle">{{ $t('home.subtitle') }}</p>
    </div>

    <div class="dashboard-grid">
      <!-- Statisztikák kártya -->
      <div class="dashboard-card stats-card">
        <h3 class="card-title">{{ $t('home.stats.title') }}</h3>
        <p class="card-description">{{ $t('home.stats.description') }}</p>
        <div class="card-stats">
          <div class="stat-item">
            <span class="stat-value">{{ expenses.length }}</span>
            <span class="stat-label">{{ $t('home.stats.totalExpenses') }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ totalThisMonth }} Ft</span>
            <span class="stat-label">{{ $t('home.stats.thisMonth') }}</span>
          </div>
        </div>
      </div>

      <!-- Új kiadás kártya -->
      <div class="dashboard-card action-card">
        <h3 class="card-title">{{ $t('home.addExpense.title') }}</h3>
        <p class="card-description">{{ $t('home.addExpense.description') }}</p>
        <button class="action-btn" @click="openAddModal">{{ $t('home.addExpense.button') }}</button>
      </div>

      <!-- Kategóriák kártya -->
      <div class="dashboard-card categories-card">
        <h3 class="card-title">{{ $t('home.categories.title') }}</h3>
        <p class="card-description">{{ $t('home.categories.description') }}</p>
        <div class="category-list">
          <span
            v-for="category in categories"
            :key="category.id"
            class="category-badge"
            :style="{ backgroundColor: category.color }"
          >
            {{ category.name }}
          </span>
        </div>
      </div>

      <!-- Chatbot kártya -->
      <div class="dashboard-card chatbot-card">
        <h3 class="card-title">{{ $t('home.chatbot.title') }}</h3>
        <p class="card-description">{{ $t('home.chatbot.description') }}</p>
        <button class="action-btn secondary">{{ $t('home.chatbot.button') }}</button>
      </div>
    </div>

    <!-- Gyors infó szekció -->
    <div class="quick-info">
      <div class="info-box">
        <h4>{{ $t('home.info.lastLogin') }}</h4>
        <p>{{ formattedLastLogin }}</p>
      </div>
      <div class="info-box">
        <h4>{{ $t('home.info.accountType') }}</h4>
        <p>{{ userRole }}</p>
      </div>
    </div>

    <!-- Expense lista -->
    <ExpenseList
      :expenses="expenses"
      :categories="categories"
      :loading="loading"
      @add="openAddModal"
      @edit="openEditModal"
      @delete="deleteExpense"
    />

    <!-- Add/Edit Modal -->
    <AddExpenseModal
      :isOpen="isModalOpen"
      :expense="selectedExpense"
      @close="closeModal"
      @success="handleExpenseSuccess"
    />
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth.js';
import { expenseService, categoryService } from '../services/api.js';
import AddExpenseModal from '../components/AddExpenseModal.vue';
import ExpenseList from '../components/ExpenseList.vue';

export default {
  name: 'HomeView',
  components: {
    AddExpenseModal,
    ExpenseList,
  },
  data() {
    return {
      expenses: [],
      categories: [],
      loading: false,
      isModalOpen: false,
      selectedExpense: null,
    };
  },
  computed: {
    userName() {
      const authStore = useAuthStore();
      return authStore.user?.name || authStore.user?.username || 'Felhasználó';
    },
    userRole() {
      const authStore = useAuthStore();
      return authStore.user?.roles?.[0] || 'USER';
    },
    formattedLastLogin() {
      const authStore = useAuthStore();
      if (!authStore.user?.lastLoginDate) return this.$t('home.info.noData');
      
      const date = new Date(authStore.user.lastLoginDate);
      return date.toLocaleString(this.$i18n.locale);
    },
    totalThisMonth() {
      const now = new Date();
      const thisMonth = now.getMonth();
      const thisYear = now.getFullYear();

      const total = this.expenses
        .filter((expense) => {
          const expenseDate = new Date(expense.date);
          return (
            expenseDate.getMonth() === thisMonth &&
            expenseDate.getFullYear() === thisYear
          );
        })
        .reduce((sum, expense) => sum + parseFloat(expense.price), 0);

      return new Intl.NumberFormat('hu-HU').format(total);
    },
  },
  async mounted() {
    await this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const [expensesData, categoriesData] = await Promise.all([
          expenseService.getAll(),
          categoryService.getAll(),
        ]);
        this.expenses = expensesData;
        this.categories = categoriesData;
      } catch (error) {
        console.error('Failed to load data:', error);
      } finally {
        this.loading = false;
      }
    },
    openAddModal() {
      this.selectedExpense = null;
      this.isModalOpen = true;
    },
    openEditModal(expense) {
      this.selectedExpense = expense;
      this.isModalOpen = true;
    },
    closeModal() {
      this.isModalOpen = false;
      this.selectedExpense = null;
    },
    async handleExpenseSuccess() {
      await this.loadData();
    },
    async deleteExpense(expenseId) {
      try {
        await expenseService.delete(expenseId);
        await this.loadData();
      } catch (error) {
        console.error('Failed to delete expense:', error);
        alert(this.$t('expense.deleteError'));
      }
    },
  },
};
</script>

<style scoped>
.home-container {
  padding: clamp(1rem, 3vw, 2rem);
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  margin-bottom: clamp(2rem, 4vw, 3rem);
}

.welcome-title {
  font-size: clamp(1.75rem, 4vw, 2.5rem);
  color: var(--color-text);
  margin-bottom: 0.5rem;
  font-weight: 800;
}

.welcome-subtitle {
  font-size: clamp(1rem, 2vw, 1.25rem);
  color: var(--color-text);
  opacity: 0.8;
}

.dashboard-grid {
  display: grid;
  gap: clamp(1rem, 2vw, 1.5rem);
  margin-bottom: 2rem;
  grid-template-columns: 1fr;
}

@media (min-width: 640px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.dashboard-card {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: clamp(1.5rem, 3vw, 2rem);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.dashboard-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.card-title {
  font-size: clamp(1.25rem, 2.5vw, 1.5rem);
  color: var(--color-text);
  font-weight: bold;
  margin: 0;
}

.card-description {
  font-size: clamp(0.9rem, 1.5vw, 1rem);
  color: var(--color-text);
  opacity: 0.8;
  margin: 0;
  flex-grow: 1;
}

.card-stats {
  display: flex;
  gap: 1.5rem;
  margin-top: 1rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-value {
  font-size: clamp(1.5rem, 3vw, 2rem);
  font-weight: bold;
  color: var(--color-primary);
}

.stat-label {
  font-size: clamp(0.8rem, 1.2vw, 0.9rem);
  color: var(--color-text);
  opacity: 0.7;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.category-badge {
  display: inline-block;
  padding: 0.375rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
}

.action-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  background-color: var(--color-primary);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
  font-size: clamp(0.9rem, 1.5vw, 1rem);
}

.action-btn:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}

.action-btn.secondary {
  background-color: transparent;
  border: 2px solid var(--color-primary);
  color: var(--color-primary);
}

.action-btn.secondary:hover {
  background-color: var(--color-primary);
  color: white;
}

.quick-info {
  display: grid;
  gap: 1rem;
  margin-top: 2rem;
}

@media (min-width: 640px) {
  .quick-info {
    grid-template-columns: repeat(2, 1fr);
  }
}

.info-box {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1.25rem;
}

.info-box h4 {
  font-size: clamp(0.95rem, 1.5vw, 1.1rem);
  color: var(--color-text);
  margin: 0 0 0.5rem 0;
  font-weight: 600;
}

.info-box p {
  font-size: clamp(0.85rem, 1.5vw, 1rem);
  color: var(--color-text);
  opacity: 0.8;
  margin: 0;
}

@media (max-width: 480px) {
  .card-stats {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>
