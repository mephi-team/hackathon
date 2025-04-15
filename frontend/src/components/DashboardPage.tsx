// components/DashboardPage.tsx

import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import {Transaction} from "../interfaces/Transaction";

interface DashboardPageProps {
  transactions: Transaction[];
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

const DashboardPage: React.FC<DashboardPageProps> = ({ transactions }) => {
  // Группировка данных по неделям/месяцам/кварталам/годам
  const groupByTimePeriod = (period: 'week' | 'month' | 'quarter' | 'year') => {
    const grouped: Record<string, number> = {};
    transactions.forEach((t) => {
      const date = new Date(t.dateTime);
      let key;
      switch (period) {
        case 'week':
          key = `${date.getFullYear()}-W${getWeekNumber(date)}`;
          break;
        case 'month':
          key = `${date.getFullYear()}-${date.getMonth() + 1}`;
          break;
        case 'quarter':
          key = `${date.getFullYear()}-Q${Math.ceil((date.getMonth() + 1) / 3)}`;
          break;
        case 'year':
          key = `${date.getFullYear()}`;
          break;
      }
      if (!grouped[key]) grouped[key] = 0;
      grouped[key]++;
    });
    return Object.entries(grouped).map(([name, count]) => ({ name, count }));
  };

  // Получение номера недели
  const getWeekNumber = (date: Date): number => {
    const startOfYear = new Date(date.getFullYear(), 0, 1);
    const pastDaysOfYear = (date.getTime() - startOfDay(startOfYear).getTime()) / 86400000;
    return Math.ceil((pastDaysOfYear + startOfYear.getDay() + 1) / 7);
  };

  const startOfDay = (date: Date): Date => {
    return new Date(date.getFullYear(), date.getMonth(), date.getDate());
  };

  // Статистика по типу операции
  const getTypeStats = () => {
    const stats: Record<string, number> = { income: 0, expense: 0 };
    transactions.forEach((t) => {
      stats[t.transactionType]++;
    });
    return Object.entries(stats).map(([name, count]) => ({ name, count }));
  };

  // Статистика по банкам отправителей и получателей
  const getBankStats = () => {
    const senderStats: Record<string, number> = {};
    const receiverStats: Record<string, number> = {};
    transactions.forEach((t) => {
      senderStats[t.senderBank] = (senderStats[t.senderBank] || 0) + 1;
      receiverStats[t.receiverBank] = (receiverStats[t.receiverBank] || 0) + 1;
    });
    return {
      sender: Object.entries(senderStats).map(([name, count]) => ({ name, count })),
      receiver: Object.entries(receiverStats).map(([name, count]) => ({ name, count })),
    };
  };

  // Статистика по категориям
  const getCategoryStats = () => {
    const stats: Record<string, number> = {};
    transactions.forEach((t) => {
      stats[t.category] = (stats[t.category] || 0) + 1;
    });
    return Object.entries(stats).map(([name, count]) => ({ name, count }));
  };

  return (
    <div className="container">
      <h3 className="mb-4">Дашборды</h3>

      {/* Grid Layout */}
      <div className="dashboard-grid">
        {/* Динамика по количеству транзакций */}
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">Динамика транзакций (по месяцам)</h5>
            <BarChart width={400} height={300} data={groupByTimePeriod('month')}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" fill="#8884d8" />
            </BarChart>
          </div>
        </div>

        {/* Динамика по типу транзакции */}
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">Динамика по типу транзакции</h5>
            <PieChart width={400} height={300}>
              <Pie
                data={getTypeStats()}
                cx={150}
                cy={150}
                labelLine={false}
                outerRadius={80}
                fill="#8884d8"
                dataKey="count"
              >
                {getTypeStats().map((_, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </div>
        </div>

        {/* Сравнение поступлений и списаний */}
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">Сравнение поступлений и списаний</h5>
            <BarChart width={300} height={200} data={getTypeStats()}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" fill="#82ca9d" />
            </BarChart>
          </div>
        </div>

        {/* Статистика по банкам */}
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">Статистика по банкам</h5>
            <div className="row">
              <div className="col-md-6">
                <h6>Банки отправителей</h6>
                <BarChart width={150} height={150} data={getBankStats().sender}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="count" fill="#8884d8" />
                </BarChart>
              </div>
              <div className="col-md-6">
                <h6>Банки получателей</h6>
                <BarChart width={150} height={150} data={getBankStats().receiver}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="count" fill="#82ca9d" />
                </BarChart>
              </div>
            </div>
          </div>
        </div>

        {/* Статистика по категориям */}
        <div className="card shadow-sm">
          <div className="card-body">
            <h5 className="card-title">Статистика по категориям</h5>
            <BarChart width={300} height={200} data={getCategoryStats()}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" fill="#8884d8" />
            </BarChart>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
