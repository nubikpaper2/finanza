import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-8">
              <h1 className="text-2xl font-bold text-blue-600">Finanza</h1>
              <div className="hidden md:flex space-x-4">
                <button
                  onClick={() => navigate('/dashboard')}
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                >
                  Dashboard
                </button>
                <button
                  onClick={() => navigate('/transactions')}
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                >
                  Transacciones
                </button>
                <button
                  onClick={() => navigate('/categories')}
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                >
                  Categorías
                </button>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-gray-700">👋 {user?.firstName}</span>
              <button
                onClick={handleLogout}
                className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition"
              >
                Cerrar Sesión
              </button>
            </div>
          </div>
        </div>
      </nav>

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
                <p className="text-2xl font-bold text-green-600">$0.00</p>
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
                <p className="text-2xl font-bold text-red-600">$0.00</p>
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
                <p className="text-2xl font-bold text-blue-600">$0.00</p>
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

        {/* System Info */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-xl font-semibold mb-4">Información del Usuario</h3>
            <div className="space-y-3">
              <div>
                <p className="text-sm text-gray-500">Email</p>
                <p className="font-medium">{user?.email}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Nombre Completo</p>
                <p className="font-medium">{user?.firstName} {user?.lastName}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Roles</p>
                <p className="font-medium">{user?.roles?.join(', ').replace(/ROLE_/g, '')}</p>
              </div>
              {user?.organizationName && (
                <div>
                  <p className="text-sm text-gray-500">Organización</p>
                  <p className="font-medium">{user.organizationName}</p>
                </div>
              )}
            </div>
          </div>

          <div className="bg-gradient-to-br from-blue-50 to-purple-50 rounded-lg shadow p-6">
            <h3 className="text-xl font-semibold mb-4 text-gray-800">✅ Sistema Funcional</h3>
            <div className="space-y-2 text-gray-700">
              <p>✓ Autenticación JWT implementada</p>
              <p>✓ Gestión de Transacciones CRUD</p>
              <p>✓ Gestión de Categorías CRUD</p>
              <p>✓ Transferencias entre cuentas</p>
              <p>✓ Filtros y búsqueda avanzada</p>
              <p>✓ Validación de saldos</p>
              <p>✓ Tags y adjuntos en transacciones</p>
              <p className="mt-4 font-bold text-blue-600">
                🚀 Sistema de movimientos básico listo!
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
