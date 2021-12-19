package com.acilgin.composetrainingfirestore.repo

import com.acilgin.composetrainingfirestore.OnSuccess
import com.acilgin.composetrainingfirestore.OneError
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


class BooksRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getBookDetails(category: String) = callbackFlow {
        val collection = firestore.collection("/books/affiliate_books/${category}")
        val snapshotListener = collection.addSnapshotListener { value, error ->
            val response = if (error == null) {
                OnSuccess(value)
            } else {
                OneError(error)
            }

            this.trySend(response).isSuccess
        }

        awaitClose {
            snapshotListener.remove()

        }

    }


}