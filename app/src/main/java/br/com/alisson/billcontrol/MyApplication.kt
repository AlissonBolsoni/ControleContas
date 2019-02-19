package br.com.alisson.billcontrol

import android.app.Application
import android.widget.Toast
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.data.models.ObBill
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.utils.CacheObBils
import br.com.alisson.billcontrol.utils.Consts
import br.com.alisson.billcontrol.utils.MD5
import br.com.alisson.billcontrol.utils.ServiceUtils
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        val sp = PreferencesConfig(this)
        if (sp.isEnableNotification())
            ServiceUtils.startService(this)

        loginOrConnectFirebase()

        getBillOnFirebase()

    }

    private fun loginOrConnectFirebase() {
        val pass = MD5.md5("Bill#App")
        val email = "billcontrol@app.com"

        val auth = FirebaseConfiguration.getFirebaseAuth()

        connectToFirebase(auth, email, pass)

//        if (auth.currentUser == null) {
//            createUserOnFirebase(auth, email, pass)
//        } else {
//            connectToFirebase(auth, email, pass)
//        }
    }

    private fun createUserOnFirebase(
        auth: FirebaseAuth,
        email: String,
        pass: String
    ) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val id = it.result!!.user.uid
                PreferencesConfig(this).setUserAuthId(id)
            } else {
                val msg: String
                try {
                    throw it.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    msg = "Senha muito fraca"
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    msg = "Email inválido"
                } catch (e: FirebaseAuthUserCollisionException) {
                    msg = "Usuário já existe"
                } catch (e: Exception) {
                    msg = "Não foi possível cadastrar o usuário"
                    e.printStackTrace()
                }

                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                connectToFirebase(auth, email, pass)
            }
        }
    }

    private fun connectToFirebase(auth: FirebaseAuth, email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val id = it.result!!.user.uid
                PreferencesConfig(this).setUserAuthId(id)
            } else {
                val msg: String
                try {
                    throw it.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    msg = "Usuário não existe"
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    msg = "Senha errada"
                } catch (e: Exception) {
                    msg = "Não foi possível conectar o usuário"
                    e.printStackTrace()
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                createUserOnFirebase(auth, email, pass)
            }
        }
    }

    private fun getBillOnFirebase(){
        val bills = ArrayList<ObBill>()
        val reference = FirebaseConfiguration.getFirebaseDatabase()
            .child(Consts.FIREBASE_BILL)
            .child(PreferencesConfig(this).getUserAuthId()).orderByChild("expirationDate")

        reference
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    bills.clear()
                    for (data in p0.children) {
                        val bill = data.getValue(ObBill::class.java)
                        if (bill != null)
                            bills.add(bill)
                    }

                    CacheObBils.set(bills)

                    BillBroadcast.notify(this@MyApplication, BillBroadcast.ACTION_DATABASE_CHANGE, null, null)
                }
            })
    }

}