package com.acilgin.composetrainingfirestore

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

sealed class BooksResponse
data class OnSuccess(val querySnapshot: QuerySnapshot?) : BooksResponse()
data class OneError(val exception: FirebaseFirestoreException?) : BooksResponse()
