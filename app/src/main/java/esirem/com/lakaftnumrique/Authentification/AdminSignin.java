package esirem.com.lakaftnumrique.Authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import esirem.com.lakaftnumrique.Home;
import esirem.com.lakaftnumrique.R;

public class AdminSignin extends AppCompatActivity implements View.OnClickListener {

    private TextView mTitreAppli, mforgotPassword_Btn;
    private EditText editPassword, editEmail;
    private Button  signIn_btn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase, mUsersDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_signin );

        //on initilaise notre firebaseAuth dévlrée là haut, en vue de l'authentification à travers firebase
        firebaseAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = mDatabase.child( "Users" );

        mTitreAppli = (TextView) findViewById( R.id.titre_appli );
        signIn_btn = findViewById( R.id.signin_btn );
        editEmail = findViewById( R.id.edit_email);
        editPassword = findViewById( R.id.edit_password );
        mforgotPassword_Btn = (TextView) findViewById( R.id.resetPassword );

        progressDialog =  new ProgressDialog( this );

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/BRUSHSCI.ttf");
        mTitreAppli.setTypeface(face);

        mforgotPassword_Btn.setOnClickListener( this );
        signIn_btn.setOnClickListener( this );
    }

    //--------------------------------------------------------   PROCEDURES/METHODES/FONCTIONS  --------------------------------------------------------------------------------------------------------------------------


    //procédure privée pour les champs à remplir afin d'authentifier l'utilisateur
    private void SignInUser(){

        Pattern aerobase = Pattern.compile( "@" );
        Pattern code = Pattern.compile( "AdminDeLaKafet" );
        //Lettres majuscules
        Pattern upperCase = Pattern.compile( "[A-Z]" );
        //Lettres miniscules
        Pattern lowerCase = Pattern.compile( "[a-z]" );
        //chiffres
        Pattern digitcase = Pattern.compile( "[0-9]" );

        final String Email = editEmail.getText().toString().trim();
        final String Mot_de_passe = editPassword.getText().toString().trim();

        //On crée une variable pour la vibration
        final Vibrator vibrator = (Vibrator) getSystemService( VIBRATOR_SERVICE );

        //On crée une variable de type long qu'on appelle pattern (objet)
        final long[] pattern = {200, 300}; //Pause pendant 200 millisecondes (0.2 sec) et vibre pendant 300 milliseconds


        //Si le champs d'email n'est pas vide
        if(!TextUtils.isEmpty(Email)){
            //Et si il n'ya pas "@"
            if(!aerobase.matcher( Email ).find()){
                vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
                Toast.makeText(this, "L'email doit contenir le symbole @", Toast.LENGTH_SHORT).show();
                return;

            }
        }
        if(!TextUtils.isEmpty( Mot_de_passe )){
            if(!code.matcher( Mot_de_passe ).find()){
                vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
                Toast.makeText(this, "Le code Administrateur est faux!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (TextUtils.isEmpty(Email)){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            //si le champ de l'Email est vide
            Toast.makeText(this, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show();
            //arreter l'execution de la fonction
            return;
        }
        if (TextUtils.isEmpty(Mot_de_passe)){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            //si le champ du mot de passe est vide
            Toast.makeText(this, "Veuillez entrer votre Mot de passe", Toast.LENGTH_SHORT).show();
            //arreter l'execution de la fonction
            return;
        }


        //si la validation est ok
        //nous aurons d'abord une apparition d'une barre de progression
        progressDialog.setMessage("La Kafèt traite vos données, patientez...");
        //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(Email,Mot_de_passe)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){


                            //utilisateur courant
                            String current_user_id = firebaseAuth.getCurrentUser().getUid();
                            //On veut créer une données Token lorsque l'utilisateur se connecte, ceci pour les notifications (Firebase function)
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            mUsersDatabase.child( current_user_id ).child( "IsStaff" ).setValue( true );

                            mUsersDatabase.child( current_user_id ).child( "device_token" ).setValue( deviceToken ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                //si la donnée "device_token a bien été crée" alors:
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    //démarrer l'activité de profil
                                    Intent mainIntent = new Intent(AdminSignin.this, Home.class);

                                    //quand on appuie sur retour ça quitte l'app si on est coonnecté
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            } );



                        }else{
                            String error = task.getException().getMessage();
                            if(error.equalsIgnoreCase( "The password is invalid or the user does not have a password." )){
                                Toast.makeText(AdminSignin.this, " Le mot de passe du compte "  + Email + " est incorrect , veuillez recommencer ", Toast.LENGTH_LONG).show();
                            }
                            else if(error.equalsIgnoreCase( "There is no user record corresponding to this identifier. The user may have been deleted." )){
                                Toast.makeText(AdminSignin.this, " L'email entré est incorrect ou inexistant ", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(AdminSignin.this, " Error: " +error, Toast.LENGTH_LONG).show();
                            }
                            progressDialog.hide();
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {

        if(v == signIn_btn){
            SignInUser();
        }

        if (v == mforgotPassword_Btn){

            Intent in = new Intent( AdminSignin.this, ResetPasswordActivity.class );
            startActivity( in );
            finish();
        }

    }
}
