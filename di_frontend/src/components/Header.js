import { useState } from 'react';
//import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
//import Toolbar from '@mui/material/Toolbar';
//import Typography from '@mui/material/Typography';
//import IconButton from '@mui/material/IconButton';
//import MenuIcon from '@mui/icons-material/Menu';
//import AccountCircle from '@mui/icons-material/AccountCircle';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
//import MenuItem from '@mui/material/MenuItem';
//import Menu from '@mui/material/Menu';



const Header = (props) => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <LoginLogout cookies = {props.cookies} removeCookie = {props.removeCookie} auth = {props.auth}
        setAuth = {props.setAuth}
      />
    </Box>
  );
}

const LoginLogout = (props) => {

  const handleChange = (event) => {
    if (!props.auth)  {
      let port = (window.location.port ? ':' + window.location.port : '');
      if (port === ':3000') port = ':8080';
      window.location.href = `//${window.location.hostname}${port}/oauth2/authorization/google`;
    } else  {
      fetch('/api/logout', {
        method: 'POST', credentials: 'include',
        headers: { 'X-XSRF-TOKEN': props.cookies['XSRF-TOKEN'] }
      })
      .then(response => {
        props.removeCookie('XSRF-TOKEN');
        props.setAuth(false);
      });
    }
  };

  return (
    <FormGroup>
      <FormControlLabel
        control={
          <Switch
            checked={props.auth}
            onChange={handleChange}
            aria-label="login switch"
          />
        }
        label={props.auth ? 'Logout' : 'Login'}
      />
    </FormGroup>
  );
}

export default Header;