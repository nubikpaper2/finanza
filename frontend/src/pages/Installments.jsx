import { useState, useEffect } from 'react';
import api from '../services/api';

export default function Installments() {
  const [installments, setInstallments] = useState([]);
  const [creditCards, setCreditCards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('unpaid'); // unpaid, upcoming, all
  const [selectedCard, setSelectedCard] = useState('all');

  useEffect(() => {
    fetchCreditCards();
    fetchInstallments();
  }, [filter, selectedCard]);

  const fetchCreditCards = async () => {
    try {
      const response = await api.get('/credit-cards');
      setCreditCards(response.data);
    } catch (error) {
      console.error('Error al cargar tarjetas:', error);
    }
  };

  const fetchInstallments = async () => {
    setLoading(true);
    try {
      let response;
      if (selectedCard !== 'all') {
        response = await api.get(`/installments/credit-card/${selectedCard}`);
      } else if (filter === 'unpaid') {
        response = await api.get('/installments/unpaid');
      } else if (filter === 'upcoming') {
        const today = new Date();
        const endDate = new Date();
        endDate.setMonth(endDate.getMonth() + 3);
        
        response = await api.get('/installments/upcoming', {
          params: {
            startDate: today.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0]
          }
        });
      }
      
      setInstallments(response?.data || []);
    } catch (error) {
      console.error('Error al cargar cuotas:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleTogglePaid = async (installmentId, currentlyPaid) => {
    try {
      if (currentlyPaid) {
        await api.patch(`/installments/${installmentId}/unpay`);
      } else {
        await api.patch(`/installments/${installmentId}/pay`);
      }
      fetchInstallments();
    } catch (error) {
      console.error('Error al actualizar cuota:', error);
      alert('Error al actualizar el estado de la cuota');
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS'
    }).format(amount || 0);
  };

  const formatDate = (dateString) => {
    return new Date(dateString + 'T00:00:00').toLocaleDateString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const isOverdue = (dueDate, paid) => {
    if (paid) return false;
    return new Date(dueDate) < new Date();
  };

  const getTotalAmount = () => {
    return installments.reduce((sum, inst) => {
      if (!inst.paid) {
        return sum + (inst.amount || 0);
      }
      return sum;
    }, 0);
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
        <h1 className="text-3xl font-bold text-gray-900">Cuotas de Tarjetas</h1>
        
        <div className="flex items-center space-x-4">
          <div className="bg-blue-100 text-blue-800 px-4 py-2 rounded-lg font-semibold">
            Total a Pagar: {formatCurrency(getTotalAmount())}
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-md p-4">
        <div className="flex flex-wrap gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Filtro
            </label>
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="unpaid">Pendientes</option>
              <option value="upcoming">Próximas (3 meses)</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tarjeta
            </label>
            <select
              value={selectedCard}
              onChange={(e) => setSelectedCard(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="all">Todas las tarjetas</option>
              {creditCards.map(card => (
                <option key={card.id} value={card.id}>
                  {card.name}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {installments.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow">
          <p className="text-gray-500 text-lg">No hay cuotas para mostrar</p>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Estado
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Vencimiento
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Tarjeta
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Descripción
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Cuota
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Monto
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {installments.map((installment) => (
                  <tr
                    key={installment.id}
                    className={`
                      ${installment.paid ? 'bg-green-50' : ''}
                      ${isOverdue(installment.dueDate, installment.paid) ? 'bg-red-50' : ''}
                    `}
                  >
                    <td className="px-6 py-4 whitespace-nowrap">
                      {installment.paid ? (
                        <span className="px-2 py-1 text-xs font-semibold text-green-800 bg-green-200 rounded-full">
                          Pagada
                        </span>
                      ) : isOverdue(installment.dueDate, installment.paid) ? (
                        <span className="px-2 py-1 text-xs font-semibold text-red-800 bg-red-200 rounded-full">
                          Vencida
                        </span>
                      ) : (
                        <span className="px-2 py-1 text-xs font-semibold text-yellow-800 bg-yellow-200 rounded-full">
                          Pendiente
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {formatDate(installment.dueDate)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {installment.creditCardName}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">
                      {installment.transactionDescription || 'Sin descripción'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {installment.installmentNumber}/{installment.totalInstallments}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                      {formatCurrency(installment.amount)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                      <button
                        onClick={() => handleTogglePaid(installment.id, installment.paid)}
                        className={`px-3 py-1 rounded ${
                          installment.paid
                            ? 'bg-yellow-500 hover:bg-yellow-600 text-white'
                            : 'bg-green-500 hover:bg-green-600 text-white'
                        }`}
                      >
                        {installment.paid ? 'Marcar Impaga' : 'Marcar Pagada'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
