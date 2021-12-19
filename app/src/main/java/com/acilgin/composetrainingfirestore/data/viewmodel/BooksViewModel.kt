package com.acilgin.composetrainingfirestore.data.viewmodel

import androidx.lifecycle.ViewModel
import com.acilgin.composetrainingfirestore.repo.BooksRepo

class BooksViewModel(val bookRepo: BooksRepo) : ViewModel() {
    fun getBooksInfo(category: String) = bookRepo.getBookDetails(category = category)

}