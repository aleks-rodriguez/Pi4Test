import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert, AlertTitle, List, Typography,
} from '@mui/material';

import { Link } from 'react-router-dom';
import { fetchBack } from '../../Services/FetchService';

function initialComponent() {
  const [advices, setAdvices] = useState([]);
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
  }, []);

  return (
    <>
      <Alert severity="info">
        <Typography variant="h4">{t('Initial.welcome')}</Typography>
        <br />
        <Typography variant="h5">{t('Initial.info1')}</Typography>
        <Typography variant="h5">{t('Initial.info2')}</Typography>
        <br />
        <Typography variant="h6">
          {t('Initial.termsMsg')}
          <Link to="/terms">
            {t('Initial.terms')}
          </Link>
        </Typography>
      </Alert>

      {Object.keys(advices).length === 0
        ? null
        : advices.map((advice) => (
          <List key={advice.uidMessage}>
            <Alert severity="error">
              <AlertTitle />
              {i18n.language === 'es' ? <strong>{advice.spMessage}</strong> : <strong>{advice.enMessage}</strong>}

            </Alert>
          </List>
        ))}
    </>
  );
}

export default initialComponent;
