/* eslint-disable react/jsx-one-expression-per-line */
import React, { useEffect, useContext, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';

import Typography from '@mui/material/Typography';

import UserContext from '../../../Contexts/UserContext';
import { fetchBack } from '../../../Services/FetchService';

function UserStatistics() {
  const [userStats, setUserStats] = useState();
  const [projectStats, setProjectsStats] = useState();
  const [testStats, setTestStats] = useState();
  const [fetched, setFetched] = useState(false);
  const { getTokenFromBack, updateHeader } = useContext(UserContext);
  const { enqueueSnackbar } = useSnackbar();
  const { t, i18n } = useTranslation();

  useEffect(() => {
    updateHeader('UserStatistics.title');

    const fetchStatistics = async () => {
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
      fetchBack('users/statistics', options)
        .then(async (response) => {
          if (response.ok) {
            const statistics = await response.json();
            setUserStats(statistics.users);
            setProjectsStats(statistics.projects);
            setTestStats(statistics.tests);
            setFetched(true);
          } else {
            const err = await response.json();
            enqueueSnackbar(err.error, { variant: 'error' });
          }
        })
        .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
    };
    fetchStatistics();
  }, []);

  return (
    <div>
      {fetched
        ? (
          <>
            <Typography variant="h4">{t('UserStatistics.users')}</Typography>
            <Typography><strong>{t('UserStatistics.actualUsers')}:</strong> {userStats.actualRegisteredUsers == null ? '' : userStats.actualRegisteredUsers}</Typography>
            <Typography><strong>{t('UserStatistics.totalRegistered')}:</strong> {userStats.totaRegisteredUsers == null ? 0 : userStats.totaRegisteredUsers}</Typography>
            <Typography><strong>{t('UserStatistics.totalDeleted')}:</strong> {userStats.totalDeletedUsers == null || userStats.totalDeletedUsers === '' ? 0 : userStats.totalDeletedUsers}</Typography>
            <Typography><strong>{t('UserStatistics.banned')}:</strong> {userStats.bannedUsers == null || userStats.bannedUsers === '' ? '{}' : userStats.bannedUsers}</Typography>
            <br />
            <Typography variant="h4">{t('UserStatistics.projects')}</Typography>
            <Typography><strong>{t('UserStatistics.maxPerUser')}:</strong> {projectStats.maxProjectsPerUser == null ? 0 : projectStats.maxProjectsPerUser}</Typography>
            <Typography><strong>{t('UserStatistics.userWithMaxProjects')}:</strong> {projectStats.usersWithMoreProjects == null || projectStats.usersWithMoreProjects === '' ? '{}' : projectStats.usersWithMoreProjects}</Typography>
            <Typography><strong>{t('UserStatistics.minPerUser')}:</strong> {projectStats.minProjectsPerUser == null ? 0 : projectStats.minProjectsPerUser}</Typography>
            <Typography><strong>{t('UserStatistics.usersWithLessProjects')}:</strong> {projectStats.usersWithLessProjects == null || projectStats.usersWithLessProjects === '' ? '{}' : projectStats.usersWithLessProjects}</Typography>
            <Typography><strong>{t('UserStatistics.avgPerUser')}:</strong> {Number(projectStats.avgProjectsPerUser == null ? 0 : projectStats.avgProjectsPerUser)}</Typography>
            <Typography><strong>{t('UserStatistics.stdDevPerUser')}:</strong> {Number(projectStats.stdDevProjectsPerUser == null ? 0 : projectStats.stdDevProjectsPerUser)}</Typography>
            <br />
            <Typography variant="h4">{t('UserStatistics.tests')}</Typography>
            <Typography><strong>{t('UserStatistics.maxPerProject')}:</strong> {testStats.maxTestPerProject == null || testStats.maxTestPerProject === '' ? '{}' : testStats.maxTestPerProject}</Typography>
            <Typography><strong>{t('UserStatistics.projectWithMaxTests')}:</strong> {testStats.projectsWithMoreTests == null || testStats.projectsWithMoreTests === '' ? '{}' : testStats.projectsWithMoreTests}</Typography>
            <Typography><strong>{t('UserStatistics.minPerProject')}:</strong> {testStats.minTestPerProject == null ? 0 : testStats.minTestPerProject}</Typography>
            <Typography><strong>{t('UserStatistics.projectWithLessTests')}:</strong> {testStats.projectsWithLessTests == null || testStats.projectsWithLessTests === '' ? '{}' : testStats.projectsWithLessTests}</Typography>
            <Typography><strong>{t('UserStatistics.avgPerProject')}:</strong> {Number(testStats.avgTestsPerProject == null ? 0 : testStats.avgTestsPerProject)}</Typography>
            <Typography><strong>{t('UserStatistics.stdDevPerProject')}:</strong> {Number(testStats.stdDevTestPerProject == null ? 0 : testStats.stdDevTestPerProject)}</Typography>
            <Typography><strong>{t('UserStatistics.top3UserTests')}:</strong> {testStats.top3UsersTest == null || testStats.top3UsersTest === '' ? '{}' : testStats.top3UsersTest}</Typography>
          </>
        ) : null}
    </div>
  );
}

export default UserStatistics;
