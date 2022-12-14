import React, { useEffect, useState } from 'react';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Container } from '@mui/system';
import {
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
} from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const styles = { hidden: { display: 'none' } };

const BookAdd = () => {
  const [open, setOpen] = React.useState(false);

  const [input, setInput] = useState({
    filename: null,
    book_title: '',
    book_author: '',
    book_publisher: '',
    book_price: '',
    book_content: '',
    book_category: '',
    book_isbn: '',
  });

  const {
    filename,
    book_title,
    book_author,
    book_publisher,
    book_price,
    book_content,
    book_category,
    book_isbn,
  } = input;

  const [inputImg, setInputImg] = useState({
    imgname: '',
  });

  const [selectCategory, setSelectCategory] = React.useState('');

  const handleCategoryChange = (event) => {
    setSelectCategory(event.target.value);
    // console.log(event.target.value);
    setInput({ ...input, book_category: event.target.value });
  };

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleFileChange = (e) => {
    e.preventDefault();
    setInput({ ...input, filename: e.target.files[0] });
    setInputImg({ ...inputImg, imgname: e.target.value });
  };

  const handleValueChange = (e) => {
    let nextState = {};
    nextState[e.target.name] = e.target.value;
    setInput((prev) => {
      return { ...prev, ...nextState };
    });
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('book_title', book_title);
    formData.append('book_author', book_author);
    formData.append('book_publisher', book_publisher);
    formData.append('book_price', book_price);
    formData.append('book_content', book_content);
    formData.append('book_category', book_category);
    formData.append('book_isbn', book_isbn);
    if (filename !== null) {
      formData.append('filename', filename);
    }
    const config = { headers: { 'Content-Type': 'multipart/form-data' } };

    await axios
      .post('http://localhost:8090/books/newbooksave', formData, config)
      .then((response) => {
        setInput({
          filename: '',
          book_title: '',
          book_author: '',
          book_publisher: '',
          book_price: '',
          book_content: '',
          book_category: '',
          book_isbn: '',
        });
      })
      .catch((err) => console.error(err.message));

    setOpen(false);
    window.location.replace('/books');
  };

  return (
    <div>
      <Button
        variant='contained'
        color='primary'
        onClick={handleClickOpen}
        fullWidth
      >
        ?????? ??????
      </Button>

      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>?????? ??????</DialogTitle>
        <DialogContent>
          <input
            style={styles.hidden}
            id='raised-button-file'
            accept='image/*'
            type='file'
            name='filename'
            file={input.filename}
            // value={inputImg.imgname}
            onChange={handleFileChange}
          />
          <label htmlFor='raised-button-file'>
            <Button
              variant='contained'
              color='primary'
              component='span'
              name='filename'
            >
              {inputImg.imgname === '' ? '??? ?????? ??????' : inputImg.imgname}
              {/* ??? ?????? ?????? */}
            </Button>
          </label>
          <br />
          <TextField
            label='??????'
            variant='standard'
            type='text'
            name='book_title'
            value={input.book_title}
            onChange={handleValueChange}
          />
          <br />

          <TextField
            label='??????'
            variant='standard'
            type='text'
            name='book_author'
            value={input.book_author}
            onChange={handleValueChange}
          />
          <br />
          <TextField
            label='?????????'
            variant='standard'
            type='text'
            name='book_publisher'
            value={input.book_publisher}
            onChange={handleValueChange}
          />
          <br />
          <TextField
            label='??????'
            variant='standard'
            type='number'
            name='book_price'
            value={input.book_price}
            onChange={handleValueChange}
            placeholder='???'
          />

          <br />
          <TextField
            label='?????? ISBN'
            variant='standard'
            type='text'
            name='book_isbn'
            value={input.book_isbn}
            onChange={handleValueChange}
          />
        </DialogContent>

        <DialogContent>
          <TextField
            id='outlined-multiline-static'
            label='??? ??????'
            multiline
            rows={10}
            name='book_content'
            value={input.book_content}
            onChange={handleValueChange}
          />
          <br />
          <Box sx={{ minWidth: 120 }}>
            <FormControl sx={{ m: 1, minWidth: 120 }} size='small'>
              <InputLabel id='demo-simple-select-required-label'>
                ??????
              </InputLabel>
              <Select
                labelId='demo-simple-select-required-label'
                id='demo-simple-select-required'
                value={selectCategory}
                label='??????'
                onChange={handleCategoryChange}
              >
                <MenuItem value={1}>???</MenuItem>
                <MenuItem value={2}>????????????</MenuItem>
                <MenuItem value={3}>????????????(SF)</MenuItem>
                <MenuItem value={4}>??????/???????????? ??????</MenuItem>
                <MenuItem value={5}>?????????/????????????</MenuItem>
                <MenuItem value={6}>??????</MenuItem>
                <MenuItem value={7}>???????????????</MenuItem>
                <MenuItem value={8}>???????????????</MenuItem>
                <MenuItem value={9}>??????</MenuItem>
                <MenuItem value={10}>??????/???????????????</MenuItem>
                <MenuItem value={11}>??????/?????????</MenuItem>
                <MenuItem value={12}>??????/????????????</MenuItem>
                <MenuItem value={13}>??????/??????</MenuItem>
                <MenuItem value={14}>????????????</MenuItem>
                <MenuItem value={15}>??????/????????????</MenuItem>
                <MenuItem value={16}>????????????</MenuItem>
                <MenuItem value={17}>??????/?????????</MenuItem>
                <MenuItem value={18}>?????? ??????</MenuItem>
                <MenuItem value={19}>???????????????</MenuItem>
                <MenuItem value={20}>?????????</MenuItem>
                <MenuItem value={21}>????????????~????????????</MenuItem>
                <MenuItem value={22}>?????????</MenuItem>
                <MenuItem value={23}>?????????</MenuItem>
                <MenuItem value={24}>??????/?????????</MenuItem>
                <MenuItem value={25}>????????????</MenuItem>
                <MenuItem value={26}>??????/??????????????? ??????</MenuItem>
                <MenuItem value={27}>??????</MenuItem>
                <MenuItem value={28}>??????</MenuItem>
                <MenuItem value={29}>??????</MenuItem>
                <MenuItem value={30}>??????</MenuItem>
                <MenuItem value={31}>????????????/????????????</MenuItem>
                <MenuItem value={32}>???????????????</MenuItem>
                <MenuItem value={33}>????????????</MenuItem>
                <MenuItem value={34}>??????</MenuItem>
                <MenuItem value={35}>?????????</MenuItem>
                <MenuItem value={36}>??????</MenuItem>
                <MenuItem value={37}>?????????/????????????</MenuItem>
                <MenuItem value={38}>?????? ??????</MenuItem>
                <MenuItem value={39}>?????????/?????????</MenuItem>
                <MenuItem value={40}>?????????/????????????</MenuItem>
                <MenuItem value={41}>?????????/??????</MenuItem>
                <MenuItem value={42}>??????/??????/??????</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button
            variant='contained'
            color='primary'
            onClick={handleFormSubmit}
          >
            ??????
          </Button>
          <Button variant='contained' color='primary' onClick={handleClose}>
            ??????
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default BookAdd;
