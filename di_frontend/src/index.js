import React from 'react';
import * as ReactDOMClient from 'react-dom/client';
import MainPage from './components/MainPage';
import { CookiesProvider } from 'react-cookie';

// ========================================
const root = ReactDOMClient.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <CookiesProvider>
      <MainPage />
    </CookiesProvider>
  </React.StrictMode>
);
