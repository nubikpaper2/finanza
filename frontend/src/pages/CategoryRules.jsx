import { useState, useEffect } from 'react';
import api from '../services/api';
import Navbar from '../components/Navbar';

export default function CategoryRules() {
  const [rules, setRules] = useState([]);
  const [categories, setCategories] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingRule, setEditingRule] = useState(null);
  const [loading, setLoading] = useState(true);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    type: 'CONTAINS',
    pattern: '',
    categoryId: '',
    active: true,
    priority: 0
  });

  useEffect(() => {
    loadRules();
    loadCategories();
  }, []);

  const loadRules = async () => {
    try {
      const response = await api.get('/category-rules');
      setRules(response.data);
    } catch (error) {
      console.error('Error cargando reglas:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const response = await api.get('/categories');
      setCategories(response.data);
    } catch (error) {
      console.error('Error cargando categorías:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const payload = {
        ...formData,
        categoryId: parseInt(formData.categoryId),
        priority: parseInt(formData.priority)
      };

      if (editingRule) {
        await api.put(`/category-rules/${editingRule.id}`, payload);
      } else {
        await api.post('/category-rules', payload);
      }

      resetForm();
      loadRules();
    } catch (error) {
      alert('Error guardando regla: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (rule) => {
    setEditingRule(rule);
    setFormData({
      name: rule.name,
      description: rule.description || '',
      type: rule.type,
      pattern: rule.pattern,
      categoryId: rule.categoryId.toString(),
      active: rule.active,
      priority: rule.priority
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Está seguro de eliminar esta regla?')) return;
    
    try {
      await api.delete(`/category-rules/${id}`);
      loadRules();
    } catch (error) {
      alert('Error eliminando regla: ' + (error.response?.data?.message || error.message));
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      type: 'CONTAINS',
      pattern: '',
      categoryId: '',
      active: true,
      priority: 0
    });
    setEditingRule(null);
    setShowForm(false);
  };

  const ruleTypeLabels = {
    CONTAINS: 'Contiene',
    STARTS_WITH: 'Empieza con',
    ENDS_WITH: 'Termina con',
    EXACT_MATCH: 'Coincidencia exacta',
    REGEX: 'Expresión regular',
    AMOUNT_RANGE: 'Rango de monto'
  };

  const ruleTypeDescriptions = {
    CONTAINS: 'La descripción contiene el texto especificado',
    STARTS_WITH: 'La descripción empieza con el texto especificado',
    ENDS_WITH: 'La descripción termina con el texto especificado',
    EXACT_MATCH: 'La descripción coincide exactamente',
    REGEX: 'La descripción coincide con la expresión regular',
    AMOUNT_RANGE: 'El monto está en el rango especificado (formato: min-max)'
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Reglas de Categorización</h1>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow-sm transition-colors"
          >
            {showForm ? 'Cancelar' : '+ Nueva Regla'}
          </button>
        </div>

      {/* Información */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
        <h2 className="text-sm font-semibold text-blue-900 mb-2">💡 ¿Qué son las reglas de categorización?</h2>
        <p className="text-sm text-blue-800">
          Las reglas permiten asignar automáticamente categorías a las transacciones basándose en su descripción o monto. 
          Cuando importes transacciones o crees una nueva, se aplicarán estas reglas automáticamente según su prioridad.
        </p>
      </div>

      {/* Formulario */}
      {showForm && (
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <h2 className="text-xl font-semibold mb-4">
            {editingRule ? 'Editar Regla' : 'Crear Nueva Regla'}
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Nombre de la Regla *
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  placeholder="ej: Auto-categorizar supermercado"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Categoría *
                </label>
                <select
                  value={formData.categoryId}
                  onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Seleccione una categoría</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name} ({cat.type === 'INCOME' ? 'Ingreso' : 'Gasto'})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Descripción
              </label>
              <input
                type="text"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                placeholder="Descripción opcional de la regla"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tipo de Regla *
                </label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  {Object.entries(ruleTypeLabels).map(([value, label]) => (
                    <option key={value} value={value}>{label}</option>
                  ))}
                </select>
                <p className="mt-1 text-xs text-gray-500">
                  {ruleTypeDescriptions[formData.type]}
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Patrón *
                </label>
                <input
                  type="text"
                  value={formData.pattern}
                  onChange={(e) => setFormData({ ...formData, pattern: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  placeholder={
                    formData.type === 'AMOUNT_RANGE' 
                      ? 'ej: 100-500' 
                      : 'ej: supermercado'
                  }
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Prioridad (mayor = se ejecuta primero)
                </label>
                <input
                  type="number"
                  value={formData.priority}
                  onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="flex items-center pt-7">
                <label className="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={formData.active}
                    onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                    className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                  />
                  <span className="ml-2 text-sm font-medium text-gray-700">Regla activa</span>
                </label>
              </div>
            </div>

            <div className="flex gap-3">
              <button
                type="submit"
                disabled={loading}
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg disabled:opacity-50"
              >
                {loading ? 'Guardando...' : (editingRule ? 'Actualizar Regla' : 'Crear Regla')}
              </button>
              <button
                type="button"
                onClick={resetForm}
                className="px-6 bg-gray-200 hover:bg-gray-300 text-gray-700 py-2 rounded-lg"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Lista de reglas */}
      {loading && !showForm ? (
        <div className="text-center py-8">Cargando reglas...</div>
      ) : rules.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          No hay reglas configuradas
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <table className="min-w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Prioridad
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Nombre
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Tipo
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Patrón
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Categoría
                </th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">
                  Estado
                </th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {rules.map((rule) => (
                <tr key={rule.id} className={!rule.active ? 'opacity-50' : ''}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {rule.priority}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{rule.name}</div>
                    {rule.description && (
                      <div className="text-sm text-gray-500">{rule.description}</div>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {ruleTypeLabels[rule.type]}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    <code className="bg-gray-100 px-2 py-1 rounded">{rule.pattern}</code>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {rule.categoryName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-center">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      rule.active 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-gray-100 text-gray-800'
                    }`}>
                      {rule.active ? '✓ Activa' : '✗ Inactiva'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                    <button
                      onClick={() => handleEdit(rule)}
                      className="text-blue-600 hover:text-blue-900 mr-3"
                    >
                      ✏️
                    </button>
                    <button
                      onClick={() => handleDelete(rule.id)}
                      className="text-red-600 hover:text-red-900"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      </div>
    </div>
  );
}
