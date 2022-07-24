import React, { useState, useEffect } from 'react';
import { useCookies } from 'react-cookie';
import Header from './Header.js';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import { useNavigate } from 'react-router-dom';


const ProfilePage = () => {
  return (
    <div>
      <Header />
      <Profile/>
    </div>
  );
}

const Profile = () => {
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
  if (profile === null) return;
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
      defaultValue={profile.firstName}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    <TextField
      id="filled-read-only-input"
      label="Last name"
      defaultValue={profile.lastName}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    <TextField
      id="filled-read-only-input"
      label="Email"
      defaultValue={profile.email}
      InputProps={{
        readOnly: true,
      }}
      variant="filled"
    />
    <TextField
      id="filled-read-only-input"
      label="Role"
      defaultValue={
        profile.roles.map( i => {
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
        profile.devices.map( i => {
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

export default ProfilePage;