import React from 'react';
import * as ReactDOMClient from 'react-dom/client';
import MainPage from './components/MainPage';
import DevicesPage from './components/DevicesPage';
import ProfilePage from './components/ProfilePage';
import DeviceCreatorPage from './components/DeviceCreatorPage';
import DeviceScannerPage from './components/DeviceScannerPage';
import { CookiesProvider } from 'react-cookie';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';


// ========================================
const root = ReactDOMClient.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <CookiesProvider>
      <Router>
        <Routes>
          <Route exact path="/" element={ <MainPage/> } />
          <Route exact path="/devices" element={ <DevicesPage/> } />
          <Route exact path="/accounts" element={ <ProfilePage/> } />
          <Route exact path="/devices/:id" element={ <DeviceCreatorPage/> } />
          <Route exact path="/scanner" element={ <DeviceScannerPage/> } />
        </Routes>
      </Router>
    </CookiesProvider>
  </React.StrictMode>
);
