import React, { useEffect, useState } from 'react';
import { useCookies } from 'react-cookie';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import Header from './Header';

const CreateDevicePage = () => {
  const initialFormState = {
    name: '',
    qrcode: ''
  };
  const [cookies] = useCookies(['XSRF-TOKEN']);
  const [device, setDevice] = useState(initialFormState);
  const navigate = useNavigate();
  const { id } = useParams();
  useEffect(() => {
    if (id !== 'new') {
      fetch(`/api/devices/${id}`)
        .then(response => response.json())
        .then(data => setDevice(data));
    }
  }, [id, setDevice]);
  const handleChange = (event) => {
    const { name, value } = event.target;
    setDevice({ ...device, [name]: value })
  }
  const handleSubmit = async (event) => {
    event.preventDefault();
    await fetch('/api/devices' + (device.id ? '/' + device.id : ''), {
      method: (device.id) ? 'PUT' : 'POST',
      headers: {
        'X-XSRF-TOKEN': cookies['XSRF-TOKEN'],
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(device),
      credentials: 'include'
    });
    setDevice(initialFormState);
    navigate('/devices');
  }
  const title = <h2>{device.id ? 'Edit Device' : 'Add Device'}</h2>;
  return (
    <div>
      <Header/>
      <Container style = {{ marginTop: '25px' }}>
        {title}
        <Form onSubmit={handleSubmit}>
          <FormGroup>
            <Label for="name">Name</Label>
            <Input type="text" name="name" id="name" value={device.name || ''}
              onChange={handleChange} autoComplete="name"/>
          </FormGroup>
          <FormGroup>
            <Label for="QRCode">QRCode</Label>
            <Input type="text" name="qrcode" id="QRCode" value={device.qrcode || ''}
              onChange={handleChange} autoComplete="QRCode"/>
          </FormGroup>
          <FormGroup>
            <Button color="primary" type="submit">Save</Button>{' '}
            <Button color="secondary" tag={Link} to="/devices">Cancel</Button>
          </FormGroup>
        </Form>
      </Container>
    </div>
  );
}

export default CreateDevicePage;