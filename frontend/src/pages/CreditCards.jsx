import { useState, useEffect } from 'react';
import api from '../services/api';

export default function CreditCards() {
  const [creditCards, setCreditCards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingCard, setEditingCard] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    lastFourDigits: '',
    closingDay: '',
    dueDay: '',
    creditLimit: '',
    currency: 'ARS',
    bank: '',
    active: true
  });

  useEffect(() => {
    fetchCreditCards();
  }, []);

  const fetchCreditCards = async () => {
    try {
      const response = await api.get('/credit-cards');
      setCreditCards(response.data);
    } catch (error) {
      console.error('Error al cargar tarjetas:', error);
      alert('Error al cargar las tarjetas de crédito');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCard) {
        await api.put(`/credit-cards/${editingCard.id}`, formData);
      } else {
        await api.post('/credit-cards', formData);
      }
      fetchCreditCards();
      resetForm();
    } catch (error) {
      console.error('Error al guardar tarjeta:', error);
      alert('Error al guardar la tarjeta');
    }
  };

  const handleEdit = (card) => {
    setEditingCard(card);
    setFormData({
      name: card.name,
      lastFourDigits: card.lastFourDigits || '',
      closingDay: card.closingDay,
      dueDay: card.dueDay,
      creditLimit: card.creditLimit || '',
      currency: card.currency,
      bank: card.bank || '',
      active: card.active
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar esta tarjeta?')) return;
    
    try {
      await api.delete(`/credit-cards/${id}`);
      fetchCreditCards();
    } catch (error) {
      console.error('Error al eliminar tarjeta:', error);
      alert('Error al eliminar la tarjeta');
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      lastFourDigits: '',
      closingDay: '',
      dueDay: '',
      creditLimit: '',
      currency: 'ARS',
      bank: '',
      active: true
    });
    setEditingCard(null);
    setShowForm(false);
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS'
    }).format(amount || 0);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Tarjetas de Crédito</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          {showForm ? 'Cancelar' : '+ Nueva Tarjeta'}
        </button>
      </div>

      {showForm && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold mb-4">
            {editingCard ? 'Editar Tarjeta' : 'Nueva Tarjeta'}
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nombre de la Tarjeta *
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Últimos 4 Dígitos
                </label>
                <input
                  type="text"
                  maxLength="4"
                  value={formData.lastFourDigits}
                  onChange={(e) => setFormData({ ...formData, lastFourDigits: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  placeholder="1234"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Banco
                </label>
                <input
                  type="text"
                  value={formData.bank}
                  onChange={(e) => setFormData({ ...formData, bank: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Moneda
                </label>
                <select
                  value={formData.currency}
                  onChange={(e) => setFormData({ ...formData, currency: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                >
                  <option value="ARS">ARS - Pesos Argentinos</option>
                  <option value="USD">USD - Dólares</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Día de Cierre *
                </label>
                <input
                  type="number"
                  min="1"
                  max="31"
                  value={formData.closingDay}
                  onChange={(e) => setFormData({ ...formData, closingDay: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Día de Vencimiento *
                </label>
                <input
                  type="number"
                  min="1"
                  max="31"
                  value={formData.dueDay}
                  onChange={(e) => setFormData({ ...formData, dueDay: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Límite de Crédito
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.creditLimit}
                  onChange={(e) => setFormData({ ...formData, creditLimit: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                />
              </div>

              <div className="flex items-center">
                <label className="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={formData.active}
                    onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                    className="mr-2"
                  />
                  <span className="text-sm font-medium text-gray-700">Activa</span>
                </label>
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
                {editingCard ? 'Actualizar' : 'Crear'}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {creditCards.map((card) => (
          <div
            key={card.id}
            className={`bg-gradient-to-br from-blue-500 to-blue-700 rounded-lg shadow-lg p-6 text-white ${
              !card.active ? 'opacity-50' : ''
            }`}
          >
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-xl font-bold">{card.name}</h3>
                {card.bank && <p className="text-sm opacity-90">{card.bank}</p>}
              </div>
              {!card.active && (
                <span className="bg-red-500 text-xs px-2 py-1 rounded">Inactiva</span>
              )}
            </div>

            {card.lastFourDigits && (
              <div className="mb-4">
                <p className="text-2xl tracking-widest">•••• {card.lastFourDigits}</p>
              </div>
            )}

            <div className="grid grid-cols-2 gap-4 mb-4 text-sm">
              <div>
                <p className="opacity-75">Cierre</p>
                <p className="font-semibold">Día {card.closingDay}</p>
              </div>
              <div>
                <p className="opacity-75">Vencimiento</p>
                <p className="font-semibold">Día {card.dueDay}</p>
              </div>
            </div>

            {card.creditLimit && (
              <div className="border-t border-white/20 pt-4 mb-4">
                <div className="flex justify-between text-sm mb-1">
                  <span>Límite:</span>
                  <span className="font-semibold">{formatCurrency(card.creditLimit)}</span>
                </div>
                <div className="flex justify-between text-sm mb-1">
                  <span>Usado:</span>
                  <span className="font-semibold">{formatCurrency(card.currentDebt)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>Disponible:</span>
                  <span className="font-semibold text-green-300">
                    {formatCurrency(card.availableCredit)}
                  </span>
                </div>
              </div>
            )}

            <div className="flex space-x-2">
              <button
                onClick={() => handleEdit(card)}
                className="flex-1 bg-white/20 hover:bg-white/30 py-2 rounded transition"
              >
                Editar
              </button>
              <button
                onClick={() => handleDelete(card.id)}
                className="flex-1 bg-red-500/50 hover:bg-red-500/70 py-2 rounded transition"
              >
                Eliminar
              </button>
            </div>
          </div>
        ))}
      </div>

      {creditCards.length === 0 && !showForm && (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No hay tarjetas de crédito registradas</p>
          <button
            onClick={() => setShowForm(true)}
            className="mt-4 text-blue-600 hover:text-blue-700"
          >
            Crear la primera tarjeta
          </button>
        </div>
      )}
    </div>
  );
}
