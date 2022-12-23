import React, { useState, useContext, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import { useSnackbar } from 'notistack';

import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import LinearProgress from '@mui/material/LinearProgress';
import SaveIcon from '@mui/icons-material/Save';

import SubmitButton from '../../../WideUsedComponents/SubmitButton';
import ChooseFileButton from '../../../WideUsedComponents/ChooseFileButton';
import UserContext from '../../../Contexts/UserContext';
import { fetchBack } from '../../../Services/FetchService';

function JMeterTest() {
  const [file, setFile] = useState(null);
  const [progress, setProgress] = useState(false);
  const [jMeterPort, setJMeterPort] = useState(0);
  const { getTokenFromBack, updateHeader } = useContext(UserContext);

  const { t, i18n } = useTranslation();
  const { idProject } = useParams();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

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
    updateHeader('JMeterTest.title');
  }, []);

  useEffect(async () => {
    const jwt = await getTokenFromBack();
    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
      'Accept-Language': i18n.language,

    });
    const options = {
      method: 'GET',
      headers,
    };
    fetchBack('test/getJMeterPort/', options)
      .then(async (res) => {
        const port = await res.json();
        setJMeterPort(port);
      }).catch();
  }, []);

  const onFileSubmit = async () => {
    const jwt = await getTokenFromBack();
    const formData = new FormData();
    if (file == null) {
      enqueueSnackbar(t('Upload.fileNotSelected'), { variant: 'error' });
    } else {
      formData.append('zipFile', file);
      formData.append('projectUid', idProject);
      formData.append('deployPort', jMeterPort);

      const headers = new Headers({
        authorization: `Bearer ${jwt}`,
        'Accept-Language': i18n.language,

      });
      const options = {
        method: 'POST',
        body: formData,
        headers,
      };

      enqueueSnackbar(t('UnitTest.testingStart'), { variant: 'info' });
      setProgress(true);
      fetchBack('test/jmeter', options)
        .then(async (res) => {
          if (res.ok) {
            enqueueSnackbar(t('SignUp.okMessage'), { variant: 'success' });
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
    }
  };

  return (
    <div>
      {progress
        ? (
          <LinearProgress color="warning" />
        )
        : null}
      <Grid container columns={24}>
        <Grid item xs={10}>
          <Typography variant="h5">
            <strong>
              {t('JMeterTest.portText')}
              {jMeterPort}
            </strong>
          </Typography>
          <br />
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
          <Typography variant="h5">{t('JMeterTest.ipText')}</Typography>
          <br />
          <Typography variant="h5">{t('JMeterTest.infoText')}</Typography>
          <br />
          <Typography variant="h5">{t('JMeterTest.acceptedZipsText')}</Typography>
          <br />
          <Typography variant="h5">{t('Misc.zipMaxSize')}</Typography>
        </Grid>
      </Grid>
    </div>
  );
}
export default JMeterTest;
