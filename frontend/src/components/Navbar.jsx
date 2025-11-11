import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <nav className="bg-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center space-x-8">
            <h1 className="text-2xl font-bold text-blue-600">Finanza</h1>
            <div className="hidden md:flex space-x-4">
              <button
                onClick={() => navigate('/dashboard')}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive('/dashboard')
                    ? 'bg-blue-100 text-blue-700'
                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100'
                }`}
              >
                Dashboard
              </button>
              <button
                onClick={() => navigate('/transactions')}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive('/transactions')
                    ? 'bg-blue-100 text-blue-700'
                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100'
                }`}
              >
                Transacciones
              </button>
              <button
                onClick={() => navigate('/categories')}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive('/categories')
                    ? 'bg-blue-100 text-blue-700'
                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100'
                }`}
              >
                Categorías
              </button>
              <button
                onClick={() => navigate('/budgets')}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive('/budgets')
                    ? 'bg-blue-100 text-blue-700'
                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100'
                }`}
              >
                Presupuestos
              </button>
              <button
                onClick={() => navigate('/reports')}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive('/reports')
                    ? 'bg-blue-100 text-blue-700'
                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100'
                }`}
              >
                Reportes
              </button>
              <button
                onClick={() => navigate('/import')}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive('/import')
                    ? 'bg-blue-100 text-blue-700'
                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100'
                }`}
              >
                Importar
              </button>
              <button
                onClick={() => navigate('/category-rules')}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive('/category-rules')
                    ? 'bg-blue-100 text-blue-700'
                    : 'text-gray-700 hover:text-blue-600 hover:bg-gray-100'
                }`}
              >
                Reglas
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
  );
}
