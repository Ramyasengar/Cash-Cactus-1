package com.example.cashcactus.data.remote

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveUserData(userId: String, data: Map<String, Any>) {
        db.collection("users")
            .document(userId)
            .set(data)
    }

    fun deleteUserDocument(userId: String) {
        db.collection("users").document(userId).delete()
    }
}