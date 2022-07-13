import React, { useEffect, useState } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Container from '@mui/material/Container';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';
import { useCookies } from 'react-cookie';


const pages = ['PROFILE', 'DEVICES', 'SCAN QR'];

const Header = () => {
  const [cookies, removeCookie] = useCookies(['XSRF-TOKEN']);
  const [auth, setAuth] = useState(false);
  const navigate = useNavigate();
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
        if (body !== '') setAuth(true);
        else navigate('/');
      });
  }, [setAuth] );
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <ContainerForm cookies = {cookies} removeCookie = {removeCookie}
          auth = {auth} setAuth = {setAuth}
        />
      </AppBar>
    </Box>
  );
}

const ContainerForm = (props) => {
  return (
    <Container maxWidth="xl" >
      <Toolbar disableGutters>
        <PagesForm/>
        <LoginLogout cookies = {props.cookies} removeCookie = {props.removeCookie}
          auth = {props.auth} setAuth = {props.setAuth}
        />
      </Toolbar>
    </Container>
  );
}

const PagesForm = () => {
  const navigate = useNavigate();
  const handleRedirect = (event) => {
    let ref = event.currentTarget.innerText;
    if (ref === pages[0]) navigate('/accounts');
    if (ref === pages[1]) navigate('/devices');
    if (ref === pages[2]) navigate('/scanner');
  }
  return (
    <Box sx={{ flexGrow: 1, display: 'inline-flex', flexWrap: 'wrap' }}>
      { pages.map((page) => (
      <Button
        key={page}
        onClick={handleRedirect}
        sx={{ my: 2, color: 'white', display: 'block' }}
      >
      {page}
      </Button>
      ))}
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
      fetch('/api/users', {
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
            color="default"
          />
        }
        label={ props.auth ? 'Logout' : 'Login' }
      />
    </FormGroup>
  );
}

export default Header;