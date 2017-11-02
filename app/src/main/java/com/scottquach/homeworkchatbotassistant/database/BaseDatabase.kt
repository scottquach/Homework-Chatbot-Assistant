package com.scottquach.homeworkchatbotassistant.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Scott Quach on 11/1/2017.
 */
open class BaseDatabase {
    protected val databaseReference = FirebaseDatabase.getInstance().reference
    protected val user = FirebaseAuth.getInstance().currentUser
}