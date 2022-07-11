import React, { useState, useEffect } from 'react';


const SayHi = (props) => {
  const test = props.user;
  let message = test ? `Привет, ${props.user.firstName}!` : 'Пожалуйста зарегистрируйтесь';
  return (
    <h1> {message} </h1>
  );
}

const Devices = (props) => {
  const [devices, setDevices] = useState([]);
  useEffect(() => {
    fetch('/api/devices')
      .then(response => response.json())
      .then(data => {
        setDevices(data);
      })
  }, [] );
}

export { SayHi, Devices }