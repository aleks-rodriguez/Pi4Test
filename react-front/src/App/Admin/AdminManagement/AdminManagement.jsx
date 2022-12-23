import React, { useState, useEffect, useContext } from 'react';
import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';

import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';

import { StyledTableCell, StyledTableRow } from '../../../WideUsedComponents/CustomColumnsRows';

import UserContext from '../../../Contexts/UserContext';
import { fetchBack } from '../../../Services/FetchService';

function AdminManagement() {
  const [admins, setAdmins] = useState([]);
  const { getTokenFromBack, updateHeader } = useContext(UserContext);
  const { enqueueSnackbar } = useSnackbar();
  const { t, i18n } = useTranslation();

  useEffect(() => {
    updateHeader('AdminManagement.title', 'AdminManagement.subtitle');
    const fetchUsers = async () => {
      const jwt = await getTokenFromBack();
      const headers = new Headers({
        authorization: `Bearer ${jwt}`,
        'Content-type': 'application/json',
        'Accept-Language': i18n.language,
      });
      const options = {
        method: 'GET',
        headers,
      };
      fetchBack('users/adminManagement', options)
        .then(async (response) => {
          if (response.ok) {
            const res = await response.json();
            setAdmins(res);
          } else {
            const err = await response.json();
            enqueueSnackbar(err.error, { variant: 'error' });
          }
        })
        .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
    };
    fetchUsers();
  }, []);

  const banAccount = async (uidAccount) => {
    const jwt = await getTokenFromBack();
    const bannedUid = { bannedUid: uidAccount };

    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
      'Content-type': 'application/json',
      'Accept-Language': i18n.language,
    });
    const options = {
      method: 'POST',
      body: JSON.stringify(bannedUid),
      headers,
    };
    fetchBack('users/ban', options)
      .then(async (response) => {
        if (response.ok) {
          const newUsers = [...admins];
          const index = newUsers.findIndex((user) => user.uidAccount === uidAccount);
          newUsers[index].enabled = false;
          setAdmins(newUsers);
          enqueueSnackbar(`${t('UserManagement.banMessage')} ${newUsers[index].userName}`, { variant: 'success' });
        } else {
          const err = await response.json();
          enqueueSnackbar(err.error, { variant: 'error' });
        }
      })
      .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
  };

  const enableAccount = async (uidAccount) => {
    const jwt = await getTokenFromBack();
    const bannedUid = { bannedUid: uidAccount };

    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
      'Content-type': 'application/json',
      'Accept-Language': i18n.language,
    });
    const options = {
      method: 'POST',
      body: JSON.stringify(bannedUid),
      headers,
    };
    fetchBack('users/enable', options)
      .then(async (response) => {
        if (response.ok) {
          const newUsers = [...admins];
          const index = newUsers.findIndex((user) => user.uidAccount === uidAccount);
          newUsers[index].enabled = true;
          setAdmins(newUsers);
          enqueueSnackbar(`${t('UserManagement.unbanMessage')} ${newUsers[index].userName}`, { variant: 'success' });
        } else {
          const err = await response.json();
          enqueueSnackbar(err.error, { variant: 'error' });
        }
      })
      .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
  };

  const convertToSuperAdmin = async (uidAccount) => {
    const jwt = await getTokenFromBack();
    const newSuperAdminUid = { newSuperAdminUidAccount: uidAccount };

    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
      'Content-type': 'application/json',
      'Accept-Language': i18n.language,
    });
    const options = {
      method: 'POST',
      body: JSON.stringify(newSuperAdminUid),
      headers,
    };
    fetchBack('users/convertSuperAdmin', options)
      .then(async (response) => {
        if (response.ok) {
          const newAdmins = [...admins];
          const index = newAdmins.findIndex((user) => user.uidAccount === uidAccount);
          newAdmins[index].superAdmin = true;
          setAdmins(newAdmins);
          enqueueSnackbar(`${t('AdminManagement.convertSuperAdminMsg')} ${newAdmins[index].userName}`, { variant: 'success' });
        } else {
          const err = await response.json();
          enqueueSnackbar(err.error, { variant: 'error' });
        }
      })
      .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
  };

  return (
    <div>
      <Typography variant="h4"><strong>{t('AdminManagement.adminText')}</strong></Typography>
      <Typography variant="h5">{t('AdminManagement.adminText2')}</Typography>
      <br />
      {admins.length !== 0
        ? (
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }}>
              <TableHead>
                <StyledTableRow>
                  <StyledTableCell>{t('UserManagement.userName')}</StyledTableCell>
                  <StyledTableCell align="center">{t('UserManagement.action')}</StyledTableCell>
                </StyledTableRow>
              </TableHead>
              <TableBody>
                {admins.map((row) => (
                  <StyledTableRow
                    key={row.uidAccount}
                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                  >
                    <StyledTableCell component="th" scope="row">{row.userName}</StyledTableCell>
                    <StyledTableCell align="center">
                      {
                        row.enabled
                          ? <Button variant="contained" color="error" onClick={() => { banAccount(row.uidAccount); }}>{t('UserManagement.cancel')}</Button>
                          : <Button variant="contained" color="info" onClick={() => { enableAccount(row.uidAccount); }}>{t('UserManagement.enable')}</Button>
                      }
                      {
                        row.superAdmin
                          ? <Button variant="contained" disabled>{t('AdminManagement.superAdmin')}</Button>
                          : <Button variant="contained" color="warning" onClick={() => { convertToSuperAdmin(row.uidAccount); }}>{t('AdminManagement.convertSuperAdmin')}</Button>
                      }
                    </StyledTableCell>
                  </StyledTableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )
        : <Typography variant="h5">{t('AdminManagement.noAdmin')}</Typography> }
    </div>
  );
}

export default AdminManagement;
