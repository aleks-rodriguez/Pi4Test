import React, { useEffect, useState, useContext } from 'react';
import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';
import Typography from '@mui/material/Typography';

import { fetchBack } from '../../../Services/FetchService';
import UserContext from '../../../Contexts/UserContext';

import AccountManagement from './AccountManagement/AccountManagement';
import MyProjectTable from './MyProjectsTable/MyProjectsTable';
import MyTestTable from './MyTestsTable/MyTestTable';

function MyData() {
  const [personalData, setPersonalData] = useState({ userName: '', created: '' });
  const [projectData, setProjectData] = useState([]);
  const [testData, setTestData] = useState([]);
  const { enqueueSnackbar } = useSnackbar();
  const { getTokenFromBack, updateHeader, getUserInfo } = useContext(UserContext);
  const { t, i18n } = useTranslation();

  useEffect(() => {
    const fetchProjectData = async () => {
      const jwt = await getTokenFromBack();
      const headers = new Headers({
        authorization: `Bearer ${jwt}`,
        'Content-type': 'application/json',
        'Accept-Language': i18n.language,
      });
      const options = {
        method: 'GET',
        headers,
      };
      fetchBack('users/info', options)
        .then(async (response) => {
          if (!response.ok) {
            throw new Error();
          } else {
            const json = await response.json();
            setPersonalData({ userName: json.userName, created: json.createDate });
            setProjectData(json.projects);
            setTestData(json.tests);
          }
        })
        .catch(() => {
          enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' });
        });
    };
    updateHeader('MyData.title', 'MyData.subtitle');
    fetchProjectData();
  }, []);

  const isSuperAdmin = () => {
    let superAdmin = false;
    if (getUserInfo.superAdmin) {
      superAdmin = true;
    }
    return superAdmin;
  };

  return (
    <>
      <Typography variant="h4"><strong>{t('MyData.personalData')}</strong></Typography>
      <Typography variant="h6">
        {t('MyData.userName')}
        :
        {' '}
        {personalData.userName}
      </Typography>
      <br />
      <Typography variant="h6">
        {t('MyData.created')}
        :
        {' '}
        {personalData.created}
      </Typography>
      <br />

      <AccountManagement isSuperAdmin={isSuperAdmin()} />
      <br />

      {
        projectData.length !== 0
          ? (
            <>
              <Typography variant="h4">{t('ProjectList.title')}</Typography>
              <MyProjectTable projects={projectData} />
            </>
          ) : null
      }
      {
        testData.length !== 0
          ? (
            <>
              <br />
              <br />
              <Typography variant="h4">{t('MyData.testsTitle')}</Typography>
              <MyTestTable tests={testData} />
            </>
          ) : null
      }
    </>
  );
}
export default MyData;
