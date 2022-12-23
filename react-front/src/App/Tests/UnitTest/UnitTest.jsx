import React, { useState, useContext, useEffect } from 'react';
import * as Yup from 'yup';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';
import { useFormik } from 'formik';

import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';
import LinearProgress from '@mui/material/LinearProgress';
import SaveIcon from '@mui/icons-material/Save';

import ChooseFileButton from '../../../WideUsedComponents/ChooseFileButton';
import UserContext from '../../../Contexts/UserContext';
import SubmitButton from '../../../WideUsedComponents/SubmitButton';

import { fetchBack } from '../../../Services/FetchService';

function UnitTest() {
  const [progress, setProgress] = useState(false);
  const [file, setFile] = useState(null);
  const { getTokenFromBack, updateHeader } = useContext(UserContext);
  const { enqueueSnackbar } = useSnackbar();

  const { idProject } = useParams();
  const { t } = useTranslation();

  const navigate = useNavigate();

  useEffect(() => {
    updateHeader('UnitTest.title', 'UnitTest.subtitle');
  }, []);

  const onFileChange = (e) => {
    e.preventDefault();
    const possibleFile = e.target.files[0];
    if (possibleFile.name.endsWith('.sql')) {
      setFile(possibleFile);
    } else {
      setFile(null);
      enqueueSnackbar(t('UnitTest.noSQL'), { variant: 'error' });
    }
  };

  const validateForm = Yup.object({
    dbName: Yup.string()
      .required('SignIn.required'),
  });

  const formik = useFormik({
    initialValues: {
      dbName: '',
    },

    validationSchema: validateForm,

    onSubmit: async () => {
      const jwt = await getTokenFromBack();
      const formData = new FormData();
      formData.append('dbName', formik.values.dbName);
      formData.append('sqlFile', file);
      formData.append('projectUid', idProject);

      const headers = new Headers({
        authorization: `Bearer ${jwt}`,
      });

      const options = {
        method: 'POST',
        body: formData,
        headers,
      };

      setProgress(true);
      if (file !== null) {
        enqueueSnackbar(t('UnitTest.testingStart'), { variant: 'info' });
        fetchBack('test/runUnit', options)
          .then(async (res) => {
            if (res.ok) {
              navigate('/projects');
            } else {
              const err = await res.json();
              enqueueSnackbar(err.error, { variant: 'error' });
            }
          })
          .catch(() => {
            enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' });
          })
          .finally(() => setProgress(false));
      } else {
        setProgress(false);
        enqueueSnackbar(t('UnitTest.noFile'), { variant: 'warning' });
      }
    },
  });

  return (
    <div>
      {progress
        ? (
          <>
            <LinearProgress color="warning" />
            <br />
          </>
        )
        : null}
      <div>
        <Grid container columns={24}>
          <Grid item xs={10}>
            <form onSubmit={formik.handleSubmit}>
              <TextField
                variant="standard"
                id="dbName"
                name="dbName"
                label={t('UnitTest.dbName')}
                value={formik.values.dbName}
                onChange={formik.handleChange}
                error={formik.touched.dbName && Boolean(formik.errors.dbName)}
                helperText={t(formik.touched.dbName && formik.errors.dbName)}
              />
              <div>
                <ChooseFileButton onChangeCallback={onFileChange} displayedText={t('Upload.chooseButton')} />
                <SubmitButton displayedText={t('UnitTest.start')} />
              </div>
            </form>
          </Grid>
          <Grid item xs={10}>
            <Typography variant="h5">{t('UnitTest.dbNameText')}</Typography>
            <br />
            <Typography variant="h5">{t('UnitTest.dumpText')}</Typography>
          </Grid>
        </Grid>
        {file != null
          ? (
            <div>
              <br />
              <SaveIcon />
              <Typography variant="h6">
                <strong>{t('Upload.selectedFile')}</strong>
                {file.name}
              </Typography>
            </div>
          ) : null}
      </div>
    </div>
  );
}

export default UnitTest;
