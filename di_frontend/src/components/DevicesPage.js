import React, { useState, useEffect } from 'react';
import { useCookies } from 'react-cookie';
import Header from './Header.js';
import { Link } from 'react-router-dom';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';



const DevicesPage = () => {
  return (
    <div>
      <Header />
      <Devices/>
    </div>
  );
}

const margin = {
  marginTop: '25px'
}

const Devices = () => {
  const [cookies] = useCookies(['XSRF-TOKEN']);
  const [devices, setDevices] = useState([]);
  useEffect(() => {
    fetch('/api/devices', {
      credentials: 'include',
      headers : {
        'X-XSRF-TOKEN': cookies['XSRF-TOKEN'],
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => setDevices(data))
  }, [cookies, setDevices] );
  const remove = async (id) => {
    await fetch(`/api/devices/${id}`, {
      credentials: 'include',
      method: 'DELETE',
      headers: {
        'X-XSRF-TOKEN': cookies['XSRF-TOKEN'],
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    }).then(() => {
      let updatedDevices = [...devices].filter(i => i.id !== id);
      setDevices(updatedDevices);
    });
  }
  const deviceList = devices.map(device => {
    const userDevice = device.user ? `${device.user.firstName || ''} ${device.user.lastName || ''}` : '';
    return <tr key={device.id}>
      <td style={{whiteSpace: 'nowrap'}}>{device.name}</td>
      <td style={{whiteSpace: 'nowrap'}}>{device.qrcode}</td>
      <td>{userDevice}</td>
      <td>
        <ButtonGroup>
          <Button size="sm" color="primary" tag={Link} to={"/devices/" + device.id}>Edit</Button>
          <Button size="sm" color="danger" onClick={() => remove(device.id)}>Delete</Button>
        </ButtonGroup>
      </td>
    </tr>
  });
  return (
    <div style={margin}>
      <Container fluid>
        <div className="float-end">
          <Button color="success" tag={Link} to="/devices/new">Add Device</Button>
        </div>
        <h3>Devices</h3>
        <Table className="mt-4">
          <thead>
          <tr>
            <th width="20%">Name</th>
            <th width="20%">QR</th>
            <th width="20%">Location</th>
          </tr>
          </thead>
          <tbody>
          {deviceList}
          </tbody>
        </Table>
      </Container>
    </div>
  );
}

export default DevicesPage;