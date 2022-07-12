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
  const [devices, setDevices] = useState([]);
  useEffect(() => {
    fetch('/api/devices', {
      credentials: 'include',
      headers : {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => setDevices(data))
  }, [] );
  const remove = async (id) => {
  }
  const deviceList = devices.map(device => {
    const userDevice = `${device.user.firstName || ''} ${device.user.lastName || ''}`;
    return <tr key={device.id}>
      <td style={{whiteSpace: 'nowrap'}}>{device.deviceName}</td>
      <td>{userDevice}</td>
      <td>{device.events.map(event => {
        return <div key={event.id}>{new Intl.DateTimeFormat('en-US', {
          year: 'numeric',
          month: 'long',
          day: '2-digit'
        }).format(new Date(event.date))}: {event.title}</div>
      })}</td>
      <td>
        <ButtonGroup>
          <Button size="sm" color="primary" tag={Link} to={"/" + device.id}>Edit</Button>
          <Button size="sm" color="danger" onClick={() => remove(device.id)}>Delete</Button>
        </ButtonGroup>
      </td>
    </tr>
  });
  return (
    <div style={margin}>
      <Container fluid>
        <div className="float-end">
          <Button color="success" tag={Link} to="/">Add Device</Button>
        </div>
        <h3>Devices</h3>
        <Table className="mt-4">
          <thead>
          <tr>
            <th width="20%">Name</th>
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