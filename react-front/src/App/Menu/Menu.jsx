import React, { useState, useContext } from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Drawer from '@mui/material/Drawer';
import Typography from '@mui/material/Typography';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import IconButton from '@mui/material/IconButton';
import TranslateIcon from '@mui/icons-material/Translate';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import HomeIcon from '@mui/icons-material/Home';
import PersonAddOutlinedIcon from '@mui/icons-material/PersonAddOutlined';
import PermIdentityOutlinedIcon from '@mui/icons-material/PermIdentityOutlined';
import AddToQueueOutlinedIcon from '@mui/icons-material/AddToQueueOutlined';
import WorkOutlineIcon from '@mui/icons-material/WorkOutline';
import ErrorOutlineOutlinedIcon from '@mui/icons-material/ErrorOutlineOutlined';
import LogoutIcon from '@mui/icons-material/Logout';
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts';

import UserContext from '../../Contexts/UserContext';

function Menu() {
  const { t, i18n } = useTranslation();
  const [lang, setLang] = useState(i18n.language);
  const { getUserInfo, logout } = useContext(UserContext);

  const { role, userName } = getUserInfo;
  const navigate = useNavigate();
  const drawerWidth = 240;

  const handleLangChange = () => {
    if (lang === 'en') {
      i18n.changeLanguage('es');
      setLang('es');
    } else {
      i18n.changeLanguage('en');
      setLang('en');
    }
  };

  const onLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
      }}
    >
      <Toolbar />
      <Box sx={{ overflow: 'auto' }}>
        <div>
          <nav>
            <Link to="/" style={{ textDecoration: 'none' }}>
              <ListItem>
                <ListItemIcon>
                  <HomeIcon />
                </ListItemIcon>
                <ListItemText>{t('Menu.home')}</ListItemText>
              </ListItem>
              <Outlet />
            </Link>
          </nav>
        </div>
        {
          (role === null)
            ? (
              <div>
                <nav>
                  <Link to="/users/signup" style={{ textDecoration: 'none' }}>
                    <ListItem>
                      <ListItemIcon>
                        <PersonAddOutlinedIcon />
                      </ListItemIcon>
                      <ListItemText>{t('Menu.signup')}</ListItemText>
                    </ListItem>
                    <Outlet />
                  </Link>
                  <Link to="/users/signin" style={{ textDecoration: 'none' }}>
                    <ListItem>
                      <ListItemIcon>
                        <PermIdentityOutlinedIcon />
                      </ListItemIcon>
                      <ListItemText>{t('Menu.signin')}</ListItemText>
                    </ListItem>
                    <Outlet />
                  </Link>
                </nav>
              </div>
            )
            : null
        }
        {
          (role !== null && role === 'TESTER')
            ? (
              <div>
                <nav>
                  {(role != null) && (
                    <Link to="/projects" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <WorkOutlineIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.projects')}</ListItemText>
                      </ListItem>
                      <Outlet />
                    </Link>
                  )}
                  {(role != null) && (
                    <Link to="/uploadProject" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <AddToQueueOutlinedIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.upload')}</ListItemText>
                      </ListItem>
                    </Link>
                  )}

                  {(role != null) && (
                    <Link to="/users/myProfile" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <PermIdentityOutlinedIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.myProfile')}</ListItemText>
                      </ListItem>
                    </Link>
                  )}
                </nav>
              </div>
            )
            : null
        }
        {
          (role != null && role === 'ADMIN')
            ? (
              <div>
                <nav>
                  {(role != null) && (
                    <Link to="/admin/newAdmin" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <PersonAddOutlinedIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.newAdmin')}</ListItemText>
                      </ListItem>
                      <Outlet />
                    </Link>
                  )}

                  {(role != null) && (
                    <Link to="/admin/security" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <ErrorOutlineOutlinedIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.breach')}</ListItemText>
                      </ListItem>
                    </Link>
                  )}

                  {(role != null) && (
                    <Link to="/admin/statistics" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <LeaderboardIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.stats')}</ListItemText>
                      </ListItem>
                    </Link>
                  )}

                  {(role != null) && (
                    <Link to="/users/myProfile" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <PermIdentityOutlinedIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.myProfile')}</ListItemText>
                      </ListItem>
                    </Link>
                  )}

                  {(role != null) && (
                    <Link to="/admin/userManagement" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <ManageAccountsIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.userManagement')}</ListItemText>
                      </ListItem>
                    </Link>
                  )}

                  {(role != null) && (
                    <Link to="/admin/adminManagement" style={{ textDecoration: 'none' }}>
                      <ListItem>
                        <ListItemIcon>
                          <ManageAccountsIcon />
                        </ListItemIcon>
                        <ListItemText>{t('Menu.adminManagement')}</ListItemText>
                      </ListItem>
                    </Link>
                  )}

                </nav>
              </div>
            )
            : null
        }
        <div>
          <IconButton component="span" onClick={handleLangChange}>
            <TranslateIcon />
            <Typography variant="subtitle1">{t('Menu.translate')}</Typography>
          </IconButton>
        </div>
        {(role != null) && (
          <div>
            <IconButton component="span" onClick={() => onLogout()}>
              <LogoutIcon />
              <Typography variant="subtitle1">{t('Menu.logout')}</Typography>
            </IconButton>
          </div>
        )}
        {(userName != null) && (
          <Typography textAlign="center" variant="subtitle1"><strong>{userName}</strong></Typography>
        )}
      </Box>
    </Drawer>
  );
}

export default Menu;
