import { useState, useEffect } from 'react';
import api from '../services/api';
import Navbar from '../components/Navbar';
import Papa from 'papaparse';

export default function Import() {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState('');
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [result, setResult] = useState(null);
  const [importType, setImportType] = useState('csv'); // 'csv' o 'excel'

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = async () => {
    try {
      const response = await api.get('/accounts');
      setAccounts(response.data);
      if (response.data.length > 0) {
        setSelectedAccount(response.data[0].id.toString());
      }
    } catch (error) {
      console.error('Error cargando cuentas:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      // Detectar tipo de archivo
      const extension = selectedFile.name.split('.').pop().toLowerCase();
      if (extension === 'csv') {
        setImportType('csv');
      } else if (extension === 'xlsx' || extension === 'xls') {
        setImportType('excel');
      }
    }
  };

  const handleImport = async () => {
    if (!file || !selectedAccount) {
      alert('Por favor seleccione un archivo y una cuenta');
      return;
    }

    const importLoading = true;
    setResult(null);

    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('accountId', selectedAccount);

      const endpoint = importType === 'csv' ? '/import/csv' : '/import/excel';
      const response = await api.post(endpoint, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      setResult(response.data);
      setFile(null);
      // Reset file input
      document.getElementById('file-input').value = '';
    } catch (error) {
      alert('Error importando archivo: ' + (error.response?.data?.message || error.message));
    }
  };

  const downloadTemplate = () => {
    const template = [
      ['fecha', 'descripcion', 'monto', 'tipo', 'categoria', 'notas'],
      ['2024-01-15', 'Pago de salario', '3000.00', 'INCOME', 'Salario', ''],
      ['2024-01-16', 'Compra en supermercado', '150.50', 'EXPENSE', 'Alimentación', 'Compra semanal'],
      ['2024-01-17', 'Pago de renta', '800.00', 'EXPENSE', 'Vivienda', '']
    ];

    const csv = Papa.unparse(template);
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', 'plantilla_importacion.csv');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-8">Cargando...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Importar Transacciones</h1>

      {/* Instrucciones */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-6">
        <h2 className="text-lg font-semibold text-blue-900 mb-3">📋 Instrucciones</h2>
        <ul className="space-y-2 text-sm text-blue-800">
          <li>• El archivo debe contener las columnas: <strong>fecha, descripcion, monto, tipo</strong></li>
          <li>• Columnas opcionales: <strong>categoria, notas</strong></li>
          <li>• El campo <strong>tipo</strong> debe ser INCOME (ingreso) o EXPENSE (gasto)</li>
          <li>• El formato de fecha puede ser: YYYY-MM-DD, DD/MM/YYYY, o MM/DD/YYYY</li>
          <li>• Si no se especifica categoría, se intentará asignar automáticamente según las reglas configuradas</li>
          <li>• Formatos soportados: CSV (.csv) y Excel (.xlsx, .xls)</li>
        </ul>
        <button
          onClick={downloadTemplate}
          className="mt-4 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm"
        >
          📥 Descargar Plantilla CSV
        </button>
      </div>

      {/* Formulario de importación */}
      <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Cuenta Destino
            </label>
            <select
              value={selectedAccount}
              onChange={(e) => setSelectedAccount(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              {accounts.map(account => (
                <option key={account.id} value={account.id}>
                  {account.name} - ${account.balance.toFixed(2)}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Archivo (CSV o Excel)
            </label>
            <input
              id="file-input"
              type="file"
              accept=".csv,.xlsx,.xls"
              onChange={handleFileChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
            {file && (
              <p className="mt-2 text-sm text-gray-600">
                Archivo seleccionado: <strong>{file.name}</strong> ({importType.toUpperCase()})
              </p>
            )}
          </div>

          <button
            onClick={handleImport}
            disabled={loading || !file || !selectedAccount}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed font-semibold"
          >
            {loading ? '⏳ Importando...' : '📤 Importar Transacciones'}
          </button>
        </div>
      </div>

      {/* Resultados */}
      {result && (
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-xl font-semibold mb-4">Resultados de la Importación</h2>
          
          <div className="grid grid-cols-3 gap-4 mb-6">
            <div className="bg-blue-50 rounded-lg p-4 text-center">
              <p className="text-sm text-gray-600">Total</p>
              <p className="text-2xl font-bold text-blue-600">{result.total}</p>
            </div>
            <div className="bg-green-50 rounded-lg p-4 text-center">
              <p className="text-sm text-gray-600">Importadas</p>
              <p className="text-2xl font-bold text-green-600">{result.imported}</p>
            </div>
            <div className="bg-red-50 rounded-lg p-4 text-center">
              <p className="text-sm text-gray-600">Fallidas</p>
              <p className="text-2xl font-bold text-red-600">{result.failed}</p>
            </div>
          </div>

          {result.errors.length > 0 && (
            <div className="mb-6">
              <h3 className="font-semibold text-red-600 mb-2">❌ Errores:</h3>
              <div className="bg-red-50 border border-red-200 rounded-lg p-4 max-h-60 overflow-y-auto">
                <ul className="space-y-1 text-sm text-red-800">
                  {result.errors.map((error, index) => (
                    <li key={index}>• {error}</li>
                  ))}
                </ul>
              </div>
            </div>
          )}

          {result.transactions.length > 0 && (
            <div>
              <h3 className="font-semibold text-green-600 mb-3">✅ Transacciones Importadas:</h3>
              <div className="overflow-x-auto max-h-96 overflow-y-auto">
                <table className="min-w-full">
                  <thead className="bg-gray-50 sticky top-0">
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">
                        Fecha
                      </th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">
                        Descripción
                      </th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">
                        Categoría
                      </th>
                      <th className="px-4 py-2 text-right text-xs font-medium text-gray-500 uppercase">
                        Monto
                      </th>
                      <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">
                        Tipo
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {result.transactions.map((transaction) => (
                      <tr key={transaction.id}>
                        <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900">
                          {transaction.transactionDate}
                        </td>
                        <td className="px-4 py-2 text-sm text-gray-900">
                          {transaction.description}
                        </td>
                        <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-600">
                          {transaction.categoryName || <span className="text-gray-400 italic">Sin categoría</span>}
                        </td>
                        <td className="px-4 py-2 whitespace-nowrap text-sm text-right font-medium">
                          ${transaction.amount.toFixed(2)}
                        </td>
                        <td className="px-4 py-2 whitespace-nowrap text-center">
                          <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                            transaction.type === 'INCOME' 
                              ? 'bg-green-100 text-green-800' 
                              : 'bg-red-100 text-red-800'
                          }`}>
                            {transaction.type === 'INCOME' ? '💰 Ingreso' : '💸 Gasto'}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}
      </div>
    </div>
  );
}
