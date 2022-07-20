import Header from './Header';
import { useState } from 'react';
import { useCookies } from 'react-cookie';
import Box from '@mui/material/Box';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import SearchIcon from '@mui/icons-material/Search';


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
  const handleChange = (event) => {
    const { name, value } = event.target;
    setCode({ ...code, [name]: value });
  }
  const handleSubmit = (event) => {
    console.log('submit');
  }
  return(
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center'
      }}
    >
      <form onSubmit={handleSubmit}>
        <TextField
          label={'QR Code'}
          margin="normal"
          onChange={handleChange}
          InputProps={SearchButton}
        />
      </form>
    </Box>
  );
}

const SearchButton = {
  endAdornment: (
    <InputAdornment position="end">
      <IconButton edge="end" color="primary" >
        <SearchIcon />
      </IconButton>
    </InputAdornment>
  ),
}

export default DeviceCreatorPage;