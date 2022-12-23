import React, { useState, useContext, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useLocation } from 'react-router-dom';
import { useSnackbar } from 'notistack';
import {
  Typography, List, AccordionSummary, AccordionDetails, ListItemAvatar,
  ListItemText, Avatar, ListItem, Accordion, Divider, Button, Dialog, DialogTitle, DialogActions,
}
  from '@mui/material';
import WorkIcon from '@mui/icons-material/Work';
import ExpandMore from '@mui/icons-material/ExpandMore';
import Delete from '@mui/icons-material/Delete';
import SettingsSuggestIcon from '@mui/icons-material/SettingsSuggest';
import FileDownloadIcon from '@mui/icons-material/FileDownload';

import { fetchBack } from '../../Services/FetchService';
import UserContext from '../../Contexts/UserContext';

function ProjectList() {
  const [projects, setProjects] = useState([]);
  const [openDialog, setDialog] = useState({ status: false, projectUid: '' });

  const { getTokenFromBack, updateHeader } = useContext(UserContext);
  const { t, i18n } = useTranslation();

  const location = useLocation();
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();

  // Redirección tras la subida de un proyecto
  useEffect(() => {
    updateHeader('ProjectList.title', 'ProjectList.subtitle');
    if (location.state != null) {
      const { uploadProject } = location.state;
      setProjects(...projects, uploadProject);
    }
  }, []);

  useEffect(() => {
    const fetchUserProjects = async () => {
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
      fetchBack('project/list', options)
        .then(async (response) => {
          if (response.ok) {
            const titles = await response.json();
            const count = Object.keys(titles);
            if (count.length !== 0) {
              setProjects(titles);
            }
          } else {
            const err = await response.json();
            if (response.status.toString.startsWith('4')) {
              enqueueSnackbar(err.error, { variant: 'error' });
            } else if (response.status.toString.startsWith('5')) {
              enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' });
            }
          }
        })
        .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
    };
    fetchUserProjects();
  }, []);

  const deleteProject = async (uid) => {
    const jwt = await getTokenFromBack();
    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
      'Content-type': 'application/json',
    });
    const options = {
      method: 'DELETE',
      headers,
    };
    fetchBack(`project/delete/${uid}`, options)
      .then(async (response) => {
        if (response.ok) {
          const index = projects.findIndex((p) => p.uid === uid);
          projects.splice(index, 1);
          setDialog({ status: false, projectUid: '' });
          enqueueSnackbar(t('ProjectList.okDelete'), { variant: 'success' });
        }
      })
      .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
  };

  const downloadTests = async (uid, title) => {
    const jwt = await getTokenFromBack();
    const headers = new Headers({
      authorization: `Bearer ${jwt}`,
      'Content-type': 'application/json',
    });
    const options = {
      method: 'GET',
      headers,
    };
    fetchBack(`test/download/${uid}`, options)
      .then(async (response) => {
        const res = await response.blob();
        return res;
      })
      .then((blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${title}-tests.zip`;
        document.body.appendChild(a);
        a.click();
        a.remove();
      })
      .catch(() => { enqueueSnackbar(t('Misc.fetchError'), { variant: 'error' }); });
  };

  return (
    <div>
      {Object.keys(projects).length === 0
        ? <Typography variant="h4"><strong>{t('ProjectList.emptyList')}</strong></Typography>
        : projects.map((p) => (
          <>
            <List key={p.uid}>
              <Accordion key={p.uid}>
                <AccordionSummary expandIcon={<ExpandMore />}>
                  <ListItem key={p.uid}>
                    <ListItemAvatar>
                      <Avatar><WorkIcon /></Avatar>
                    </ListItemAvatar>
                    <ListItemText primary={`${p.title}`} secondary={`${p.description}`} />
                  </ListItem>
                </AccordionSummary>

                <AccordionDetails key={p.uid}>
                  <Typography sx={{ fontWeight: 'bold', fontStyle: 'italic' }}>{t('ProjectList.actions')}</Typography>
                  <Button onClick={() => { setDialog({ status: true, projectUid: p.uid }); }}>
                    <Delete />
                    {t('ProjectList.deleteProject')}
                  </Button>

                  {p.unitTestExecuted === true || p.performanceTestExecuted === true
                    ? (
                      <Button onClick={() => downloadTests(p.uid, p.title)}>
                        <FileDownloadIcon />
                        {t('ProjectList.downloadTests')}
                      </Button>
                    )
                    : null}
                </AccordionDetails>
                <Divider />

                <AccordionDetails>
                  <Typography sx={{ fontWeight: 'bold', fontStyle: 'italic' }}>{t('ProjectList.tests')}</Typography>

                  <Button onClick={() => navigate(`/test/unit/${p.uid}`)}>
                    <SettingsSuggestIcon />
                    {t('ProjectList.runMavenTest')}
                  </Button>
                  {p.unitTestExecuted === true
                    ? (
                      <Button onClick={() => navigate(`/test/jmeter/${p.uid}`)}>
                        <SettingsSuggestIcon />
                        {t('ProjectList.runPerformanceTest')}
                      </Button>
                    )
                    : (
                      <Typography>{t('ProjectList.unitTestRequired')}</Typography>
                    )}

                </AccordionDetails>
                <Divider />

              </Accordion>
            </List>

            <Dialog open={openDialog.status} onClose={() => { setDialog({ status: false, projectUid: '' }); }}>
              <DialogTitle>
                {t('ProjectList.modalTitle')}
              </DialogTitle>
              <DialogActions>
                <Button onClick={() => { setDialog({ status: false, projectUid: '' }); }}>{t('ProjectList.modalCancel')}</Button>
                <Button color="error" onClick={() => deleteProject(openDialog.projectUid)} autoFocus>{t('ProjectList.deleteProject')}</Button>
              </DialogActions>
            </Dialog>
          </>
        ))}
    </div>
  );
}
export default ProjectList;
