package esirem.com.lakaftnumrique.Authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import esirem.com.lakaftnumrique.R;

public class ResetPasswordActivity extends AppCompatActivity {

     TextView mTitreAppli;
     Button btn_reset;
     EditText send_email;
     ProgressDialog progressDialog;
     FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reset_password );

        mTitreAppli = (TextView) findViewById( R.id.titre_appli );
        btn_reset =  findViewById( R.id.reset_btn );
        send_email =  findViewById( R.id.edit_email );

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(  this);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/BRUSHSCI.ttf");
        mTitreAppli.setTypeface(face);

        //On crée une variable de type long qu'on appelle pattern (objet)
        final long[] pattern = {200, 300}; //Pause pendant 200 millisecondes (0.2 sec) et vibre pendant 300 milliseconds
        //On crée une variable pour la vibration
        final Vibrator vibrator = (Vibrator) getSystemService( VIBRATOR_SERVICE );

        btn_reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = send_email.getText().toString();

                if(email.equals( "" )){

                    vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
                    Toast.makeText( ResetPasswordActivity.this, "Vous devez entrer l'email", Toast.LENGTH_SHORT ).show();

                }else{
                    //nous aurons d'abord une apparition d'une barre de progression
                    progressDialog.setMessage("La kafèt traite de votre demande, patientez...");
                    //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    firebaseAuth.sendPasswordResetEmail( email ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText( ResetPasswordActivity.this, "S'il-vous-plait, veuillez consulter vos Emails", Toast.LENGTH_LONG ).show();
                                Intent startAuth = new Intent( ResetPasswordActivity.this, LauncherActivity.class );
                                startActivity( startAuth );
                                progressDialog.hide();
                            }else{
                                progressDialog.setMessage("ERREUR !!!!!...");
                                String error = task.getException().getMessage();
                                Toast.makeText( ResetPasswordActivity.this, error, Toast.LENGTH_SHORT ).show();
                                progressDialog.setCanceledOnTouchOutside(true);

                            }

                        }
                    } );
                }

            }
        } );
    }
}
