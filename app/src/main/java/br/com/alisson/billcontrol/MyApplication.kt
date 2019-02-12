package br.com.alisson.billcontrol

import android.app.Activity
import android.app.Application
import android.widget.Toast
import br.com.alisson.billcontrol.configs.FirebaseConfiguration
import br.com.alisson.billcontrol.preferences.PreferencesConfig
import br.com.alisson.billcontrol.utils.MD5
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        val pass = MD5.md5("Bill#App")
        val email = "billcontrol@app.com"

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
                }
            }
        }else{
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

}