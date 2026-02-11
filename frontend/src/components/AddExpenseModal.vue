<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>{{ editMode ? $t('expense.edit') : $t('expense.add') }}</h2>
        <button class="close-btn" @click="closeModal">&times;</button>
      </div>

      <form @submit.prevent="handleSubmit" class="expense-form">
        <div class="form-group">
          <label for="name">{{ $t('expense.name') }} *</label>
          <input
            type="text"
            id="name"
            v-model="formData.name"
            class="form-control"
            required
            :placeholder="$t('expense.namePlaceholder')"
          />
        </div>

        <div class="form-group">
          <label for="price">{{ $t('expense.price') }} (Ft) *</label>
          <input
            type="number"
            id="price"
            v-model="formData.price"
            class="form-control"
            required
            min="0"
            step="1"
          />
        </div>

        <div class="form-group">
          <label for="category">{{ $t('expense.category') }} *</label>
          <select
            id="category"
            v-model="formData.categoryId"
            class="form-control"
            required
          >
            <option value="" disabled>{{ $t('expense.selectCategory') }}</option>
            <option
              v-for="category in categories"
              :key="category.id"
              :value="category.id"
            >
              {{ category.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="date">{{ $t('expense.date') }} *</label>
          <input
            type="date"
            id="date"
            v-model="formData.date"
            class="form-control"
            required
          />
        </div>

        <div class="form-group">
          <label for="description">{{ $t('expense.description') }}</label>
          <textarea
            id="description"
            v-model="formData.description"
            class="form-control"
            rows="3"
            :placeholder="$t('expense.descriptionPlaceholder')"
          ></textarea>
        </div>

        <div v-if="errorMessage" class="alert alert-error">
          {{ errorMessage }}
        </div>

        <div class="modal-actions">
          <button type="button" class="btn-secondary" @click="closeModal">
            {{ $t('common.cancel') }}
          </button>
          <button type="submit" class="btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? $t('common.saving') : $t('common.save') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { categoryService, expenseService } from '../services/api.js';

export default {
  name: 'AddExpenseModal',
  props: {
    isOpen: {
      type: Boolean,
      required: true,
    },
    expense: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      formData: {
        name: '',
        price: '',
        categoryId: '',
        date: new Date().toISOString().split('T')[0],
        description: '',
      },
      categories: [],
      isSubmitting: false,
      errorMessage: '',
    };
  },
  computed: {
    editMode() {
      return this.expense !== null;
    },
  },
  watch: {
    isOpen(newVal) {
      if (newVal) {
        this.loadCategories();
        if (this.expense) {
          this.formData = {
            name: this.expense.name,
            price: this.expense.price,
            categoryId: this.expense.categoryId,
            date: this.expense.date,
            description: this.expense.description || '',
          };
        } else {
          this.resetForm();
        }
      }
    },
  },
  methods: {
    async loadCategories() {
      try {
        this.categories = await categoryService.getAll();
      } catch (error) {
        console.error('Failed to load categories:', error);
        this.errorMessage = this.$t('expense.categoryLoadError');
      }
    },
    async handleSubmit() {
      this.isSubmitting = true;
      this.errorMessage = '';

      try {
        const expenseData = {
          name: this.formData.name,
          price: parseFloat(this.formData.price),
          categoryId: this.formData.categoryId,
          date: this.formData.date,
          description: this.formData.description,
        };

        if (this.editMode) {
          await expenseService.update(this.expense.id, expenseData);
        } else {
          await expenseService.create(expenseData);
        }

        this.$emit('success');
        this.closeModal();
      } catch (error) {
        console.error('Failed to save expense:', error);
        this.errorMessage = this.$t('expense.saveError');
      } finally {
        this.isSubmitting = false;
      }
    },
    closeModal() {
      this.resetForm();
      this.$emit('close');
    },
    resetForm() {
      this.formData = {
        name: '',
        price: '',
        categoryId: '',
        date: new Date().toISOString().split('T')[0],
        description: '',
      };
      this.errorMessage = '';
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background-color: var(--color-card-bg);
  border-radius: 12px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  margin: 0;
  color: var(--color-text);
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  color: var(--color-text);
  cursor: pointer;
  line-height: 1;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: var(--color-border);
}

.expense-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--color-text);
  font-weight: 600;
  font-size: 0.95rem;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 1rem;
  background-color: var(--color-background);
  color: var(--color-text);
  transition: border-color 0.3s;
  box-sizing: border-box;
  font-family: inherit;
}

.form-control:focus {
  outline: none;
  border-color: var(--color-primary);
}

select.form-control {
  cursor: pointer;
}

textarea.form-control {
  resize: vertical;
  min-height: 80px;
}

.alert {
  padding: 0.75rem;
  border-radius: 6px;
  margin-bottom: 1rem;
}

.alert-error {
  background-color: rgba(220, 53, 69, 0.1);
  border: 1px solid rgba(220, 53, 69, 0.3);
  color: #dc3545;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.btn-primary,
.btn-secondary {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: var(--color-primary);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: transparent;
  color: var(--color-text);
  border: 1px solid var(--color-border);
}

.btn-secondary:hover {
  background-color: var(--color-border);
}
</style>
