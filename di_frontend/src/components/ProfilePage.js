import React, { useState, useEffect } from 'react';
import { useCookies } from 'react-cookie';
import Header from './Header.js';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import { useNavigate } from 'react-router-dom';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';


const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const navigate = useNavigate();
  useEffect(() => {
    fetch('/api/accounts', {
      credentials: 'include',
      headers : {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        setProfile(data);
        if (data === null) navigate('/');
      })
  }, [setProfile] );
  const setData = data => {
    setProfile(data);
  }
  return (
    <div>
      <Header/>
      <Profile profile={profile} setProfile={setProfile}/>
      <AdminUser profile={profile} setProfile={setProfile}/>
    </div>
  );
}

const Profile = (props) => {
  if (props.profile === null) return;
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        '& .MuiTextField-root': { width: '25ch' },
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: '25px'
      }}
    >
    <TextField
      id="filled-read-only-input"
      label="First name"
      defaultValue={props.profile.firstName}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    <TextField
      id="filled-read-only-input"
      label="Last name"
      defaultValue={props.profile.lastName}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    <TextField
      id="filled-read-only-input"
      label="Email"
      defaultValue={props.profile.email}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    <TextField
      id="filled-read-only-input"
      label="Role"
      defaultValue={
        props.profile.roles.map( i => {
          let roleText = i.name.replace('ROLE_', '').toLowerCase();
          return roleText[0].toUpperCase() + roleText.slice(1);
        }
      )}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    <TextField
      id="filled-read-only-input"
      label="Devices"
      defaultValue={
        props.profile.devices.map( i => {
          return ` ${i.name} (${i.qrcode})`;
        }
      )}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    </Box>
  );
}


const AdminUser = (props) => {
  const [cookies] = useCookies(['XSRF-TOKEN']);
  const handleClickEnable = () => {
    fetch('/api/admins/' + props.profile.id, {
      credentials: 'include',
      method: 'PUT',
      headers : {
        'X-XSRF-TOKEN': cookies['XSRF-TOKEN'],
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        props.setProfile(data);
        window.location.reload();
      })
  }
  const handleClickDisable = () => {
    fetch('/api/admins/' + props.profile.id, {
      credentials: 'include',
      method: 'DELETE',
      headers : {
        'X-XSRF-TOKEN': cookies['XSRF-TOKEN'],
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        props.setProfile(data);
        window.location.reload();
      })
  }
  return (
    <Stack sx={{justifyContent: 'center'}} direction="row" spacing={2} marginTop={2}>
      <Button variant="contained" onClick={handleClickDisable}>DIS ADMIN</Button>
      <Button variant="contained" color="error" onClick={handleClickEnable}>EN ADMIN</Button>
    </Stack>
  )
}

export default ProfilePage;