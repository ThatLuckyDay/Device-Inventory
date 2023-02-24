import React, { useState, useEffect } from 'react';
import { useCookies } from 'react-cookie';
import Header from './Header.js';



const MainPage = () => {
  return (
    <div>
      <Header/>
      <SayHi />
    </div>
  );
}

const SayHi = () => {
  const [cookies] = useCookies(['XSRF-TOKEN']);
  const [user, setUser] = useState(null);
  useEffect(() => {
    fetch('/api/users', {
      credentials: 'include',
      headers : {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
      .then(response => response.text())
      .then(body => {
        if (typeof JSON.parse(body).email === 'undefined') setUser(null);
        else if (body !== '') setUser(JSON.parse(body));
      });
  }, [setUser] );
  if (cookies['XSRF-TOKEN'] === 'undefined' && user !== null) setUser(null);
  let message = user ? `Привет, ${user.firstName}!` : 'Пожалуйста зарегистрируйтесь';
  return (
    <h1> {message} </h1>
  );
}

export default MainPage;