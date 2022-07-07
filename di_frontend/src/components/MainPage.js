import React from 'react';
import Header from './Header.js';
import Body from './Body.js';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { useCookies } from 'react-cookie';



const MainPage = () => {
  const [cookies, removeCookie] = useCookies(['XSRF-TOKEN']);
  const deleteToken = () => {
    removeCookie('XSRF-TOKEN');
  }

  return (
    <Router>
      <Routes>
        <Route exact path="/" element = {
          <Header value = {cookies} onClick = {removeCookie} />
        } />
      </Routes>
    </Router>
  );
}

export default MainPage;