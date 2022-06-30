import React, { useState, useEffect } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import AccountCircle from '@mui/icons-material/AccountCircle';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import MenuItem from '@mui/material/MenuItem';
import Menu from '@mui/material/Menu';
import { useCookies } from 'react-cookie';


const Header = () => {
  const [auth, setAuth] = React.useState(false);
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [cookies, removeCookie] = useCookies(['XSRF-TOKEN']);
  const [user, setUser] = useState(undefined);

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleChange = (event) => {
    if (!auth) {
      login();
      if (user !== '') setAuth(event.target.checked);
    } else {
      logout(cookies);
      setAuth(event.target.checked);
    }
  };

  const login = () => {
    let port = (window.location.port ? ':' + window.location.port : '');
    if (port === ':3000') {
      port = ':8080';
    }
    window.location.href = `//${window.location.hostname}${port}/oauth2/authorization/google`;
  }

  useEffect(() => {
    fetch('api/user', { credentials: 'include' })
      .then(response => response.text())
      .then(body => {
        if (body !== '') setUser(JSON.parse(body));
      });
  }, [setUser])

  const logout = (cookies) => {
    fetch('/api/logout', {
      method: 'POST', credentials: 'include',
      headers: { 'X-XSRF-TOKEN': cookies['XSRF-TOKEN'] }
    })
      .then(res => res.json())
      .then(response => {
        window.location.href = `${response.logoutUrl}?id_token_hint=${response.idToken}`
          + `&post_logout_redirect_uri=${window.location.origin}`;
      });
  }

  const message = user ?
    <h2>Welcome, {user.name}!</h2> :
    <p>Please log in to manage your Devices.</p>;

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <Box sx={{ flexGrow: 1 }}>

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

      <AppBar position="static">
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
          >
          <MenuIcon />
          </IconButton>
          {auth && (
            <div>
              <IconButton
                size="large"
                aria-label="account of current user"
                aria-controls="menu-appbar"
                aria-haspopup="true"
                onClick={handleMenu}
                color="inherit"
              >
                <AccountCircle />
              </IconButton>
              <Menu
                id="menu-appbar"
                anchorEl={anchorEl}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorEl)}
                onClose={handleClose}
              >
                <MenuItem onClick={handleClose}>Profile</MenuItem>
                <MenuItem onClick={handleClose}>My account</MenuItem>
              </Menu>
            </div>
          )}
        </Toolbar>
      </AppBar>

      <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
        {message}
      </Typography>

    </Box>

  );
}

export default Header;