package br.com.alisson.billcontrol

import android.app.Application
import android.widget.Toast
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.utils.MD5
import br.com.alisson.billcontrol.utils.ServiceUtils
import com.google.firebase.auth.*

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        val sp = PreferencesConfig(this)
        if (sp.isEnableNotification())
            ServiceUtils.startService(this)

        val pass = MD5.md5("Bill#App")
        val email = "billcontrol@app.com"
        val email2 = "billcontrol@app.com"

        val auth = FirebaseConfiguration.getFirebaseAuth()
        if (auth.currentUser == null) {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    val id = it.result!!.user.uid
                    PreferencesConfig(this).setUserAuthId(id)
                } else {
                    var msg = ""
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
        }else{
            connectToFirebase(auth, email, pass)
        }

    }

    private fun connectToFirebase(auth: FirebaseAuth, email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val id = it.result!!.user.uid
                PreferencesConfig(this).setUserAuthId(id)
            } else {
                var msg = ""
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
            }
        }
    }

}