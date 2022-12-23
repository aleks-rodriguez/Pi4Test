import React, { useContext, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from 'notistack';

import { useFormik } from 'formik';
import * as Yup from 'yup';

import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Grid from '@mui/material/Grid';

import SubmitButton from '../../../WideUsedComponents/SubmitButton';
import UserContext from '../../../Contexts/UserContext';
import { fetchBack } from '../../../Services/FetchService';

function AdminSignIn() {
  const { getTokenFromBack, updateHeader } = useContext(UserContext);
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();
  const { t, i18n } = useTranslation();

  useEffect(() => {
    updateHeader('AdminSignIn.title', 'AdminSignIn.subtitle');
  }, []);

  const validateForm = Yup.object({
    nick: Yup.string()
      .required('SignIn.required')
      .min(2, 'SignUp.minChar')
      .max(20, 'SignUp.maxChar'),
    password: Yup.string()
      .required('SignIn.required')
      .min(8, 'SignUp.minPass')
      .matches('^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z]).*', {
        message: 'SignUp.passMatch',
        excludeEmptyString: false,
      }),
    repeatPass: Yup.string().required('SignIn.required')
      .oneOf([Yup.ref('password'), null], 'MyData.passNotEqual'),
  });

  const formik = useFormik({
    initialValues: {
      nick: '',
      password: '',
      repeatPass: '',
    },
    validationSchema: validateForm,
    onSubmit: async (values) => {
      const jwt = await getTokenFromBack();
      const headers = new Headers({
        'Content-type': 'application/json',
        'Accept-Language': i18n.language,
        authorization: `Bearer ${jwt}`,
      });
      const options = {
        method: 'POST',
        body: JSON.stringify(values),
        headers,
      };
      await fetchBack('admin/users/newAdmin', options)
        .then(async (res) => {
          if (res.ok) {
            enqueueSnackbar(t('SignIn.okMessage'), { variant: 'success' });
            navigate('/');
          } else {
            const err = await res.json();
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
            <SubmitButton displayedText={t('SignUp.button')} />
          </form>
        </Grid>
        <Grid item xs={10}>
          <Typography variant="h3"><strong>{t('SignUp.userText')}</strong></Typography>
          <Typography variant="body1">{t('SignUp.userText2')}</Typography>
          <Typography variant="h3"><strong>{t('SignUp.passText')}</strong></Typography>
          <Typography variant="body1">{t('SignUp.passText2')}</Typography>
        </Grid>
      </Grid>
    </div>
  );
}

export default AdminSignIn;
