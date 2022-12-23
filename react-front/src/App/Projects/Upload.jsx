import React, { useState, useContext, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from 'notistack';

import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import LinearProgress from '@mui/material/LinearProgress';
import SaveIcon from '@mui/icons-material/Save';

import SubmitButton from '../../WideUsedComponents/SubmitButton';
import ChooseFileButton from '../../WideUsedComponents/ChooseFileButton';
import UserContext from '../../Contexts/UserContext';
import { fetchBack } from '../../Services/FetchService';

function Upload() {
  const [file, setFile] = useState(null);
  const [progress, setProgress] = useState(false);
  const { getTokenFromBack, updateHeader } = useContext(UserContext);

  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();

  const onFileChange = (e) => {
    const possibleFile = e.target.files[0];
    if (possibleFile !== undefined && possibleFile.name.endsWith('.zip')) {
      setFile(possibleFile);
    } else if (possibleFile !== undefined) {
      document.getElementById('inputFile').value = '';
      enqueueSnackbar(t('Upload.notZip'), { variant: 'error' });
    }
  };

  useEffect(() => {
    updateHeader('Upload.title', 'Upload.subtitle');
  }, []);

  const onFileSubmit = async () => {
    const jwt = await getTokenFromBack();
    const formData = new FormData();
    if (file == null) {
      enqueueSnackbar(t('Upload.fileNotSelected'), { variant: 'error' });
    } else {
      formData.append('file', file);
      const headers = new Headers({
        authorization: `Bearer ${jwt}`,
        'Accept-Language': i18n.language,

      });
      const options = {
        method: 'POST',
        body: formData,
        headers,
      };
      setProgress(true);
      fetchBack('project/upload', options)
        .then(async (res) => {
          if (!res.ok) {
            setProgress(false);
            const err = await res.json();
            enqueueSnackbar(err.error, { variant: 'error' });
          } else {
            setProgress(false);
            const project = await res.json();
            navigate('/projects', { state: { uploadProject: project } });
          }
        })
        .catch(() => {
          setProgress(false);
          enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' });
        });
    }
  };

  return (
    <div>
      {progress
        ? (
          <div>
            <LinearProgress color="warning" />
          </div>
        )
        : null}
      <Grid container columns={24}>
        <Grid item xs={10}>
          <ChooseFileButton onChangeCallback={(e) => { onFileChange(e); }} displayedText={t('Upload.chooseButton')} />
          <SubmitButton onClickCallback={() => { onFileSubmit(); }} displayedText={t('Upload.submitButton')} />

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

        </Grid>
        <Grid item xs={10}>
          <Typography variant="h5">{t('Upload.zipText')}</Typography>
          <br />
          <Typography variant="h5">{t('Upload.resourceLocation')}</Typography>
          <br />
          <Typography variant="h5">{t('JMeterTest.acceptedZipsText')}</Typography>
          <br />
          <Typography variant="h5">{t('Misc.zipMaxSize')}</Typography>
        </Grid>
      </Grid>
    </div>
  );
}
export default Upload;
