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



const Header = ({value, onClick}) => {

  return (
    <Box sx={{ flexGrow: 1 }}>
      <LoginLogout value = {value} onClick = {onClick} />
    </Box>
  );
}

const LoginLogout = ({value, onClick}) => {
  const [auth, setAuth] = useState(false);

  const handleChange = (event) => {
    if (!auth)  login(setAuth);
    else  logout(setAuth, value, onClick);
  };

  return (
    <FormGroup>
      <FormControlLabel
        control={
          <Switch
            checked={auth}
            onChange={handleChange}
            aria-label="login switch"
          />
        }
        label={auth ? 'Logout' : 'Login'}
      />
    </FormGroup>
  );
}

const login = (setAuth) => {
  let port = (window.location.port ? ':' + window.location.port : '');
  if (port === ':3000') {
    port = ':8080';
  }
  window.location.href = `//${window.location.hostname}${port}/oauth2/authorization/google`;
  setAuth(true);
}

const logout = (setAuth, cookies, deleteCookie) => {
  fetch('/api/logout', {
    method: 'POST', credentials: 'include',
    headers: { 'X-XSRF-TOKEN': cookies['XSRF-TOKEN'] }
  })
  .then(response => {
    deleteCookie('XSRF-TOKEN');
    setAuth(false);
  });
}

export default Header;