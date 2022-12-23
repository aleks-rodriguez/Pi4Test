import React, { useContext } from 'react';
import { useTranslation } from 'react-i18next';

import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';

import UserContext from '../../Contexts/UserContext';
import SubHeaderContext from '../../Contexts/HeaderContext';

function Header() {
  const { getUserInfo } = useContext(UserContext);
  const { role, userName } = getUserInfo;
  const { t } = useTranslation();

  const { headerInfo } = useContext(SubHeaderContext);
  const { title, subtitle, secondarySubtitle } = headerInfo;

  const isMainLocation = window.location.pathname === '/';
  const isTermsLocation = window.location.pathname === '/terms';

  return (
    <div>
      <AppBar color="inherit" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>

        {isMainLocation || isTermsLocation
          ? (
            <Toolbar>
              {userName == null
                ? <Typography variant="h4">{t('Header.noUser')}</Typography>
                : null}
              {userName != null && role === 'TESTER'
                ? (
                  <Typography variant="h4">
                    {t('Header.welcome')}
                    {', '}
                    {userName}
                  </Typography>
                )
                : null}
              {userName != null && role === 'ADMIN'
                ? (
                  <Typography variant="h4">
                    {t('Header.welcomeAdmin')}
                    {', '}
                    {userName}
                  </Typography>
                )
                : null}
            </Toolbar>
          )
          : (
            <Toolbar>
              <Typography variant="h4" sx={{ paddingRight: 2 }}>{t(title)}</Typography>
              {
                subtitle
                  ? <Typography variant="h5">{t(subtitle)}</Typography>
                  : null
              }
              {
                secondarySubtitle
                  ? <Typography variant="h5">{t(secondarySubtitle)}</Typography>
                  : null
              }
            </Toolbar>

          )}
      </AppBar>
    </div>
  );
}

export default Header;
