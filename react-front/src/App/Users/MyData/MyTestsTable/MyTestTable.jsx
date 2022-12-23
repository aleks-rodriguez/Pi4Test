/* eslint-disable react/prop-types */
import React from 'react';
import { useTranslation } from 'react-i18next';

import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import { StyledTableCell, StyledTableRow } from '../../../../WideUsedComponents/CustomColumnsRows';

function MyTestTable({ tests }) {
  const { t } = useTranslation();

  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }}>
        <TableHead>
          <StyledTableRow>
            <StyledTableCell>{t('MyTestTable.individualTestTitle')}</StyledTableCell>
            <StyledTableCell align="right">{t('MyTestTable.testType')}</StyledTableCell>
            <StyledTableCell align="right">{t('MyTestTable.elapsedTime')}</StyledTableCell>
            <StyledTableCell align="right">{t('MyTestTable.executedAt')}</StyledTableCell>
          </StyledTableRow>
        </TableHead>
        <TableBody>
          {tests.map((row) => (
            <StyledTableRow
              key={row.testId}
              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >
              <StyledTableCell component="th" scope="row">
                {row.projectTitle}
              </StyledTableCell>
              <StyledTableCell align="right">{row.type === 'FUNCTIONAL' ? t('MyTestTable.functional') : t('MyTestTable.performance')}</StyledTableCell>
              <StyledTableCell align="right">{row.elapsedTime}</StyledTableCell>
              <StyledTableCell align="right">{row.executedAt}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

export default MyTestTable;
