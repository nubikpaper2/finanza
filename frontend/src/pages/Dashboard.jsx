import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { transactionService } from '../services/api';
import Navbar from '../components/Navbar';

function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    income: 0,
    expense: 0,
    balance: 0
  });
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      // Obtener transacciones del mes actual
      const now = new Date();
      const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
      const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
      
      const params = {
        startDate: firstDay.toISOString().split('T')[0],
        endDate: lastDay.toISOString().split('T')[0],
        page: 0,
        size: 100
      };

      const response = await transactionService.getAll(params);
      const transactions = response.content || [];

      // Calcular estadísticas
      const income = transactions
        .filter(t => t.type === 'INCOME')
        .reduce((sum, t) => sum + t.amount, 0);
      
      const expense = transactions
        .filter(t => t.type === 'EXPENSE')
        .reduce((sum, t) => sum + t.amount, 0);

      setStats({
        income,
        expense,
        balance: income - expense
      });

      // Obtener transacciones recientes
      const recentParams = {
        page: 0,
        size: 5,
        sortBy: 'transactionDate',
        sortDirection: 'DESC'
      };
      const recentResponse = await transactionService.getAll(recentParams);
      setRecentTransactions(recentResponse.content || []);

    } catch (error) {
      console.error('Error cargando datos del dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-2">
            ¡Bienvenido, {user?.firstName}! 👋
          </h2>
          <p className="text-gray-600">Sistema de Gestión Financiera Finanza</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-500 text-sm mb-1">Ingresos del Mes</p>
                <p className="text-2xl font-bold text-green-600">
                  {loading ? '...' : formatCurrency(stats.income)}
                </p>
              </div>
              <div className="bg-green-100 rounded-full p-3">
                <span className="text-2xl">💰</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-500 text-sm mb-1">Gastos del Mes</p>
                <p className="text-2xl font-bold text-red-600">
                  {loading ? '...' : formatCurrency(stats.expense)}
                </p>
              </div>
              <div className="bg-red-100 rounded-full p-3">
                <span className="text-2xl">💸</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-500 text-sm mb-1">Balance Total</p>
                <p className={`text-2xl font-bold ${stats.balance >= 0 ? 'text-blue-600' : 'text-red-600'}`}>
                  {loading ? '...' : formatCurrency(stats.balance)}
                </p>
              </div>
              <div className="bg-blue-100 rounded-full p-3">
                <span className="text-2xl">📊</span>
              </div>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow p-6 mb-8">
          <h3 className="text-xl font-semibold mb-4">Acciones Rápidas</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <button
              onClick={() => navigate('/transactions')}
              className="bg-blue-600 text-white p-4 rounded-lg hover:bg-blue-700 transition flex items-center justify-center space-x-2"
            >
              <span className="text-xl">➕</span>
              <span>Nueva Transacción</span>
            </button>
            <button
              onClick={() => navigate('/categories')}
              className="bg-purple-600 text-white p-4 rounded-lg hover:bg-purple-700 transition flex items-center justify-center space-x-2"
            >
              <span className="text-xl">📁</span>
              <span>Gestionar Categorías</span>
            </button>
            <button
              onClick={() => navigate('/transactions')}
              className="bg-green-600 text-white p-4 rounded-lg hover:bg-green-700 transition flex items-center justify-center space-x-2"
            >
              <span className="text-xl">📈</span>
              <span>Ver Reportes</span>
            </button>
          </div>
        </div>

        {/* Recent Transactions & System Info */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Recent Transactions */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold">Transacciones Recientes</h3>
              <button
                onClick={() => navigate('/transactions')}
                className="text-blue-600 hover:text-blue-700 text-sm"
              >
                Ver todas →
              </button>
            </div>
            {loading ? (
              <p className="text-gray-500 text-center py-4">Cargando...</p>
            ) : recentTransactions.length === 0 ? (
              <p className="text-gray-500 text-center py-4">No hay transacciones aún</p>
            ) : (
              <div className="space-y-3">
                {recentTransactions.map((transaction) => (
                  <div
                    key={transaction.id}
                    className="flex justify-between items-center p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition cursor-pointer"
                    onClick={() => navigate('/transactions')}
                  >
                    <div className="flex-1">
                      <p className="font-medium text-gray-900">
                        {transaction.description || 'Sin descripción'}
                      </p>
                      <p className="text-sm text-gray-500">
                        {formatDate(transaction.transactionDate)}
                        {transaction.categoryName && ` • ${transaction.categoryName}`}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className={`font-bold ${
                        transaction.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
                      }`}>
                        {transaction.type === 'INCOME' ? '+' : '-'}
                        {formatCurrency(Math.abs(transaction.amount))}
                      </p>
                      <p className="text-xs text-gray-500">
                        {transaction.type === 'INCOME' ? 'Ingreso' : 'Gasto'}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* User Info & System Status */}
          <div className="space-y-6">
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-xl font-semibold mb-4">Información del Usuario</h3>
              <div className="space-y-3">
                <div>
                  <p className="text-sm text-gray-500">EMAIL</p>
                  <p className="font-medium">{user?.email}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">NOMBRE COMPLETO</p>
                  <p className="font-medium">{user?.firstName} {user?.lastName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">ROLES</p>
                  <p className="font-medium">{user?.roles?.join(', ')}</p>
                </div>
                {user?.organizationName && (
                  <div>
                    <p className="text-sm text-gray-500">ORGANIZACIÓN</p>
                    <p className="font-medium">{user.organizationName}</p>
                  </div>
                )}
                {user?.organizationId && (
                  <div>
                    <p className="text-sm text-gray-500">ID ORGANIZACIÓN</p>
                    <p className="font-medium">#{user.organizationId}</p>
                  </div>
                )}
                {user?.id && (
                  <div>
                    <p className="text-sm text-gray-500">ID USUARIO</p>
                    <p className="font-medium">#{user.id}</p>
                  </div>
                )}
              </div>
            </div>

            <div className="bg-gradient-to-br from-blue-50 to-purple-50 rounded-lg shadow p-6">
              <h3 className="text-xl font-semibold mb-4 text-gray-800">✅ Sistema Funcional</h3>
              <div className="space-y-2 text-gray-700">
                <p>✓ Autenticación JWT implementada</p>
                <p>✓ Registro de usuarios funcional</p>
                <p>✓ Login con credenciales seguras</p>
                <p>✓ Gestión de organizaciones</p>
                <p>✓ Base de datos PostgreSQL configurada</p>
                <p>✓ Entidades: Users, Organizations, Accounts</p>
                <p>✓ Roles de usuario (USER/ADMIN)</p>
                <p>✓ Data seeder con cuentas demo</p>
                <p className="mt-4 font-bold text-blue-600">
                  🚀 Sistema listo para desarrollo de funcionalidades adicionales
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
