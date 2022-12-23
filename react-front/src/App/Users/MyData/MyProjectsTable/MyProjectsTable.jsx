/* eslint-disable react/prop-types */
import React from 'react';
import { useTranslation } from 'react-i18next';

import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import { StyledTableCell, StyledTableRow } from '../../../../WideUsedComponents/CustomColumnsRows';

function MyProjectTable({ projects }) {
  const { t } = useTranslation();

  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }}>
        <TableHead>
          <StyledTableRow>
            <StyledTableCell>{t('MyProjectTable.projectTitle')}</StyledTableCell>
            <StyledTableCell align="right">{t('MyProjectTable.description')}</StyledTableCell>
            <StyledTableCell align="right">{t('MyProjectTable.unitTestsExecuted')}</StyledTableCell>
            <StyledTableCell align="right">{t('MyProjectTable.performanceTestsExecuted')}</StyledTableCell>
          </StyledTableRow>
        </TableHead>
        <TableBody>
          {projects.map((row) => (
            <StyledTableRow
              key={row.title}
              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >
              <StyledTableCell component="th" scope="row">
                {row.title}
              </StyledTableCell>
              <StyledTableCell align="right">{row.description}</StyledTableCell>
              <StyledTableCell align="right">{row.unitTestExecuted ? t('MyProjectTable.yes') : t('MyProjectTable.no')}</StyledTableCell>
              <StyledTableCell align="right">{row.performanceTestExecuted ? t('MyProjectTable.yes') : t('MyProjectTable.no')}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

export default MyProjectTable;
