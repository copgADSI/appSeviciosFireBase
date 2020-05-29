package com.example.appseviciosfirebase

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class TipoProveedor{
    BASIC
}
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bundle: Bundle?=intent.extras
        val email :String?= bundle?.getString("Correo")
        val pass :String?= bundle?.getString("Proveedor")
        Configuracion(email ?:"",pass ?:"")


        val prefs :SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefsFile),Context.MODE_PRIVATE).edit()
        prefs.putString("Correo",email)
        prefs.putString("Proveedor",pass)
        prefs.apply()





    }
    private fun Configuracion(email:String,provider:String){
       title="Inicio"

        val prefs :SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefsFile),Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        lblEmail.text=email
        lblProveedor.text=provider
       btnLogout.setOnClickListener {
           FirebaseAuth.getInstance().signOut()
           onBackPressed()
       }

    }

}
