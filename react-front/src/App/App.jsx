// eslint-disable react/jsx-no-constructed-context-values */
import React, { useEffect, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { SnackbarProvider } from 'notistack';

import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';

import Body from './Body/Body';
import Header from './Headers/Header';
import Menu from './Menu/Menu';

import UserContext from '../Contexts/UserContext';
import HeaderContext from '../Contexts/HeaderContext';

import { getTokenFromAPI, fetchLogout } from '../Services/FetchService';

function App() {
  // User data global state
  const [userName, setUserName] = useState(null);
  const [jwt, setJwt] = useState(null);
  const [role, setRole] = useState(null);
  const [superAdmin, setSuperAdmin] = useState(null);

  // Header Component global state
  const [headerInfo, setHeaderInfo] = useState({ title: '', subtitle: '', secondarySubtitle: '' });

  const navigate = useNavigate();
  const getUserInfo = ({ userName, role, superAdmin });

  // Functions to handle user data and authetication
  useEffect(() => {
    if (jwt != null) {
      const payload = atob(jwt.split('.')[1]);
      setUserName(JSON.parse(payload).sub);
      const userRole = JSON.parse(payload).roles;
      const isSuperAdmin = Boolean(JSON.parse(payload).superAdmin);

      setSuperAdmin(isSuperAdmin);
      if (userRole[0] === 'ROLE_TESTER') {
        setRole('TESTER');
      }
      if (userRole[0] === 'ROLE_ADMIN') {
        setRole('ADMIN');
      }
    }
  }, [jwt]);

  const getTokenFromBack = async () => {
    const token = await getTokenFromAPI(jwt);
    if (token !== jwt) {
      setJwt(token);
    }
    return token;
  };

  const logout = async () => {
    const token = await getTokenFromBack();
    const res = await fetchLogout(token);
    if (res) {
      setUserName(null);
      setJwt(null);
      setRole(null);
      navigate('/');
    }
  };

  // Functions to update the States
  const updateUserAuth = (token) => {
    setJwt(token);
  };
  const updateHeader = (title, subtitle, secondarySubtitle) => {
    setHeaderInfo({ title, subtitle, secondarySubtitle });
  };

  // Contexts
  const userContextData = {
    getTokenFromBack, getUserInfo, logout, updateUserAuth, updateHeader,
  };
  const headerContextData = { headerInfo };

  return (
    <div>
      <Box sx={{ display: 'flex' }}>
        <CssBaseline />
        <UserContext.Provider value={userContextData}>
          <Menu />
          <Box component="main" sx={{ flexGrow: 1, paddingX: 0 }}>
            <HeaderContext.Provider value={headerContextData}>
              <Header />
            </HeaderContext.Provider>
            <Toolbar />
            <SnackbarProvider maxSnack={3}>
              <Body />
            </SnackbarProvider>
          </Box>
        </UserContext.Provider>
      </Box>
    </div>
  );
}

export default App;
