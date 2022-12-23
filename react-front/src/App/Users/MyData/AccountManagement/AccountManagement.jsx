/* eslint-disable react/prop-types */
import React, { useState, useContext } from 'react';
import * as Yup from 'yup';

import { useTranslation } from 'react-i18next';
import { useFormik } from 'formik';
import { useSnackbar } from 'notistack';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import Typography from '@mui/material/Typography';
import DialogActions from '@mui/material/DialogActions';
import DialogTitle from '@mui/material/DialogTitle';
import TextField from '@mui/material/TextField';

import { fetchBack } from '../../../../Services/FetchService';
import UserContext from '../../../../Contexts/UserContext';

import SubmitButton from '../../../../WideUsedComponents/SubmitButton';

function AccountManagement({ isSuperAdmin }) {
  const [edit, setEdit] = useState(false);
  const [openDialog, setDialog] = useState(false);

  const { getTokenFromBack, logout } = useContext(UserContext);
  const { t, i18n } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();

  const deleteAccount = async () => {
    const jwt = await getTokenFromBack();
    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
      'Content-type': 'application/json',
      'Accept-Language': i18n.language,
    });
    const options = {
      method: 'DELETE',
      headers,
    };
    fetchBack('users/delete/', options)
      .then(async (response) => {
        if (response.ok) {
          setDialog(false);
          enqueueSnackbar(t('ProjectList.okDelete'), { variant: 'success' });
          logout();
        }
      })
      .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
  };

  const validateForm = Yup.object({
    password: Yup.string()
      .required('SignIn.required')
      .min(8, 'SignIn.minPass')
      .matches('^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z]).*', {
        message: 'SignIn.passMatch',
        excludeEmptyString: false,
      }),
    repeatPass: Yup.string().required('SignIn.required')
      .oneOf([Yup.ref('password'), null], 'MyData.passNotEqual'),
  });

  const formik = useFormik({
    initialValues: {
      password: '',
      repeatPass: '',
    },
    validationSchema: validateForm,
    onSubmit: async (values) => {
      const jwt = await getTokenFromBack();
      const headers = new Headers({
        'Content-type': 'application/json',
        authorization: `Bearer ${jwt}`,
      });
      const options = {
        method: 'POST',
        body: JSON.stringify(values),
        headers,
      };
      fetchBack('auth/changePass', options)
        .then(async (res) => {
          if (res.ok) {
            enqueueSnackbar(t('AccountManagement.passChanged'), { variant: 'success' });
            setEdit(false);
          }
        })
        .catch(() => {
          enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' });
        });
    },
  });

  return (
    <div>
      {
        edit
          ? (
            <form onSubmit={formik.handleSubmit}>
              <TextField
                id="password"
                name="password"
                variant="standard"
                type="password"
                label={t('AccountManagement.pass')}
                value={formik.values.password}
                onChange={formik.handleChange}
                error={formik.touched.password && Boolean(formik.errors.password)}
                helperText={t(formik.touched.password && formik.errors.password)}
              />
              {'\n'}
              <TextField
                id="repeatPass"
                name="repeatPass"
                variant="standard"
                type="password"
                label={t('MyData.repeatPass')}
                value={formik.values.repeatPass}
                onChange={formik.handleChange}
                error={formik.touched.repeatPass && Boolean(formik.errors.repeatPass)}
                helperText={t(formik.touched.repeatPass && formik.errors.repeatPass)}
              />
              <SubmitButton displayedText={t('AccountManagement.buttonSave')} />
              <Button
                variant="contained"
                color="error"
                onClick={() => { setEdit(false); }}
              >
                {t('AccountManagement.modalCancel')}
              </Button>
            </form>
          )
          : (
            <div>
              <Button variant="outlined" color="info" onClick={() => { setEdit(true); }}>
                {t('AccountManagement.buttonEdit')}
              </Button>
              {isSuperAdmin
                ? (
                  <>
                    <Typography variant="h4">{t('AccountManagement.isSuperAdminText')}</Typography>
                    <Typography variant="h4">{t('AccountManagement.isSuperAdminText2')}</Typography>
                  </>
                )
                : (
                  <Button variant="contained" color="error" onClick={() => { setDialog(true); }}>
                    {t('AccountManagement.deleteAcc')}
                  </Button>
                )}
              <Dialog open={openDialog} onClose={() => { setDialog(false); }}>
                <DialogTitle>
                  {t('AccountManagement.modalTitle')}
                </DialogTitle>
                <DialogActions>
                  <Button color="info" onClick={() => { setDialog(false); }}>{t('AccountManagement.modalCancel')}</Button>
                  <Button color="error" onClick={() => deleteAccount()} autoFocus>{t('AccountManagement.deleteAcc')}</Button>
                </DialogActions>
              </Dialog>
            </div>
          )
      }
    </div>
  );
}

export default AccountManagement;
