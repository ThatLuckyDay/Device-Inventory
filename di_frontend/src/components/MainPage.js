import React, { useEffect, useState } from 'react';
import Header from './Header.js';
import { SayHi } from './Body.js';
import { useCookies } from 'react-cookie';


const MainPage = () => {
  const [cookies, removeCookie] = useCookies(['XSRF-TOKEN']);
  const [user, setUser] = useState(false);
  const [auth, setAuth] = useState(false);
  useEffect(() => {
    if (!auth) setUser(null);
    fetch('/api/users', {
      credentials: 'include',
      headers : {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
      .then(response => response.text())
      .then(body => {
        if (body !== '') {
          setUser(JSON.parse(body));
          setAuth(true);
        }
      });
  }, [setUser, setAuth, auth] );
  return (
    <div>
      <Header cookies = {cookies} removeCookie = {removeCookie} auth = {auth} setAuth = {setAuth} />
      <SayHi cookies = {cookies} removeCookie = {removeCookie} user = {user} setUser = {setUser} />
    </div>
  );
}

export default MainPage;