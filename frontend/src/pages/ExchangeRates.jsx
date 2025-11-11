import { useState, useEffect } from 'react';
import api from '../services/api';

export default function ExchangeRates() {
  const [rates, setRates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    date: new Date().toISOString().split('T')[0],
    rateType: 'OFICIAL',
    buyRate: '',
    sellRate: ''
  });

  const rateTypes = [
    { value: 'OFICIAL', label: 'Dólar Oficial' },
    { value: 'BLUE', label: 'Dólar Blue' },
    { value: 'MEP', label: 'Dólar MEP' },
    { value: 'TARJETA', label: 'Dólar Tarjeta' }
  ];

  useEffect(() => {
    fetchRates();
  }, []);

  const fetchRates = async () => {
    try {
      const response = await api.get('/exchange-rates');
      setRates(response.data);
    } catch (error) {
      console.error('Error al cargar tipos de cambio:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/exchange-rates', formData);
      fetchRates();
      resetForm();
    } catch (error) {
      console.error('Error al guardar tipo de cambio:', error);
      alert('Error al guardar el tipo de cambio');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Eliminar este tipo de cambio?')) return;
    
    try {
      await api.delete(`/exchange-rates/${id}`);
      fetchRates();
    } catch (error) {
      console.error('Error al eliminar:', error);
      alert('Error al eliminar el tipo de cambio');
    }
  };

  const resetForm = () => {
    setFormData({
      date: new Date().toISOString().split('T')[0],
      rateType: 'OFICIAL',
      buyRate: '',
      sellRate: ''
    });
    setShowForm(false);
  };

  const groupByDate = () => {
    const grouped = {};
    rates.forEach(rate => {
      if (!grouped[rate.date]) {
        grouped[rate.date] = [];
      }
      grouped[rate.date].push(rate);
    });
    return grouped;
  };

  const getRateColor = (type) => {
    const colors = {
      OFICIAL: 'bg-blue-100 text-blue-800',
      BLUE: 'bg-green-100 text-green-800',
      MEP: 'bg-purple-100 text-purple-800',
      TARJETA: 'bg-orange-100 text-orange-800'
    };
    return colors[type] || 'bg-gray-100 text-gray-800';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  const groupedRates = groupByDate();

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Tipos de Cambio</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          {showForm ? 'Cancelar' : '+ Nuevo Tipo de Cambio'}
        </button>
      </div>

      {showForm && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold mb-4">Agregar Tipo de Cambio</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Fecha *
                </label>
                <input
                  type="date"
                  value={formData.date}
                  onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Tipo *
                </label>
                <select
                  value={formData.rateType}
                  onChange={(e) => setFormData({ ...formData, rateType: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  required
                >
                  {rateTypes.map(type => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Compra *
                </label>
                <input
                  type="number"
                  step="0.0001"
                  value={formData.buyRate}
                  onChange={(e) => setFormData({ ...formData, buyRate: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  placeholder="1000.00"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Venta *
                </label>
                <input
                  type="number"
                  step="0.0001"
                  value={formData.sellRate}
                  onChange={(e) => setFormData({ ...formData, sellRate: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  placeholder="1050.00"
                  required
                />
              </div>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={resetForm}
                className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
              >
                Guardar
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="space-y-6">
        {Object.keys(groupedRates).length === 0 ? (
          <div className="text-center py-12 bg-white rounded-lg shadow">
            <p className="text-gray-500 text-lg">No hay tipos de cambio registrados</p>
            <button
              onClick={() => setShowForm(true)}
              className="mt-4 text-blue-600 hover:text-blue-700"
            >
              Agregar el primero
            </button>
          </div>
        ) : (
          Object.keys(groupedRates)
            .sort((a, b) => new Date(b) - new Date(a))
            .map(date => (
              <div key={date} className="bg-white rounded-lg shadow-md p-6">
                <h3 className="text-lg font-semibold mb-4">
                  {new Date(date + 'T00:00:00').toLocaleDateString('es-AR', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                  })}
                </h3>
                
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                  {groupedRates[date].map(rate => (
                    <div
                      key={rate.id}
                      className="border border-gray-200 rounded-lg p-4 hover:shadow-lg transition"
                    >
                      <div className="flex justify-between items-start mb-3">
                        <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getRateColor(rate.rateType)}`}>
                          {rateTypes.find(t => t.value === rate.rateType)?.label}
                        </span>
                        <button
                          onClick={() => handleDelete(rate.id)}
                          className="text-red-500 hover:text-red-700"
                          title="Eliminar"
                        >
                          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </div>

                      <div className="space-y-2">
                        <div className="flex justify-between">
                          <span className="text-gray-600 text-sm">Compra:</span>
                          <span className="font-semibold text-green-600">
                            ${rate.buyRate.toFixed(2)}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600 text-sm">Venta:</span>
                          <span className="font-semibold text-red-600">
                            ${rate.sellRate.toFixed(2)}
                          </span>
                        </div>
                        <div className="flex justify-between border-t pt-2">
                          <span className="text-gray-600 text-sm">Promedio:</span>
                          <span className="font-bold text-blue-600">
                            ${rate.averageRate.toFixed(2)}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))
        )}
      </div>
    </div>
  );
}
