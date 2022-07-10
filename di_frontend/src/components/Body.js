import React, { useState, useEffect } from 'react';


const Body = (props) => {
  let message = props.user ? `Привет, ${props.user.firstName}!` : 'Пожалуйста зарегистрируйтесь';
  return (
    <h1> {message} </h1>
  );
}

export default Body;