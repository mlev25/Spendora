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

        <!-- AI kategorizálás checkbox (csak új kiadásnál) -->
        <div v-if="!editMode" class="form-group ai-checkbox-group">
          <label class="ai-checkbox-label">
            <input
              type="checkbox"
              v-model="useAiPrediction"
              @change="handleAiToggle"
              :disabled="!formData.name || aiPredicting"
            />
            {{ $t('expense.aiPrediction') }}
            <span v-if="aiPredicting" class="ai-loading">{{ $t('expense.aiLoading') }}</span>
          </label>
          <p v-if="aiPredictedMessage" class="ai-success">{{ aiPredictedMessage }}</p>
          <p v-else class="ai-hint">{{ $t('expense.aiHint') }}</p>
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
import './styles/AddExpenseModal.css';

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
      useAiPrediction: false,
      aiPredicting: false,
      aiPredictedMessage: '',
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
    'formData.categoryId'(newVal, oldVal) {
      // Ha a user manuálisan változtatja a kategóriát, és nem az AI állította be
      if (newVal && !this.aiPredicting && this.useAiPrediction) {
        this.useAiPrediction = false;
        this.aiPredictedMessage = '';
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
      this.useAiPrediction = false;
      this.aiPredicting = false;
      this.aiPredictedMessage = '';
    },
    async handleAiToggle() {
      if (this.useAiPrediction && this.formData.name) {
        await this.predictCategoryWithAi();
      }
    },
    async predictCategoryWithAi() {
      this.aiPredicting = true;
      this.errorMessage = '';
      this.aiPredictedMessage = '';

      try {
        const response = await expenseService.predictCategory({
          name: this.formData.name,
          price: this.formData.price ? parseFloat(this.formData.price) : null,
          description: this.formData.description,
        });

        // Megkeressük a kategóriát név alapján
        const predictedCategory = this.categories.find(
          (cat) => cat.name === response.categoryName
        );

        if (predictedCategory) {
          this.formData.categoryId = predictedCategory.id;
          this.aiPredictedMessage = this.$t('expense.aiPredicted', { category: predictedCategory.name });
          
          // Üzenet eltüntetése 5 másodperc után
          setTimeout(() => {
            this.aiPredictedMessage = '';
          }, 5000);
        }
      } catch (error) {
        console.error('AI prediction failed:', error);
        this.useAiPrediction = false;
        // Nem dobunk errort, mert az AI opcionális funkció
      }
    },
  },
};
</script>

