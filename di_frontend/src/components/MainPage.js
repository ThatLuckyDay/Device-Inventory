import React, { useEffect, useState } from 'react';
import Header from './Header.js';
import Body from './Body.js';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { useCookies } from 'react-cookie';



const MainPage = () => {
  const [cookies, removeCookie] = useCookies(['XSRF-TOKEN']);
  const [user, setUser] = useState(false);
  const [auth, setAuth] = useState(false);

  useEffect(() => {
    if (!auth) setUser(null);
    fetch('/api/users', {
      credentials: 'include',
      crossDomain: true
    })
      .then(response => response.text())
      .then(body => {
        if (body !== '') {
          setUser(JSON.parse(body));
          setAuth(true);
        }
      });
  }, [setUser, setAuth])

  return (
    <Router>
      <Routes>
        <Route exact path="/" element = {
          <Header cookies = {cookies} removeCookie = {removeCookie} auth = {auth} setAuth = {setAuth} />
        } />
      </Routes>
    </Router>
  );
}

export default MainPage;