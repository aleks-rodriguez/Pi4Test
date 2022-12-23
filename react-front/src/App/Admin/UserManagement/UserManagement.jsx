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

function UserManagement() {
  const [users, setUsers] = useState([]);
  const { getTokenFromBack, updateHeader } = useContext(UserContext);
  const { enqueueSnackbar } = useSnackbar();
  const { t, i18n } = useTranslation();

  useEffect(() => {
    updateHeader('UserManagement.title', 'UserManagement.subtitle');
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
      fetchBack('users/userManagement', options)
        .then(async (response) => {
          if (response.ok) {
            const res = await response.json();
            setUsers(res);
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
          const newUsers = [...users];
          const index = newUsers.findIndex((user) => user.uidAccount === uidAccount);
          newUsers[index].enabled = false;
          setUsers(newUsers);
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
          const newUsers = [...users];
          const index = newUsers.findIndex((user) => user.uidAccount === uidAccount);
          newUsers[index].enabled = true;
          setUsers(newUsers);
          enqueueSnackbar(`${t('UserManagement.unbanMessage')} ${newUsers[index].userName}`, { variant: 'success' });
        } else {
          const err = await response.json();
          enqueueSnackbar(err.error, { variant: 'error' });
        }
      })
      .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
  };

  return (
    <div>
      <Typography variant="h4"><strong>{t('UserManagement.usersMsg')}</strong></Typography>
      <br />
      {users.length !== 0
        ? (
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }}>
              <TableHead>
                <StyledTableRow>
                  <StyledTableCell>{t('UserManagement.userName')}</StyledTableCell>
                  <StyledTableCell align="right">{t('UserManagement.action')}</StyledTableCell>
                </StyledTableRow>
              </TableHead>
              <TableBody>
                {users.map((row) => (
                  <StyledTableRow
                    key={row.uidAccount}
                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                  >
                    <StyledTableCell component="th" scope="row">{row.userName}</StyledTableCell>
                    <StyledTableCell align="right">
                      {
                        row.enabled
                          ? <Button variant="contained" color="error" onClick={() => { banAccount(row.uidAccount); }}>{t('UserManagement.cancel')}</Button>
                          : <Button variant="contained" color="info" onClick={() => { enableAccount(row.uidAccount); }}>{t('UserManagement.enable')}</Button>
                      }
                    </StyledTableCell>
                  </StyledTableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )
        : null}
    </div>
  );
}

export default UserManagement;
