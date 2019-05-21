package br.com.alisson.billcontrol

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.support.v4.content.ContextCompat
import android.widget.Toast
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.data.dao.FirebaseDao
import br.com.alisson.billcontrol.data.models.ObBill
import br.com.alisson.billcontrol.data.models.ObUser
import br.com.alisson.billcontrol.data.models.toDto
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.services.BillsService
import br.com.alisson.billcontrol.services.broadcasts.BillBroadcast
import br.com.alisson.billcontrol.utils.*
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyApplication : Application() {

    companion object {
        const val TAG = "MyApplication"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        var appContext: Context? = null

        @Volatile
        lateinit var applicationHandler: Handler

        private lateinit var connectivityManager: ConnectivityManager

        @Volatile
        private var applicationInitiated = false

        @Volatile
        var isConnected = false

        @Volatile
        var serviceOn = false

        fun startMessageService() {
            try {
                ContextCompat.startForegroundService(appContext!!, Intent(appContext!!, BillsService::class.java))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appContext?.startService(Intent(appContext, BillsService::class.java))
                }

            } catch (ignore: Throwable) {
            }
        }

        fun checkConnection() {
            isConnected = connectivityManager.activeNetworkInfo != null
        }

        fun postInitApplication() {
            if (applicationInitiated) {
                return
            }
            applicationInitiated = true

            try {
                connectivityManager =
                    MyApplication.appContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkStateReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        try {
                            checkConnection()
                        } catch (ignore: Throwable) {
                        }
                    }
                }
                val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                MyApplication.appContext!!.registerReceiver(networkStateReceiver, filter)
                checkConnection()

            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        lateinit var singleton: MyApplication
    }


    var sp: PreferencesConfig? = null
        get() {
            if (field == null)
                field = PreferencesConfig(appContext!!)

            return field
        }

    override fun onCreate() {
        try {
            appContext = applicationContext
        } catch (ignore: Throwable) {
        }
        super.onCreate()

        if (appContext == null) {
            appContext = applicationContext
        }
        applicationHandler = Handler(appContext!!.mainLooper)

        Utilities.runOnUIThread(Runnable { MyApplication.startMessageService() })

        singleton = this
    }

    fun loginOrConnectFirebase(email: String, pass: String) {
//        val pass = MD5.md5("Bill#App")
//        val email = "billcontrol@app.com"

        val auth = firebaseAuth()
        connectToFirebase(auth, email, pass)
    }

    fun firebaseAuth() = FirebaseConfiguration.getFirebaseAuth()


    fun createUserOnFirebase(
        auth: FirebaseAuth,
        obUser: ObUser,
        callback: (Boolean) -> Unit
    ) {
        val passMd5 = MD5.md5(obUser.password)
        auth.createUserWithEmailAndPassword(obUser.email, passMd5).addOnCompleteListener {
            if (it.isSuccessful) {
                val id = it.result!!.user.uid
                sp!!.setUserAuthId(id)
                sp!!.setUserEmail(obUser.email)
                sp!!.setUserPassword(obUser.password)

                val user = obUser.toDto()
                user.id = id
                FirebaseDao.insert(user)
                callback(true)
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

                callback(false)
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun connectToFirebase(auth: FirebaseAuth, email: String, pass: String) {
        connectToFirebase(auth, email, pass) {

        }
    }

    fun connectToFirebase(auth: FirebaseAuth, email: String, pass: String, callback: (ObUser?) -> Unit) {
        val passMd5 = MD5.md5(pass)
        auth.signInWithEmailAndPassword(email, passMd5).addOnCompleteListener {
            if (it.isSuccessful) {
                val id = it.result!!.user.uid
                sp!!.setUserAuthId(id)
                sp!!.setUserEmail(email)
                sp!!.setUserPassword(pass)
                callback(null)
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
                callback(ObUser(null, "", email, pass))
            }
        }
    }

    fun getBillOnFirebase() {
        val bills = ArrayList<ObBill>()
        val reference = FirebaseConfiguration.getFirebaseDatabase()
            .child(Consts.FIREBASE_BILL)
            .child(PreferencesConfig(this).getUserAuthId()).orderByChild("expirationDate")

        reference
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    BillBroadcast.notify(MyApplication.appContext!!, BillBroadcast.ACTION_DATABASE_CHANGE, null, null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    bills.clear()
                    for (data in p0.children) {
                        val bill = data.getValue(ObBill::class.java)
                        if (bill != null)
                            bills.add(bill)
                    }

                    CacheObBils.set(bills)

                    BillBroadcast.notify(MyApplication.appContext!!, BillBroadcast.ACTION_DATABASE_CHANGE, null, null)
                }
            })

    }


}