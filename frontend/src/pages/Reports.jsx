import { useState, useEffect } from 'react';
import api from '../services/api';
import Navbar from '../components/Navbar';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';

export default function Reports() {
  const [report, setReport] = useState(null);
  const [yearlyData, setYearlyData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(() => {
    const now = new Date();
    return { year: now.getFullYear(), month: now.getMonth() + 1 };
  });
  const [viewType, setViewType] = useState('monthly'); // 'monthly' o 'yearly'

  useEffect(() => {
    if (viewType === 'monthly') {
      loadMonthlyReport();
    } else {
      loadYearlyReport();
    }
  }, [selectedDate, viewType]);

  const loadMonthlyReport = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/reports/monthly/${selectedDate.year}/${selectedDate.month}`);
      setReport(response.data);
    } catch (error) {
      console.error('Error cargando reporte:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadYearlyReport = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/reports/yearly/${selectedDate.year}`);
      setYearlyData(response.data);
    } catch (error) {
      console.error('Error cargando reporte anual:', error);
    } finally {
      setLoading(false);
    }
  };

  const changeMonth = (delta) => {
    let newMonth = selectedDate.month + delta;
    let newYear = selectedDate.year;
    
    if (newMonth > 12) {
      newMonth = 1;
      newYear++;
    } else if (newMonth < 1) {
      newMonth = 12;
      newYear--;
    }
    
    setSelectedDate({ year: newYear, month: newMonth });
  };

  const changeYear = (delta) => {
    setSelectedDate({ ...selectedDate, year: selectedDate.year + delta });
  };

  const monthNames = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 
                      'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
  const monthFullNames = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 
                          'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];

  const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899', '#14B8A6', '#F97316'];

  // Preparar datos para gráfico anual
  const yearlyChartData = yearlyData.map((monthData, index) => ({
    month: monthNames[index],
    ingresos: monthData.totalIncome,
    gastos: monthData.totalExpenses,
    balance: monthData.balance
  }));

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-8">Cargando reportes...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Reportes y Análisis</h1>
          <div className="flex gap-2">
            <button
              onClick={() => setViewType('monthly')}
              className={`px-4 py-2 rounded-lg shadow-sm transition-colors ${
                viewType === 'monthly' 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Mensual
            </button>
            <button
              onClick={() => setViewType('yearly')}
              className={`px-4 py-2 rounded-lg shadow-sm transition-colors ${
                viewType === 'yearly' 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Anual
            </button>
          </div>
        </div>

      {/* Selector de periodo */}
      <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
        <div className="flex items-center justify-center gap-4">
          <button
            onClick={() => viewType === 'monthly' ? changeMonth(-1) : changeYear(-1)}
            className="text-gray-600 hover:text-gray-900"
          >
            ← Anterior
          </button>
          <h2 className="text-xl font-semibold">
            {viewType === 'monthly' 
              ? `${monthFullNames[selectedDate.month - 1]} ${selectedDate.year}`
              : selectedDate.year
            }
          </h2>
          <button
            onClick={() => viewType === 'monthly' ? changeMonth(1) : changeYear(1)}
            className="text-gray-600 hover:text-gray-900"
          >
            Siguiente →
          </button>
        </div>
      </div>

      {viewType === 'monthly' && report && (
        <>
          {/* Resumen mensual */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <div className="bg-gradient-to-br from-green-500 to-green-600 rounded-lg shadow-lg p-6 text-white">
              <h3 className="text-lg font-semibold mb-2">Total Ingresos</h3>
              <p className="text-3xl font-bold">${report.totalIncome.toFixed(2)}</p>
            </div>
            <div className="bg-gradient-to-br from-red-500 to-red-600 rounded-lg shadow-lg p-6 text-white">
              <h3 className="text-lg font-semibold mb-2">Total Gastos</h3>
              <p className="text-3xl font-bold">${report.totalExpenses.toFixed(2)}</p>
            </div>
            <div className={`bg-gradient-to-br ${
              report.balance >= 0 ? 'from-blue-500 to-blue-600' : 'from-orange-500 to-orange-600'
            } rounded-lg shadow-lg p-6 text-white`}>
              <h3 className="text-lg font-semibold mb-2">Balance</h3>
              <p className="text-3xl font-bold">${report.balance.toFixed(2)}</p>
            </div>
          </div>

          {/* Gráficos */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
            {/* Gráfico de barras - Top categorías gastos */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h3 className="text-lg font-semibold mb-4">Top Categorías de Gastos</h3>
              {report.topExpenseCategories.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={report.topExpenseCategories.slice(0, 5)}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="categoryName" />
                    <YAxis />
                    <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                    <Bar dataKey="amount" fill="#EF4444" />
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <p className="text-center text-gray-500 py-8">No hay gastos en este periodo</p>
              )}
            </div>

            {/* Gráfico de pastel - Distribución gastos */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h3 className="text-lg font-semibold mb-4">Distribución de Gastos</h3>
              {report.topExpenseCategories.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={report.topExpenseCategories.slice(0, 5)}
                      dataKey="amount"
                      nameKey="categoryName"
                      cx="50%"
                      cy="50%"
                      outerRadius={100}
                      label={(entry) => `${entry.percentage.toFixed(1)}%`}
                    >
                      {report.topExpenseCategories.slice(0, 5).map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <p className="text-center text-gray-500 py-8">No hay datos para mostrar</p>
              )}
            </div>
          </div>

          {/* Tabla detallada de gastos */}
          <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
            <h3 className="text-lg font-semibold mb-4">Detalle de Gastos por Categoría</h3>
            {report.topExpenseCategories.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="min-w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Categoría
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        Cantidad
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        Monto
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        % del Total
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {report.topExpenseCategories.map((cat) => (
                      <tr key={cat.categoryId}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {cat.categoryName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">
                          {cat.transactionCount}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">
                          ${cat.amount.toFixed(2)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">
                          {cat.percentage.toFixed(1)}%
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="text-center text-gray-500 py-4">No hay gastos registrados</p>
            )}
          </div>

          {/* Tabla de ingresos */}
          {report.incomeByCategory.length > 0 && (
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h3 className="text-lg font-semibold mb-4">Detalle de Ingresos por Categoría</h3>
              <div className="overflow-x-auto">
                <table className="min-w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Categoría
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        Cantidad
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        Monto
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                        % del Total
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {report.incomeByCategory.map((cat) => (
                      <tr key={cat.categoryId}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {cat.categoryName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">
                          {cat.transactionCount}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">
                          ${cat.amount.toFixed(2)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">
                          {cat.percentage.toFixed(1)}%
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </>
      )}

      {viewType === 'yearly' && yearlyData.length > 0 && (
        <>
          {/* Gráfico de líneas - Evolución anual */}
          <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
            <h3 className="text-lg font-semibold mb-4">Evolución Anual - Ingresos vs Gastos</h3>
            <ResponsiveContainer width="100%" height={400}>
              <LineChart data={yearlyChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                <Legend />
                <Line type="monotone" dataKey="ingresos" stroke="#10B981" strokeWidth={2} name="Ingresos" />
                <Line type="monotone" dataKey="gastos" stroke="#EF4444" strokeWidth={2} name="Gastos" />
                <Line type="monotone" dataKey="balance" stroke="#3B82F6" strokeWidth={2} name="Balance" />
              </LineChart>
            </ResponsiveContainer>
          </div>

          {/* Gráfico de barras - Comparativa mensual */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <h3 className="text-lg font-semibold mb-4">Comparativa Mensual</h3>
            <ResponsiveContainer width="100%" height={400}>
              <BarChart data={yearlyChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                <Legend />
                <Bar dataKey="ingresos" fill="#10B981" name="Ingresos" />
                <Bar dataKey="gastos" fill="#EF4444" name="Gastos" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </>
      )}
      </div>
    </div>
  );
}
