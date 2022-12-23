import { Typography } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
// import Alert from '@material-ui/lab/Alert';

function NotFound() {
  const { t } = useTranslation();

  return <Typography variant="h2" color="red">{t('NotFound.message')}</Typography>;
}

export default NotFound;
