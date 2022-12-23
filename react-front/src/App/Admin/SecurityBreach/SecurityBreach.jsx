/* eslint-disable react/jsx-one-expression-per-line */
import React, { useState, useContext, useEffect } from 'react';
import * as Yup from 'yup';

import { useTranslation } from 'react-i18next';
import { useFormik } from 'formik';
import { useSnackbar } from 'notistack';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogTitle from '@mui/material/DialogTitle';
import TextField from '@mui/material/TextField';
import TableContainer from '@mui/material/TableContainer';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import Grid from '@mui/material/Grid';

import Delete from '@mui/icons-material/Delete';
import SubmitButton from '../../../WideUsedComponents/SubmitButton';

import UserContext from '../../../Contexts/UserContext';
import { fetchBack } from '../../../Services/FetchService';

function SecurityBreach() {
  const [openDialog, setDialog] = useState({ status: false, messageUid: '' });
  const [advices, setAdvices] = useState([]);

  const { getTokenFromBack, updateHeader } = useContext(UserContext);
  const { enqueueSnackbar } = useSnackbar();
  const { t, i18n } = useTranslation();

  useEffect(() => {
    const checkMessages = () => {
      const options = {
        method: 'GET',
        'Accept-Language': i18n.language,
      };

      fetchBack('admin/message/get', options)
        .then(async (res) => {
          if (res.ok) {
            const json = await res.json();
            setAdvices(json);
          }
        });
    };
    checkMessages();
    updateHeader('SecurityBreach.title', 'SecurityBreach.subtitle');
  }, []);

  const validateForm = Yup.object({
    spMessage: Yup.string()
      .required('SignIn.required'),
    enMessage: Yup.string()
      .required('SignIn.required'),
  });

  const formik = useFormik({
    initialValues: {
      title: '',
      spMessage: '',
      enMessage: '',
    },
    validationSchema: validateForm,

    onSubmit: async (values) => {
      const jwt = await getTokenFromBack();
      const headers = new Headers({
        authorization: `Bearer ${jwt}`,
        'Accept-Language': i18n.language,
        'Content-type': 'application/json',
      });
      const options = {
        method: 'POST',
        body: JSON.stringify(values),
        headers,
      };
      fetchBack('admin/message/save', options)
        .then(async (res) => {
          if (res.ok) {
            const newAdvice = await res.json();
            const refreshedAdvices = advices.concat(newAdvice);
            setAdvices(refreshedAdvices);
            enqueueSnackbar(t('SecurityBreach.okMessage'), { variant: 'success' });
            formik.resetForm();
          } else {
            const err = await res.json();
            enqueueSnackbar(err.error, { variant: 'error' });
          }
        })
        .catch(() => {
          enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' });
        });
    },
  });

  const deleteMessage = async (uid) => {
    const jwt = await getTokenFromBack();
    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
    });

    const options = {
      method: 'DELETE',
      headers,
    };
    fetchBack(`admin/message/delete/${uid}`, options)
      .then(async (res) => {
        if (res.ok) {
          const index = advices.findIndex((p) => p.uidMessage === uid);
          advices.splice(index, 1);
          setDialog({ status: false, uidMessage: '' });
          enqueueSnackbar(t('SecurityBreach.deletedMessage'), { variant: 'success' });
        } else {
          const err = await res.json();
          enqueueSnackbar(err.error, { variant: 'error' });
        }
      })
      .catch(() => {
        enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' });
      });
  };

  return (
    <>
      <Typography variant="h4"><strong>{t('SecurityBreach.advices')}</strong></Typography>

      {advices.length === 0
        ? <Typography variant="h5">{t('SecurityBreach.nothing')}</Typography>
        : advices.map((row) => (
          <TableContainer component={Paper}>
            <Table key={row.uidMessage}>
              <tr>
                <td>
                  <Typography bgcolor="black" color="white" variant="h5">{row.title}</Typography>
                  <Typography bgcolor="lightgrey">{t('SecurityBreach.spMessage')}: {row.spMessage}</Typography>
                  <Typography bgcolor="lightgray">{t('SecurityBreach.enMessage')}: {row.enMessage}</Typography>
                  <Button variant="contained" color="error" onClick={() => { setDialog({ status: true, messageUid: row.uidMessage }); }}>
                    <Delete />
                    {t('SecurityBreach.modalReset')}
                  </Button>
                </td>
              </tr>
            </Table>
            <br />
          </TableContainer>
        ))}

      <Dialog open={openDialog.status} onClose={() => { setDialog({ status: false, uidMessage: '' }); }}>
        <DialogTitle>
          {t('SecurityBreach.modalTitle')}
        </DialogTitle>
        <DialogActions>
          <Button autoFocus onClick={() => { setDialog({ status: false, uidMessage: '' }); }}>{t('SecurityBreach.modalCancel')}</Button>
          <Button color="error" onClick={() => deleteMessage(openDialog.messageUid)}>{t('SecurityBreach.modalReset')}</Button>
        </DialogActions>
      </Dialog>
      <br />
      <div>
        <Typography variant="h5"><strong>{t('SecurityBreach.newAdvice')}</strong></Typography>
        <Grid container columns={24}>
          <Grid item xs={10}>
            <form onSubmit={formik.handleSubmit}>
              <TextField
                variant="standard"
                id="title"
                name="title"
                label={t('SecurityBreach.adviceTitle')}
                fullWidth
                value={formik.values.title}
                onChange={formik.handleChange}
                error={formik.touched.title && Boolean(formik.errors.title)}
                helperText={t(formik.touched.title && formik.errors.title)}
              />

              <TextField
                variant="standard"
                id="spMessage"
                name="spMessage"
                label={t('SecurityBreach.spMessage')}
                multiline
                maxRows={Infinity}
                fullWidth
                value={formik.values.spMessage}
                onChange={formik.handleChange}
                error={formik.touched.spMessage && Boolean(formik.errors.spMessage)}
                helperText={t(formik.touched.spMessage && formik.errors.spMessage)}
              />

              <TextField
                variant="standard"
                id="enMessage"
                name="enMessage"
                label={t('SecurityBreach.enMessage')}
                multiline
                maxRows={Infinity}
                fullWidth
                value={formik.values.enMessage}
                onChange={formik.handleChange}
                error={formik.touched.enMessage && Boolean(formik.errors.enMessage)}
                helperText={t(formik.touched.enMessage && formik.errors.enMessage)}
              />
              <SubmitButton displayedText={t('SecurityBreach.submit')} />
            </form>
          </Grid>
        </Grid>
      </div>
    </>
  );
}

export default SecurityBreach;
