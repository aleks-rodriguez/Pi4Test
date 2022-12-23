import React, { useEffect, useContext } from 'react';
import * as Yup from 'yup';

import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';
import { useNavigate } from 'react-router-dom';
import { useFormik } from 'formik';

import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';

import SubmitButton from '../../../WideUsedComponents/SubmitButton';
import UserContext from '../../../Contexts/UserContext';
import { fetchBack } from '../../../Services/FetchService';

function SignUp() {
  const { updateUserAuth, updateHeader } = useContext(UserContext);
  const navigate = useNavigate();
  const { t, i18n } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    updateHeader('SignUp.title', 'SignUp.subtitle');
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
    onSubmit: (values) => {
      const headers = new Headers({
        'Content-type': 'application/json',
        'Accept-Language': i18n.language,
      });
      const options = {
        method: 'POST',
        body: JSON.stringify(values),
        headers,
      };
      fetchBack('auth/new', options)
        .then(async (res) => {
          if (res.ok) {
            const jwt = await res.json();
            updateUserAuth(jwt.token);
            enqueueSnackbar(t('SignUp.okMessage'), { variant: 'success' });
            navigate('/projects');
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

export default SignUp;
