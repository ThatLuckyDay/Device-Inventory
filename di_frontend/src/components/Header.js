import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Container from '@mui/material/Container';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';


const pages = ['PROFILE', 'DEVICES', 'SCAN QR'];

const Header = (props) => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <ContainerForm cookies = {props.cookies} removeCookie = {props.removeCookie} auth = {props.auth}
          setAuth = {props.setAuth}
        />
      </AppBar>
    </Box>
  );
}

const ContainerForm = (props) => {
  return (
    <Container maxWidth="xl" >
      <Toolbar disableGutters>
        <PagesForm />
        <LoginLogout cookies = {props.cookies} removeCookie = {props.removeCookie} auth = {props.auth}
          setAuth = {props.setAuth} sx = {{ mr : 2 }}
        />
      </Toolbar>
    </Container>
  );
}

const PagesForm = () => {
  const navigate = useNavigate();
  const handleRedirect = (event) => {
    let ref = event.currentTarget.innerText;
    if (ref === pages[0]) navigate('/api/accounts');
    if (ref === pages[1]) navigate('/api/devices');
    if (ref === pages[2]) navigate('/api/scanner');
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