import React from 'react';
import * as ReactDOMClient from 'react-dom/client';
import MainPage from './components/MainPage';
import { CookiesProvider } from 'react-cookie';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

// ========================================
const root = ReactDOMClient.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <CookiesProvider>
      <Router>
        <Routes>
          <Route exact path="/" element={ <MainPage/> } />
        </Routes>
      </Router>
    </CookiesProvider>
  </React.StrictMode>
);
