import Header from './Header';
import { useState } from 'react';
import { useCookies } from 'react-cookie';
import { useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import SearchIcon from '@mui/icons-material/Search';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import FormControl from '@mui/material/FormControl';


const DeviceCreatorPage = () => {
  return(
    <div>
      <Header />
      <InputQRForm />
    </div>
  );
}

const InputQRForm = () => {
  const [code, setCode] = useState();
  const [cookies] = useCookies(['XSRF-TOKEN']);
  const navigate = useNavigate();
  const handleChange = (event) => {
    const { name, value } = event.target;
    setCode({ ...code, [name]: value });
  }
  const handleSubmit = (event) => {
    event.preventDefault();
    fetch('/api/owners', {
      method: 'POST',
      headers: {
        'X-XSRF-TOKEN': cookies['XSRF-TOKEN'],
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(code),
      credentials: 'include'
    })
      .then(response => response.json())
      .then(data => {
          if (data !== null) navigate('/devices');
      });
  }
  return(
    <Box sx={{ display: 'flex', justifyContent: 'center', marginTop: '25px' }} >
      <FormControl sx={{ m: 1, width: '40ch' }} variant="outlined">
        <InputLabel>QR Code</InputLabel>
        <OutlinedInput
           id="outlined-adornment-qr"
           onChange={handleChange}
           name="qrcode"
           endAdornment={
             <InputAdornment position="end">
               <IconButton edge="end" color="primary" onClick={handleSubmit}>
                 <SearchIcon />
               </IconButton>
             </InputAdornment>
           }
           label='QR Code'
        />
        </FormControl>
    </Box>
  );
}

export default DeviceCreatorPage;