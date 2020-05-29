package com.example.appseviciosfirebase

import android.R.attr.alertDialogTheme
import android.R.attr.name
import android.R.id
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.se.omapi.Session
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Provider

private val GOOGLEAUT=100
class AuthActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      // Obtain the FirebaseAnalytics instance.
    var analytics : FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    var bundle=Bundle()
      bundle.putString("message","Integración de  FireBase completado")
      analytics.logEvent("InitScren",bundle)

     Configuracion()
      session()

    }

    override fun onStart() {
        super.onStart()
        RelativeLayout.INVISIBLE
    }
    private fun session(){
        val prefs :SharedPreferences = getSharedPreferences(getString(R.string.prefsFile),Context.MODE_PRIVATE)
        val email :String? =prefs.getString("Correo",null)
        val proveedor :String? = prefs.getString("Proveedor",null)
        if (email!=null&& proveedor!=null ){
            RelativeLayout.INVISIBLE
            mostrarHome(email,TipoProveedor.valueOf(proveedor))
        }


    }
    private fun Configuracion(){
        title="Autentificación"
        btnRegistrar.setOnClickListener {
            if (txtEmail.text.isNotEmpty()&& txtPass.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtEmail.text.toString(),txtPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                       mostrarHome(it.result?.user?.email ?: "",TipoProveedor.BASIC)
                    }else{
                       mostrarAlerta()
                    }
                }

            }
        }
        btnGoogle.setOnClickListener {
            val googleConf :GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient :GoogleSignInClient =GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLEAUT)


        }
        btnAcceder.setOnClickListener {
            if (txtEmail.text.isNotEmpty()&& txtPass.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail.text.toString(),txtPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        mostrarHome(it.result?.user?.email ?: "",TipoProveedor.BASIC)
                    }else{
                        mostrarAlerta()
                    }
                }

            }
        }
    }
    private fun mostrarAlerta(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error al querer registarse")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog=builder.create()
        dialog.show()

    }
    private fun mostrarHome(email : String,provider:TipoProveedor){

        val homeIntent=Intent(this,HomeActivity::class.java).apply {
            putExtra("Correo",email)
            putExtra("Proveedor",provider.name)
        }
        startActivity(homeIntent)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== GOOGLEAUT){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
          try {
              val account =task.getResult(ApiException::class.java)

              if (account!=null){
                  val credential=GoogleAuthProvider.getCredential(account.idToken,null)
                  FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                      if (it.isSuccessful){
                          mostrarHome(account.email ?: "", TipoProveedor.BASIC )


                      }
                      else{
                          mostrarAlerta()
                      }
                  }

              }
          }catch (e: ApiException){
              mostrarAlerta()
          }
        }
    }
    
}
