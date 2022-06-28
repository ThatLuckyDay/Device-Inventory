import React from 'react';
import * as ReactDOMClient from 'react-dom/client';
import Header from './components/Header';
import { CookiesProvider } from 'react-cookie';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';


const MainPage = () => {
  return (
    <Router>
      <Routes>
        <Route exact path="/" element={<Header />}/>
      </Routes>
    </Router>
  );
}

// ========================================
const root = ReactDOMClient.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <CookiesProvider>
      <MainPage />
    </CookiesProvider>
  </React.StrictMode>
);
