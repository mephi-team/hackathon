// components/TransactionForm.tsx

import React, { useEffect, useState } from 'react';
import { v4 as uuidv4 } from 'uuid'; // Генерация уникального ID
import { Transaction } from '../interfaces/Transaction';

interface TransactionFormProps {
  addTransaction: (transaction: Transaction) => void;
  updateTransaction: (transaction: Transaction) => void;
  editingTransaction: Transaction | null;
}

const TransactionForm: React.FC<TransactionFormProps> = ({
                                                           addTransaction,
                                                           updateTransaction,
                                                           editingTransaction,
                                                         }) => {
  const [formData, setFormData] = useState<Transaction>({
    id: null,
    personType: 'Физическое лицо',
    operationDate: '',
    transactionType: 'INCOME',
    comment: '',
    amount: '',
    status: 'NEW',
    senderBank: '',
    account: '',
    receiverBank: '',
    receiverInn: '',
    receiverAccount: '',
    category: '',
    receiverPhone: '',
  });
  const [isEditMode, setIsEditMode] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string }>({}); // Хранилище ошибок валидации

  useEffect(() => {
    if (editingTransaction) {
      setFormData(editingTransaction); // Заполняем форму данными редактируемой транзакции
      setIsEditMode(true);
    } else {
      resetForm(); // Сбрасываем форму, если нет редактируемой транзакции
    }
  }, [editingTransaction]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setErrors((prev) => ({ ...prev, [name]: '' })); // Очищаем ошибку для этого поля
  };

  const validateForm = (): boolean => {
    const newErrors: { [key: string]: string } = {};
    if (!formData.personType) newErrors.personType = 'Тип лица обязателен';
    if (!formData.operationDate) newErrors.operationDate = 'Дата и время обязательны';
    if (!formData.transactionType) newErrors.transactionType = 'Тип транзакции обязателен';
    if (!formData.amount) {
      newErrors.amount = 'Сумма обязательна';
    } else if (!/^\d+(\.\d{1,5})?$/.test(formData.amount)) {
      newErrors.amount = 'Сумма должна быть числом с точностью до 5 знаков';
    }
    if (!formData.status) newErrors.status = 'Статус операции обязателен';
    if (formData.receiverInn && !/^\d{10}$|^\d{12}$/.test(formData.receiverInn)) {
      newErrors.receiverInn = 'ИНН должен содержать 10 или 12 цифр';
    }
    if (formData.receiverPhone && !/^(\+7|8)\d{10}$/.test(formData.receiverPhone)) {
      newErrors.receiverPhone = 'Телефон должен соответствовать формату +7/8 XXXXXXXXXX';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0; // Форма валидна, если ошибок нет
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return; // Если форма невалидна, прерываем отправку

    setIsLoading(true);
    try {
      if (isEditMode && formData.id) {
        await updateTransaction(formData); // Обновляем транзакцию
      } else {
        const newTransaction = { ...formData, id: uuidv4() }; // Генерируем уникальный ID
        await addTransaction(newTransaction); // Добавляем новую транзакцию
      }
      resetForm(); // Сбрасываем форму
    } catch (error) {
      console.error('Ошибка при сохранении транзакции:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      id: null,
      personType: 'PHYSICAL',
      operationDate: '',
      transactionType: 'INCOME',
      comment: '',
      amount: '',
      status: 'NEW',
      senderBank: '',
      account: '',
      receiverBank: '',
      receiverInn: '',
      receiverAccount: '',
      category: '',
      receiverPhone: '',
    });
    setIsEditMode(false);
    setErrors({}); // Очищаем ошибки
  };

  return (
    <div className="card shadow-sm mb-4">
      <div className="card-body">
        <h4 className="card-title">{isEditMode ? 'Редактировать транзакцию' : 'Добавить транзакцию'}</h4>
        <form onSubmit={handleSubmit}>
          {/* Блок информации о плательщике */}
          <div className="mb-4">
            <h6 className="text-muted">Информация о плательщике</h6>
            <div className="row g-3">
              {/* Тип лица */}
              <div className="col-md-6">
                <label htmlFor="personType" className="form-label">
                  Тип лица
                </label>
                <select
                  className={`form-select ${errors.personType ? 'is-invalid' : ''}`}
                  id="personType"
                  name="personType"
                  value={formData.personType}
                  onChange={handleChange}
                  required
                >
                  <option value="PHYSICAL">Физическое лицо</option>
                  <option value="LEGAL">Юридическое лицо</option>
                </select>
                {errors.personType && <div className="invalid-feedback">{errors.personType}</div>}
              </div>

              {/* Банк отправителя */}
              <div className="col-md-6">
                <label htmlFor="senderBank" className="form-label">
                  Банк отправителя
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="senderBank"
                  name="senderBank"
                  value={formData.senderBank}
                  onChange={handleChange}
                />
              </div>
            </div>
          </div>

          {/* Блок информации о получателе */}
          <div className="mb-4">
            <h6 className="text-muted">Информация о получателе</h6>
            <div className="row g-3">
              {/* Банк получателя */}
              <div className="col-md-6">
                <label htmlFor="receiverBank" className="form-label">
                  Банк получателя
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="receiverBank"
                  name="receiverBank"
                  value={formData.receiverBank}
                  onChange={handleChange}
                />
              </div>

              {/* ИНН получателя */}
              <div className="col-md-6">
                <label htmlFor="receiverInn" className="form-label">
                  ИНН получателя
                </label>
                <input
                  type="text"
                  className={`form-control ${errors.receiverInn ? 'is-invalid' : ''}`}
                  id="receiverInn"
                  name="receiverInn"
                  value={formData.receiverInn}
                  onChange={handleChange}
                />
                {errors.receiverInn && <div className="invalid-feedback">{errors.receiverInn}</div>}
              </div>

              {/* Расчетный счет получателя */}
              <div className="col-md-6">
                <label htmlFor="receiverAccount" className="form-label">
                  Расчетный счет получателя
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="receiverAccount"
                  name="receiverAccount"
                  value={formData.receiverAccount}
                  onChange={handleChange}
                />
              </div>

              {/* Телефон получателя */}
              <div className="col-md-6">
                <label htmlFor="receiverPhone" className="form-label">
                  Телефон получателя
                </label>
                <input
                  type="text"
                  className={`form-control ${errors.receiverPhone ? 'is-invalid' : ''}`}
                  id="receiverPhone"
                  name="receiverPhone"
                  value={formData.receiverPhone}
                  onChange={handleChange}
                />
                {errors.receiverPhone && <div className="invalid-feedback">{errors.receiverPhone}</div>}
              </div>
            </div>
          </div>

          {/* Блок основной информации */}
          <div className="mb-4">
            <h6 className="text-muted">Основная информация</h6>
            <div className="row g-3">
              {/* Дата и время операции */}
              <div className="col-md-6">
                <label htmlFor="dateTime" className="form-label">
                  Дата и время операции
                </label>
                <input
                  type="datetime-local"
                  className={`form-control ${errors.operationDate ? 'is-invalid' : ''}`}
                  id="operationDate"
                  name="operationDate"
                  value={formData.operationDate}
                  onChange={handleChange}
                  required
                />
                {errors.operationDate && <div className="invalid-feedback">{errors.operationDate}</div>}
              </div>

              {/* Тип транзакции */}
              <div className="col-md-6">
                <label htmlFor="transactionType" className="form-label">
                  Тип транзакции
                </label>
                <select
                  className={`form-select ${errors.transactionType ? 'is-invalid' : ''}`}
                  id="transactionType"
                  name="transactionType"
                  value={formData.transactionType}
                  onChange={handleChange}
                  required
                >
                  <option value="INCOME">Поступление</option>
                  <option value="OUTCOME">Списание</option>
                </select>
                {errors.transactionType && <div className="invalid-feedback">{errors.transactionType}</div>}
              </div>

              {/* Сумма */}
              <div className="col-md-6">
                <label htmlFor="amount" className="form-label">
                  Сумма
                </label>
                <input
                  type="text"
                  className={`form-control ${errors.amount ? 'is-invalid' : ''}`}
                  id="amount"
                  name="amount"
                  value={formData.amount}
                  onChange={handleChange}
                  required
                />
                {errors.amount && <div className="invalid-feedback">{errors.amount}</div>}
              </div>

              {/* Статус операции */}
              <div className="col-md-6">
                <label htmlFor="status" className="form-label">
                  Статус операции
                </label>
                <select
                  className={`form-select ${errors.status ? 'is-invalid' : ''}`}
                  id="status"
                  name="status"
                  value={formData.status}
                  onChange={handleChange}
                  required
                >
                  <option value="NEW">Новая</option>
                  <option value="CONFIRMED">Подтвержденная</option>
                  <option value="IN_PROGRESS">В обработке</option>
                  <option value="CANCELED">Отменена</option>
                  <option value="COMPLETED">Платеж выполнен</option>
                  <option value="DELETED">Платеж удален</option>
                  <option value="REFUND">Возврат</option>
                </select>
                {errors.status && <div className="invalid-feedback">{errors.status}</div>}
              </div>
            </div>
          </div>

              <div className="mb-3">
                <label htmlFor="comment" className="form-label">
                  Комментарий к операции
                </label>
                <textarea
                  className="form-control"
                  id="comment"
                  name="comment"
                  value={formData.comment}
                  onChange={handleChange}
                ></textarea>
              </div>

              {/* Категория */}
              <div className="mb-3">
                <label htmlFor="category" className="form-label">
                  Категория
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="category"
                  name="category"
                  value={formData.category}
                  onChange={handleChange}
                />
              </div>

              {/* Счет поступления/списания */}
              <div className="mb-3">
                <label htmlFor="account" className="form-label">
                  Счет поступления/списания
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="account"
                  name="account"
                  value={formData.account}
                  onChange={handleChange}
                />
              </div>

          {/* Кнопка отправки */}
          <button type="submit" className="btn btn-success w-100" disabled={isLoading}>
            {isLoading ? 'Сохранение...' : isEditMode ? 'Обновить' : 'Добавить'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default TransactionForm;
