package esirem.com.lakaftnumrique.Authentification;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import esirem.com.lakaftnumrique.ActivityBDE;
import esirem.com.lakaftnumrique.Home;
import esirem.com.lakaftnumrique.R;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener  {

    private boolean IsViewed = false;

    private TextView mTitreAppli, mforgotPassword_Btn;
    private Button facebook_btn, google_btn, sim_btn, view_btn, signIn_btn;
    private ImageView mLogo, OU, IconEmail, IconPassword;
    private EditText editPassword, editEmail;
    private LinearLayout SignUpOptions;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_launcher );

        //on initilaise notre firebaseAuth dévlrée là haut, en vue de l'authentification à travers firebase
        firebaseAuth = FirebaseAuth.getInstance();

        mTitreAppli = (TextView) findViewById( R.id.titre_appli );
        facebook_btn = findViewById( R.id.facebook_signIn );
        google_btn = findViewById( R.id.google_signIn );
        sim_btn = findViewById( R.id.sim_signIn );
        view_btn = findViewById( R.id.view_password );
        signIn_btn = findViewById( R.id.signin_btn );
        mLogo = findViewById( R.id.logo );
        OU = findViewById( R.id.ou );
        IconEmail = findViewById( R.id.icon_email );
        IconPassword = findViewById( R.id.icon_password );
        editEmail = findViewById( R.id.edit_email);
        editPassword = findViewById( R.id.edit_password );
        SignUpOptions = findViewById( R.id.layout_signUp );
        mforgotPassword_Btn = (TextView) findViewById( R.id.resetPassword );

        progressDialog = new ProgressDialog(  this);

        if(firebaseAuth.getCurrentUser() != null){
            //Si l'utilisateur est déja connecté voici ce qui se passe
            //on cherche l'ID de l'utilisateur courant
            mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( firebaseAuth.getCurrentUser().getUid());
            Toast.makeText( LauncherActivity.this, "la Kafèt vous dit Bonjour", Toast.LENGTH_SHORT ).show();
            Intent mainIntent = new Intent(LauncherActivity.this, ActivityBDE.class);
            //quand on appuie sur retour ça quitte l'app si on est coonnecté
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }

        //Appel de procédures de clics
        facebook_btn.setOnClickListener( this );
        google_btn.setOnClickListener( this );
        sim_btn.setOnClickListener( this );
        view_btn.setOnClickListener( this );
        signIn_btn.setOnClickListener( this );
        mforgotPassword_Btn.setOnClickListener( this);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/BRUSHSCI.ttf");
        mTitreAppli.setTypeface(face);

    }


    //--------------------------------------------------------   PROCEDURES/METHODES/FONCTIONS  --------------------------------------------------------------------------------------------------------------------------


    //procédure privée pour les champs à remplir afin d'authentifier l'utilisateur
    private void registerUser(){

        Pattern aerobase = Pattern.compile( "@" );
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


        if (Mot_de_passe.length() < 8){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            // si le champs du nom d'user est vide
            Toast.makeText(this, "le mot de passe doit être supérieur à 8 lettres", Toast.LENGTH_SHORT).show();
            //arreter l'execution de la fonction
            return;
        }

        //Si il n'ya pas de lettres miniscules
        if (!lowerCase.matcher( Mot_de_passe ).find()){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            Toast.makeText(this, "Vous devez insérer des lettres miniscules", Toast.LENGTH_SHORT).show();
            return;
        }

        //Si il n'ya pas de lettres majuscules
        if (!upperCase.matcher( Mot_de_passe ).find()){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            Toast.makeText(this, "Vous devez insérer des lettres majuscules", Toast.LENGTH_SHORT).show();
            return;
        }

        //Si il n'ya pas de chiffres
        if (!digitcase.matcher( Mot_de_passe ).find()){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            Toast.makeText(this, "Vous devez insérer au moins un chiffre", Toast.LENGTH_SHORT).show();
            return;
        }
        //si la validation est ok
        //nous aurons d'abord une apparition d'une barre de progression
        progressDialog.setMessage("La Kafèt traite vos données, patientez...");
        //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(Email,Mot_de_passe)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            //Lorsque l'utilisateur s'isncrit, les données suivantes lui sont assignées...genre statut par defaut, nom d'utilisateur qu'il a inscrit, photo (par défaut) etc
                            FirebaseUser current_user = firebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            //On veut créer une données Token lorsque l'utilisateur s'inscrit, ceci pour les notifications (Firebase function)
                            //device token est le jeton qui permettra d'envoyer un élément sur le téléphone d'un utilisateur
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            Map userMap = new HashMap<>();
                            userMap.put( "IsStaff", false );
                            userMap.put("nom", "default");
                            userMap.put( "prenom", "default" );
                            userMap.put("password", Mot_de_passe);
                            userMap.put("phone_number", "default");
                            userMap.put( "email", Email );
                            userMap.put( "sexe", "default" );
                            userMap.put("image", "default");
                            userMap.put( "device_token", deviceToken );

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // l'utilisateur est bien enregistré et vous êtes connecté
                                        //nous allons commencer l'activité de profil ici
                                        //maintenant, nous allons afficher un toast uniquement
                                        Toast.makeText(LauncherActivity.this, "Enregistrement réussi!", Toast.LENGTH_SHORT).show();
                                        progressDialog.hide();
                                        startActivity( new Intent( LauncherActivity.this, Home.class ) );
                                        /*Intent mainIntent = new Intent(LauncherActivity.this, MainActivity.class);
                                        //quand on appuie sur retour ça quitte l'app si on est coonnecté
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);*/
                                        finish();
                                    }
                                }
                            });

                        }
                        else{
                            String error = task.getException().getMessage();

                                Toast.makeText(LauncherActivity.this, " Error: " +error, Toast.LENGTH_LONG).show();


                            progressDialog.hide();
                            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {

        //si view est égale au signupbutt alors on enregistre l'utilisateur (bref vous comprennez lol), genre si on observe un click sur signtubuut
        if (v == signIn_btn){
            registerUser();


        }

        if (v == facebook_btn){
            Intent confirm_facebook = new Intent (LauncherActivity.this, Confirmation_choix_facebook.class);
            startActivity( confirm_facebook );
            Toast.makeText( LauncherActivity.this, "Nous allons juste récupérer vos Noms, prénoms, email et photo de profil via (facebook)", Toast.LENGTH_LONG ).show();

        }

        if (v == google_btn){

            Intent confirm_google = new Intent (LauncherActivity.this, Confirmation_choix_google.class);
            //quand on appuie sur retour ça quitte l'app si on est coonnecté car la tâche Principale activity a été supprimé
            confirm_google.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity( confirm_google );
            Toast.makeText( LauncherActivity.this, "Nous allons juste récupérer vos Noms, prénoms, email et photo de profil via Google", Toast.LENGTH_LONG ).show();

        }

        if (v == sim_btn){

            Intent confirm_sim = new Intent (LauncherActivity.this, Confirmation_choix_sim.class);
            startActivity( confirm_sim );
            Toast.makeText( LauncherActivity.this, "Veuillez entrer votre numéro de téléphone (SIM)", Toast.LENGTH_LONG ).show();

        }

        if (v == view_btn ){

            //Affichage du mot de passe
            if(IsViewed == false) {
                view_btn.setBackgroundResource( R.drawable.view );
                editPassword.setTransformationMethod( HideReturnsTransformationMethod.getInstance() );
                IsViewed = true;

            }
            //Codage du mot de passe
            else{

                view_btn.setBackgroundResource( R.drawable.no_view );
                editPassword.setTransformationMethod( PasswordTransformationMethod.getInstance() );
                IsViewed = false;
            }

        }

        if( v == mforgotPassword_Btn){

            Intent startResetPassword = new Intent (LauncherActivity.this, ResetPasswordActivity.class);
            startActivity( startResetPassword );

        }
    }


}
