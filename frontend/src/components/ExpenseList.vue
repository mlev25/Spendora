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
import './styles/ExpenseList.css';

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

