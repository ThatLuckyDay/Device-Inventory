import React, { useState, useEffect } from 'react';


const Body = (props) => {
}

const Authorize = () => {
  const [user, setUser] = useState(false);
  return (
    <h1> {user} . Test </h1>
  );
}

export default { Body, Authorize };