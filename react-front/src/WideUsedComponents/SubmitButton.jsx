import React from 'react';
import Button from '@mui/material/Button';
import PublishIcon from '@mui/icons-material/Publish';
import PropTypes from 'prop-types';

function SubmitButton({ onClickCallback, displayedText }) {
  return (
    <div>
      <Button
        variant="contained"
        color="info"
        startIcon={<PublishIcon />}
        onClick={onClickCallback}
        type="submit"
      >
        {displayedText}
      </Button>
    </div>
  );
}

SubmitButton.propTypes = {
  onClickCallback: PropTypes.func,
  displayedText: PropTypes.string,
};

SubmitButton.defaultProps = {
  onClickCallback: () => { },
  displayedText: '',
};

export default SubmitButton;
