package esirem.com.lakaftnumrique.Authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import esirem.com.lakaftnumrique.R;

public class Confirmation_choix_sim extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 7117; // n'importe quel numéro que l'on veut
    //déclaration de notre provider (fournisseur de service)
    List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_confirmation_choix_sim );

        //on initilaise notre firebaseAuth dévlrée là haut, en vue de l'authentification à travers firebase
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(  this);

        //Initialisation de notre provider (fournisseur de service)
        providers = Arrays.asList(

                new AuthUI.IdpConfig.PhoneBuilder().build() //Phone Builder

        );

        showsignInOptions();

    }

    //Procédure qui permettra d'afficher les boutons pour les fournisseurs de services
    private void showsignInOptions() {

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders( providers )
                        .setTheme( R.style.MyTheme ).build(), MY_REQUEST_CODE

        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == MY_REQUEST_CODE){

            IdpResponse response = IdpResponse.fromResultIntent( data );

            //Si le resultat est OK
            if(resultCode == RESULT_OK){

                //nous aurons d'abord une apparition d'une barre de progression
                progressDialog.setMessage("La Kafèt traite vos données, patientez...");
                //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                //Lorsque l'utilisateur s'isncrit, les données suivantes lui sont assignées...genre statut par defaut, nom d'utilisateur qu'il a inscrit, photo (par défaut) etc
                FirebaseUser current_user = firebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();

                //On veut créer une données Token lorsque l'utilisateur s'inscrit, ceci pour les notifications (Firebase function)
                //device token est le jeton qui permettra d'envoyer un élément sur le téléphone d'un utilisateur
                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                final String number = current_user.getPhoneNumber().toString().trim();

                Map userMap = new HashMap<>();
                userMap.put( "IsStaff", false );
                userMap.put("nom", "default");
                userMap.put( "prenom", "default" );
                userMap.put("password", "default");
                userMap.put("phone_number", number);
                userMap.put( "email", "default" );
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
                            Toast.makeText(Confirmation_choix_sim.this, "Enregistrement réussi!", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            Intent Info = new Intent (Confirmation_choix_sim.this, ActivityInfo.class );
                            startActivity( Info );

                                        /*Intent mainIntent = new Intent(LauncherActivity.this, MainActivity.class);
                                        //quand on appuie sur retour ça quitte l'app si on est coonnecté
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);*/
                            finish();
                        }
                    }
                });
                //Afficher l'email dans un Toast
                //Toast.makeText( this, "" + user.getEmail(), Toast.LENGTH_SHORT ).show();


            }else{ //Si le résultat n'est pas OK

                Toast.makeText( this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT ).show();
            }
        }
    }
}
