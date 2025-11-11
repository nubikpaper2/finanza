import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Transactions from './pages/Transactions';
import Categories from './pages/Categories';
import Budgets from './pages/Budgets';
import Reports from './pages/Reports';
import Import from './pages/Import';
import CategoryRules from './pages/CategoryRules';
import CreditCards from './pages/CreditCards';
import ExchangeRates from './pages/ExchangeRates';
import Installments from './pages/Installments';

function PrivateRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" />;
}

function PublicRoute({ children }) {
  const { user } = useAuth();
  return !user ? children : <Navigate to="/dashboard" />;
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          } />
          <Route path="/register" element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          } />
          <Route path="/dashboard" element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          } />
          <Route path="/transactions" element={
            <PrivateRoute>
              <Transactions />
            </PrivateRoute>
          } />
          <Route path="/categories" element={
            <PrivateRoute>
              <Categories />
            </PrivateRoute>
          } />
          <Route path="/budgets" element={
            <PrivateRoute>
              <Budgets />
            </PrivateRoute>
          } />
          <Route path="/reports" element={
            <PrivateRoute>
              <Reports />
            </PrivateRoute>
          } />
          <Route path="/import" element={
            <PrivateRoute>
              <Import />
            </PrivateRoute>
          } />
          <Route path="/category-rules" element={
            <PrivateRoute>
              <CategoryRules />
            </PrivateRoute>
          } />
          <Route path="/credit-cards" element={
            <PrivateRoute>
              <CreditCards />
            </PrivateRoute>
          } />
          <Route path="/exchange-rates" element={
            <PrivateRoute>
              <ExchangeRates />
            </PrivateRoute>
          } />
          <Route path="/installments" element={
            <PrivateRoute>
              <Installments />
            </PrivateRoute>
          } />
          <Route path="/" element={<Navigate to="/dashboard" />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
