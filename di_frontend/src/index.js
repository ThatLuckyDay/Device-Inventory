import React from 'react';
import * as ReactDOMClient from 'react-dom/client';
import Header from './components/Header';
import { CookiesProvider } from 'react-cookie';


class MainPage extends React.Component {
  render() {
    return (
      <div className="mainPage">
        <div className="header" >
          <Header />
        </div>
      </div>
    );
  }
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
