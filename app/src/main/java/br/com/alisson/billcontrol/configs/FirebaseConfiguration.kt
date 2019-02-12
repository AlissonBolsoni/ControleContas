package br.com.alisson.billcontrol.configs

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseConfiguration {

    private var databaseFirebase: DatabaseReference? = null
    private var auth: FirebaseAuth? = null

    fun getFirebaseDatabase(): DatabaseReference{

        if (databaseFirebase == null) {
            val instance = FirebaseDatabase.getInstance()
            instance.setPersistenceEnabled(true)
            databaseFirebase = instance.reference
            databaseFirebase!!.keepSynced(true)
        }

        return databaseFirebase!!
    }

    fun getFirebaseAuth(): FirebaseAuth{

        if (auth == null)
            auth = FirebaseAuth.getInstance()

        return auth!!
    }
}