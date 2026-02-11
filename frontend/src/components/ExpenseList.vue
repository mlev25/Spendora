<template>
  <div class="expense-list-container">
    <div class="list-header">
      <h2>{{ $t('expense.myExpenses') }}</h2>
      <button class="btn-add" @click="$emit('add')">
        + {{ $t('expense.addNew') }}
      </button>
    </div>

    <div v-if="loading" class="loading-state">
      {{ $t('common.loading') }}...
    </div>

    <div v-else-if="expenses.length === 0" class="empty-state">
      <p>{{ $t('expense.noExpenses') }}</p>
      <button class="btn-primary" @click="$emit('add')">
        {{ $t('expense.addFirst') }}
      </button>
    </div>

    <div v-else class="expense-table-wrapper">
      <table class="expense-table">
        <thead>
          <tr>
            <th>{{ $t('expense.name') }}</th>
            <th>{{ $t('expense.category') }}</th>
            <th>{{ $t('expense.price') }}</th>
            <th>{{ $t('expense.date') }}</th>
            <th>{{ $t('expense.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="expense in expenses" :key="expense.id">
            <td>
              <div class="expense-name">{{ expense.name }}</div>
              <div v-if="expense.description" class="expense-desc">
                {{ expense.description }}
              </div>
            </td>
            <td>
              <span
                class="category-badge"
                :style="{ backgroundColor: getCategoryColor(expense.categoryId) }"
              >
                {{ getCategoryName(expense.categoryId) }}
              </span>
            </td>
            <td class="price-cell">{{ formatPrice(expense.price) }} Ft</td>
            <td>{{ formatDate(expense.date) }}</td>
            <td>
              <div class="action-buttons">
                <button
                  class="btn-icon btn-edit"
                  @click="$emit('edit', expense)"
                  :title="$t('expense.edit')"
                >
                  <img src="/pencil.png" alt="Edit" class="icon-img" />
                </button>
                <button
                  class="btn-icon btn-delete"
                  @click="handleDelete(expense)"
                  :title="$t('expense.delete')"
                >
                  <img src="/bin.png" alt="Delete" class="icon-img" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ExpenseList',
  props: {
    expenses: {
      type: Array,
      required: true,
    },
    categories: {
      type: Array,
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    getCategoryName(categoryId) {
      const category = this.categories.find((c) => c.id === categoryId);
      return category ? category.name : this.$t('expense.unknown');
    },
    getCategoryColor(categoryId) {
      const category = this.categories.find((c) => c.id === categoryId);
      return category ? category.color : '#BDC3C7';
    },
    formatPrice(price) {
      return new Intl.NumberFormat('hu-HU').format(price);
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString(this.$i18n.locale);
    },
    handleDelete(expense) {
      if (confirm(this.$t('expense.deleteConfirm', { name: expense.name }))) {
        this.$emit('delete', expense.id);
      }
    },
  },
};
</script>

<style scoped>
.expense-list-container {
  background-color: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: clamp(1rem, 2vw, 1.5rem);
  margin-top: 2rem;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.list-header h2 {
  margin: 0;
  color: var(--color-text);
  font-size: clamp(1.25rem, 3vw, 1.75rem);
}

.btn-add {
  background-color: var(--color-primary);
  color: white;
  border: none;
  padding: 0.625rem 1.25rem;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-add:hover {
  background-color: #0056b3;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: var(--color-text);
}

.empty-state p {
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
  opacity: 0.8;
}

.btn-primary {
  background-color: var(--color-primary);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.expense-table-wrapper {
  overflow-x: auto;
}

.expense-table {
  width: 100%;
  border-collapse: collapse;
}

.expense-table thead {
  background-color: var(--color-background);
}

.expense-table th {
  padding: 0.75rem;
  text-align: left;
  font-weight: 600;
  color: var(--color-text);
  border-bottom: 2px solid var(--color-border);
  font-size: 0.9rem;
}

.expense-table td {
  padding: 1rem 0.75rem;
  border-bottom: 1px solid var(--color-border);
  color: var(--color-text);
}

.expense-table tbody tr:hover {
  background-color: var(--color-background);
}

.expense-name {
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.expense-desc {
  font-size: 0.85rem;
  opacity: 0.7;
  margin-top: 0.25rem;
}

.category-badge {
  display: inline-block;
  padding: 0.375rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
  white-space: nowrap;
}

.price-cell {
  font-weight: 600;
  font-size: 1rem;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  background: none;
  border: 1px solid var(--color-border);
  padding: 0.375rem 0.625rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-img {
  width: 16px;
  height: 16px;
  display: block;
}

.btn-icon:hover {
  background-color: var(--color-background);
  transform: scale(1.1);
}

.btn-edit:hover {
  border-color: #007bff;
  background-color: rgba(0, 123, 255, 0.1);
}

.btn-delete:hover {
  border-color: #dc3545;
  background-color: rgba(220, 53, 69, 0.1);
}

@media (max-width: 768px) {
  .expense-table {
    font-size: 0.85rem;
  }

  .expense-table th,
  .expense-table td {
    padding: 0.5rem 0.375rem;
  }

  .category-badge {
    font-size: 0.75rem;
    padding: 0.25rem 0.5rem;
  }
}
</style>
