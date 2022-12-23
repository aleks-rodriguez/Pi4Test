import React from 'react';
import PropTypes from 'prop-types';
import Button from '@mui/material/Button';
import FileCopy from '@mui/icons-material/FileCopy';

function ChooseFileButton({ onChangeCallback, displayedText }) {
  return (
    <div>
      <input
        id="inputFile"
        name="inputFile"
        type="file"
        hidden
        onInput={(e) => onChangeCallback(e)}
      />
      <label htmlFor="inputFile">
        <Button variant="contained" color="secondary" component="span" startIcon={<FileCopy />}>
          {displayedText}
        </Button>
      </label>
    </div>
  );
}

ChooseFileButton.propTypes = {
  onChangeCallback: PropTypes.func,
  displayedText: PropTypes.string,
};

ChooseFileButton.defaultProps = {
  onChangeCallback: () => { },
  displayedText: '',
};

export default ChooseFileButton;
