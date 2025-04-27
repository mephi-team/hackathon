// components/TransactionFilter.tsx

import React, { useState } from 'react';
import {FilterData} from "../interfaces/FilterData";
import {fetchExcel, fetchPdf} from "./fetchApi";

interface TransactionFilterProps {
  onFilter: (filters: FilterData) => void;
  onReset: () => void;
}

const TransactionFilter: React.FC<TransactionFilterProps> = ({ onFilter, onReset }) => {
  const [filters, setFilters] = useState<FilterData>({});
  const [showMore, setShowMore] = useState(false); // Флаг для расширенных фильтров

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (dates: [string, string]) => {
    setFilters((prev) => ({ ...prev, date: dates }));
  };

  const handleAmountRangeChange = (range: [number, number]) => {
    setFilters((prev) => ({ ...prev, amountRange: range }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onFilter(filters);
  };

  // Сброс фильтров
  const handleReset = () => {
    setFilters({}); // Очищаем состояние фильтров
    onReset(); // Вызываем коллбэк для сброса данных в родительском компоненте
  };

  const download = async (fetch: () => Promise<Blob>, ext: string) => {
    try {
      const blob = await fetch();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `transaction-report.${ext}`); // Имя файла
      document.body.appendChild(link);
      link.click();

      // Очищаем ссылку
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Ошибка:', error);
      alert('Не удалось скачать файл.');
    }
  }
  const handleDownloadPdf = async () => {
    await download(fetchPdf, "pdf");
  }

  const handleDownloadExcel = async () => {
    await download(fetchExcel, "xlsx");
  }

  return (
    <div className="card shadow-sm mb-4">
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          {/* Основные фильтры */}
          <div className="row g-3 align-items-center">
            {/* Дата */}
            <div className="col-auto">
              <label htmlFor="startDate" className="form-label visually-hidden">
                Начальная дата
              </label>
              <div className="input-group">
                <span className="input-group-text">Дата</span>
                <input
                  type="date"
                  className="form-control"
                  id="startDate"
                  placeholder="Начальная дата"
                  value={filters.date?.[0] || ''}
                  onChange={(e) =>
                    handleDateChange([e.target.value, filters.date?.[1] || ''])
                  }
                />
                <input
                  type="date"
                  className="form-control"
                  id="endDate"
                  placeholder="Конечная дата"
                  value={filters.date?.[1] || ''}
                  onChange={(e) =>
                    handleDateChange([filters.date?.[0] || '', e.target.value])
                  }
                />
              </div>
            </div>

            {/* Статус */}
            <div className="col-auto">
              <label htmlFor="status" className="form-label visually-hidden">
                Статус
              </label>
              <div className="input-group">
                <span className="input-group-text">Статус</span>
                <select
                  className="form-select"
                  id="status"
                  name="status"
                  value={filters.status || ''}
                  onChange={handleChange}
                >
                  <option value="">Все</option>
                  <option value="Новая">Новая</option>
                  <option value="Подтвержденная">Подтвержденная</option>
                  <option value="Отменена">Отменена</option>
                </select>
              </div>
            </div>

            {/* Тип операции */}
            <div className="col-auto">
              <label htmlFor="transactionType" className="form-label visually-hidden">
                Тип операции
              </label>
              <div className="input-group">
                <span className="input-group-text">Тип</span>
                <select
                  className="form-select"
                  id="transactionType"
                  name="transactionType"
                  value={filters.transactionType || ''}
                  onChange={handleChange}
                >
                  <option value="">Все</option>
                  <option value="Поступление">Поступление</option>
                  <option value="Списание">Списание</option>
                </select>
              </div>
            </div>

            {/* Кнопка "Показать больше" */}
            <div className="col-auto ms-auto">
              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => setShowMore(!showMore)}
              >
                {showMore ? 'Скрыть' : 'Показать больше'}
              </button>
            </div>
          </div>

          {/* Расширенные фильтры */}
          {showMore && (
            <div className="mt-3">
              {/* Банк отправителя и Банк получателя */}
              <div className="row g-3">
                <div className="col-md-6">
                  <label htmlFor="senderBank" className="visually-hidden">
                    Банк отправителя
                  </label>
                  <div className="input-group">
                    <span className="input-group-text">Банк отправителя</span>
                    <input
                      type="text"
                      className="form-control"
                      id="senderBank"
                      name="senderBank"
                      value={filters.senderBank || ''}
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="col-md-6">
                  <label htmlFor="receiverBank" className="visually-hidden">
                    Банк получателя
                  </label>
                  <div className="input-group">
                    <span className="input-group-text">Банк получателя</span>
                    <input
                      type="text"
                      className="form-control"
                      id="receiverBank"
                      name="receiverBank"
                      value={filters.receiverBank || ''}
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>

              {/* ИНН, Сумма и Категория */}
              <div className="row g-3 mt-3">
                <div className="col-md-4">
                  <label htmlFor="inn" className="visually-hidden">
                    ИНН
                  </label>
                  <div className="input-group">
                    <span className="input-group-text">ИНН</span>
                    <input
                      type="text"
                      className="form-control"
                      id="inn"
                      name="inn"
                      value={filters.inn || ''}
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="col-md-4">
                  <label htmlFor="amountRange" className="visually-hidden">
                    Сумма (диапазон)
                  </label>
                  <div className="input-group">
                    <span className="input-group-text">Сумма</span>
                    <input
                      type="number"
                      className="form-control"
                      placeholder="Минимум"
                      value={filters.amountRange?.[0] || ''}
                      onChange={(e) =>
                        handleAmountRangeChange([
                          parseFloat(e.target.value),
                          filters.amountRange?.[1] || 0,
                        ])
                      }
                    />
                    <input
                      type="number"
                      className="form-control"
                      placeholder="Максимум"
                      value={filters.amountRange?.[1] || ''}
                      onChange={(e) =>
                        handleAmountRangeChange([
                          filters.amountRange?.[0] || 0,
                          parseFloat(e.target.value),
                        ])
                      }
                    />
                  </div>
                </div>
                <div className="col-md-4">
                  <label htmlFor="category" className="visually-hidden">
                    Категория
                  </label>
                  <div className="input-group">
                    <span className="input-group-text">Категория</span>
                    <input
                      type="text"
                      className="form-control"
                      id="category"
                      name="category"
                      value={filters.category || ''}
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Кнопки */}
          <div className="d-flex gap-2 mt-3">
            <button type="submit" className="btn btn-primary">
              Применить фильтр
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={handleReset} // Добавляем обработчик для сброса
            >
              Сбросить фильтр
            </button>
            <button
              type="button"
              className="btn btn-outline-primary"
              onClick={handleDownloadPdf} // Добавляем обработчик для сброса
            >
              Скачать PDF
            </button>
            <button
              type="button"
              className="btn btn-outline-secondary"
              onClick={handleDownloadExcel} // Добавляем обработчик для сброса
            >
              Скачать Excel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TransactionFilter;
