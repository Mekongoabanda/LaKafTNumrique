package esirem.com.lakaftnumrique;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 7117; // n'importe quel numéro que l'on veut
    //déclaration de notre provider (fournisseur de service)
    List<AuthUI.IdpConfig> providers;

    private Button sign_out_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        sign_out_btn = (Button) findViewById( R.id.signOut ) ;


        //Action lorsque l'on clique sur le bouton de déconnexion
        sign_out_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Déconnexion pour pour nos providers
                AuthUI.getInstance()
                        .signOut( MainActivity.this )
                        .addOnCompleteListener( new OnCompleteListener<Void>() {
                            //Si c'est un succès
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                //Bouton désactivé
                            sign_out_btn.setEnabled( false );

                            //On affiche les options d'Authentification
                                showsignInOptions();

                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            //Si c'est un échec
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        //On affiche un message d'erreur
                        Toast.makeText( MainActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT ).show();

                    }
                } );

            }
        } );

        //Initialisation de notre provider (fournisseur de service)
        providers = Arrays.asList(

                new AuthUI.IdpConfig.EmailBuilder().build(), //Email Builder
                new AuthUI.IdpConfig.PhoneBuilder().build(), //Phone Builder
                new AuthUI.IdpConfig.FacebookBuilder().build(), //Facebook Builder
                new AuthUI.IdpConfig.GoogleBuilder().build() //Google Builder
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

                //On récupère l'utilisateur courant
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //Afficher l'email dans un Toast
                Toast.makeText( this, "" + user.getEmail(), Toast.LENGTH_SHORT ).show();

                //Afficher le bouton SignOut
                sign_out_btn.setEnabled( true );


            }else{ //Si le résultat n'est pas OK

                Toast.makeText( this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT ).show();
            }
        }
    }
}
