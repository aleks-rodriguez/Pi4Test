import React, { useContext, useEffect } from 'react';
import * as Yup from 'yup';

import { useSnackbar } from 'notistack';
import { useFormik } from 'formik';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

import TextField from '@mui/material/TextField';
import Grid from '@mui/material/Grid';

import SubmitButton from '../../../WideUsedComponents/SubmitButton';
import UserContext from '../../../Contexts/UserContext';
import { fetchBack } from '../../../Services/FetchService';

function SignIn() {
  const { updateUserAuth, updateHeader } = useContext(UserContext);
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    updateHeader('SignIn.title', 'SignIn.subtitle');
  }, []);

  const validateForm = Yup.object({
    nick: Yup.string()
      .required('SignIn.required'),
    password: Yup.string()
      .required('SignIn.required'),
  });

  const formik = useFormik({
    initialValues: {
      nick: '',
      password: '',
    },
    validationSchema: validateForm,
    onSubmit: async (values) => {
      const headers = new Headers({
        'Content-type': 'application/json',
        'Accept-Language': i18n.language,
      });
      const options = {
        method: 'POST',
        body: JSON.stringify(values),
        headers,
      };
      await fetchBack('auth/login', options)
        .then(async (response) => {
          if (response.ok) {
            const responseJson = await response.json();
            updateUserAuth(responseJson.token);
            const payload = atob(responseJson.token.split('.')[1]);
            const userRole = JSON.parse(payload).roles;
            if (userRole[0] === 'ROLE_TESTER') {
              navigate('/projects');
            } else {
              navigate('/');
            }
          } else {
            const err = await response.json();
            enqueueSnackbar(err.error, { variant: 'error' });
          }
        })
        .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
    },
  });

  return (
    <div>
      <Grid container columns={24}>
        <Grid item xs={10}>
          <form onSubmit={formik.handleSubmit}>
            <TextField
              variant="standard"
              id="nick"
              name="nick"
              label={t('SignIn.userName')}
              value={formik.values.nick}
              onChange={formik.handleChange}
              error={formik.touched.nick && Boolean(formik.errors.nick)}
              helperText={t(formik.touched.nick && formik.errors.nick)}
            />
            <br />
            <br />
            <TextField
              variant="standard"
              id="password"
              name="password"
              label={t('SignIn.password')}
              type="password"
              value={formik.values.password}
              onChange={formik.handleChange}
              error={formik.touched.password && Boolean(formik.errors.password)}
              helperText={t(formik.touched.password && formik.errors.password)}
            />
            <br />
            <br />
            <SubmitButton displayedText={t('SignIn.buttonText')} />
          </form>
        </Grid>
      </Grid>
    </div>
  );
}

export default SignIn;
