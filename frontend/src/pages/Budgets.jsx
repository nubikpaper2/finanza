import { useState, useEffect } from 'react';
import api from '../services/api';

export default function Budgets() {
  const [budgets, setBudgets] = useState([]);
  const [categories, setCategories] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [selectedDate, setSelectedDate] = useState(() => {
    const now = new Date();
    return { year: now.getFullYear(), month: now.getMonth() + 1 };
  });

  const [formData, setFormData] = useState({
    categoryId: '',
    amount: '',
    year: selectedDate.year,
    month: selectedDate.month
  });

  useEffect(() => {
    loadCategories();
  }, []);

  useEffect(() => {
    loadBudgets();
  }, [selectedDate]);

  const loadCategories = async () => {
    try {
      const response = await api.get('/categories');
      setCategories(response.data.filter(c => c.type === 'EXPENSE'));
    } catch (error) {
      console.error('Error cargando categorías:', error);
    }
  };

  const loadBudgets = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/budgets/month/${selectedDate.year}/${selectedDate.month}`);
      setBudgets(response.data);
    } catch (error) {
      console.error('Error cargando presupuestos:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.post('/budgets', {
        ...formData,
        amount: parseFloat(formData.amount)
      });
      setShowForm(false);
      setFormData({
        categoryId: '',
        amount: '',
        year: selectedDate.year,
        month: selectedDate.month
      });
      loadBudgets();
    } catch (error) {
      alert('Error creando presupuesto: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Está seguro de eliminar este presupuesto?')) return;
    
    try {
      await api.delete(`/budgets/${id}`);
      loadBudgets();
    } catch (error) {
      alert('Error eliminando presupuesto: ' + (error.response?.data?.message || error.message));
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

  const getProgressColor = (percentage) => {
    if (percentage >= 100) return 'bg-red-600';
    if (percentage >= 80) return 'bg-yellow-500';
    return 'bg-green-600';
  };

  const getAlertClass = (percentage) => {
    if (percentage >= 100) return 'border-red-500 bg-red-50';
    if (percentage >= 80) return 'border-yellow-500 bg-yellow-50';
    return 'border-gray-200 bg-white';
  };

  const monthNames = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 
                      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Presupuestos</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
        >
          {showForm ? 'Cancelar' : '+ Nuevo Presupuesto'}
        </button>
      </div>

      {/* Selector de mes */}
      <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
        <div className="flex items-center justify-center gap-4">
          <button
            onClick={() => changeMonth(-1)}
            className="text-gray-600 hover:text-gray-900"
          >
            ← Anterior
          </button>
          <h2 className="text-xl font-semibold">
            {monthNames[selectedDate.month - 1]} {selectedDate.year}
          </h2>
          <button
            onClick={() => changeMonth(1)}
            className="text-gray-600 hover:text-gray-900"
          >
            Siguiente →
          </button>
        </div>
      </div>

      {/* Formulario */}
      {showForm && (
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <h2 className="text-xl font-semibold mb-4">Crear Presupuesto</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Categoría
              </label>
              <select
                value={formData.categoryId}
                onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Seleccione una categoría</option>
                {categories.map(cat => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Monto Presupuestado
              </label>
              <input
                type="number"
                step="0.01"
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                placeholder="0.00"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg disabled:opacity-50"
            >
              {loading ? 'Guardando...' : 'Crear Presupuesto'}
            </button>
          </form>
        </div>
      )}

      {/* Lista de presupuestos */}
      {loading && !showForm ? (
        <div className="text-center py-8">Cargando presupuestos...</div>
      ) : budgets.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          No hay presupuestos para este mes
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {budgets.map((budget) => (
            <div
              key={budget.id}
              className={`border-2 rounded-lg p-6 ${getAlertClass(budget.percentage)}`}
            >
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="font-semibold text-lg">{budget.categoryName}</h3>
                  <p className="text-sm text-gray-600">
                    {monthNames[budget.month - 1]} {budget.year}
                  </p>
                </div>
                <button
                  onClick={() => handleDelete(budget.id)}
                  className="text-red-600 hover:text-red-800"
                >
                  🗑️
                </button>
              </div>

              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Presupuestado:</span>
                  <span className="font-semibold">${budget.amount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Gastado:</span>
                  <span className="font-semibold">${budget.spent.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Restante:</span>
                  <span className={`font-semibold ${budget.remaining < 0 ? 'text-red-600' : 'text-green-600'}`}>
                    ${budget.remaining.toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Barra de progreso */}
              <div className="mb-2">
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-gray-600">Progreso</span>
                  <span className="font-semibold">{budget.percentage.toFixed(1)}%</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div
                    className={`h-2.5 rounded-full ${getProgressColor(budget.percentage)}`}
                    style={{ width: `${Math.min(budget.percentage, 100)}%` }}
                  ></div>
                </div>
              </div>

              {/* Alertas */}
              {budget.percentage >= 100 && (
                <div className="mt-3 text-sm text-red-700 font-medium">
                  ⚠️ Presupuesto excedido
                </div>
              )}
              {budget.percentage >= 80 && budget.percentage < 100 && (
                <div className="mt-3 text-sm text-yellow-700 font-medium">
                  ⚠️ Cerca del límite
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
