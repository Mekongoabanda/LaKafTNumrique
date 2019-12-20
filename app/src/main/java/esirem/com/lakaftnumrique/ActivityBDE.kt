package esirem.com.lakaftnumrique

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import esirem.com.lakaftnumrique.Authentification.LauncherActivity

class ActivityBDE : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var btnLogout: Button? = null
    private val dialog: Dialog? = null
    private var progressdialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bde)
        btnLogout = findViewById(R.id.btn_logout)
        mAuth = FirebaseAuth.getInstance()
        //pour notre progressDialog déclarée là haut
        progressdialog = ProgressDialog(this)

        btnLogout!!.setOnClickListener(View.OnClickListener {
            //si la déconnexion est ok
//nous aurons d'abord une apparition d'une barre de progression
            progressdialog!!.setMessage("Déconnexion  en cours...")
            progressdialog!!.show()
            mAuth!!.signOut()
            //Déconnexion pour pour nos providers---------------------------------------------------DEBUT
            AuthUI.getInstance()
                    .signOut(this@ActivityBDE).addOnCompleteListener {
                        progressdialog!!.hide()
                        startActivity(Intent(this@ActivityBDE, LauncherActivity::class.java))
                        progressdialog!!.setMessage("Vous êtes déconnectés")
                        progressdialog!!.show()
                        finish()
                    }.addOnFailureListener { }
        })
    }
}