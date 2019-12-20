package esirem.com.lakaftnumrique.Authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import esirem.com.lakaftnumrique.R;

public class ActivityInfo extends AppCompatActivity {

    TextView mTitreAppli;
    EditText edit_nom, edit_prenom, edit_email;
    Button valider_btn;
    ImageView mImage ;
    FirebaseAuth mAuth;
    DatabaseReference mUsersDatabase, mdatabase, mCurrentUserReference;
    FirebaseUser mCurrentUser;
    ProgressDialog progressDialog;
    StorageReference mImageStorage;
    String mCurrentUserId;
    //galerie photo
    public static final int GALLERY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_info );

        //Initialisation de nos UI
        mTitreAppli = (TextView) findViewById( R.id.titre_appli );
        edit_nom = (EditText) findViewById( R.id.edit_nom );
        edit_prenom = (EditText) findViewById( R.id.edit_prenom );
        edit_email = (EditText) findViewById( R.id.edit_mail );
        valider_btn = (Button) findViewById( R.id.valider_btn );
        mImage = (ImageView) findViewById( R.id.img );

        //Police du titre
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/BRUSHSCI.ttf");
        mTitreAppli.setTypeface(face);

        //Initialisaton de notre progressDialog
        progressDialog = new ProgressDialog( this );

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase  = mdatabase.child( "Users" );
        mCurrentUser = mAuth.getCurrentUser();
        mCurrentUserId = mCurrentUser.getUid();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUsersDatabase.child( mCurrentUserId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String urlImage = dataSnapshot.child( "image" ).getValue().toString();


                    Picasso.get( ).load( urlImage ).networkPolicy( NetworkPolicy.OFFLINE ).
                            placeholder( R.drawable.add_pic ).into( mImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError(Exception e) {


                            Picasso.get( ).load( urlImage ).placeholder( R.drawable.add_pic ).into( mImage );

                        }
                    } );

                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                String error = databaseError.getMessage().toString();
                Toast.makeText( ActivityInfo.this, "Erreur : " + error, Toast.LENGTH_SHORT ).show();

            }
        } );


        //todo *************************** ACTION SUR LE CLIC VAILDER *************************************
        valider_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendData();

            }
        } );

        /*
        //todo ************************* ACTION SUR LE CLIC DE L'IMAGE ***********************************
        mImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType( "image/*" );
                galleryIntent.setAction( Intent.ACTION_GET_CONTENT );

                startActivityForResult( Intent.createChooser( galleryIntent, "SELECT IMAGE" ), GALLERY_PICK );


            }
        } );
        */

    }



    //TODO : -------------------------------------- ENREGISTRER LES DONNEES DANS LA BASE DE DONNEES-----------------------------------

    private void SendData() {

        final String nom = edit_nom.getText().toString().trim();
        final String prenom = edit_prenom.getText().toString().trim();
        final String email  =edit_email.getText().toString().trim();

        Pattern aerobase = Pattern.compile( "@" );

        //On crée une variable pour la vibration
        final Vibrator vibrator = (Vibrator) getSystemService( VIBRATOR_SERVICE );

        //On crée une variable de type long qu'on appelle pattern (objet)
        final long[] pattern = {200, 300}; //Pause pendant 200 millisecondes (0.2 sec) et vibre pendant 300 milliseconds

        if(TextUtils.isEmpty( nom )){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            Toast.makeText( this, "Veuillez entrer votre nom pour continuer", Toast.LENGTH_SHORT ).show();

            return;
        }

        if(TextUtils.isEmpty( prenom )){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            Toast.makeText( this, "Veuillez entrer votre prenom pour continuer", Toast.LENGTH_SHORT ).show();
            return;
        }

        if(TextUtils.isEmpty( email )){
            vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
            Toast.makeText( this, "Veuillez entrer votre Adresse Email pour continuer", Toast.LENGTH_SHORT ).show();
            return;
        }

        //Si le champs d'email n'est pas vide
        if(!TextUtils.isEmpty(email)){
            //Et si il n'ya pas "@"
            if(!aerobase.matcher( email ).find()){
                vibrator.vibrate( pattern,-1 );// 0 signifie repéter chaque fois et -1 juste une fois
                Toast.makeText(this, "L'email doit contenir le symbole @", Toast.LENGTH_SHORT).show();
                return;

            }
        }

        //nous aurons d'abord une apparition d'une barre de progression
        progressDialog.setMessage("La Kafèt traite vos données, patientez...");
        //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

            mUsersDatabase.child( mCurrentUserId ).child( "nom" ).setValue( nom );
            mUsersDatabase.child( mCurrentUserId ).child( "prenom" ).setValue( prenom );
            mUsersDatabase.child( mCurrentUserId ).child( "email" ).setValue( email );
        Intent Home = new Intent( ActivityInfo.this, esirem.com.lakaftnumrique.Home.class );
        startActivity( Home );
        progressDialog.hide();



    }

    // TODO-------------------------------------------------METHODE POUR L'ENVOI D'IMAGE ---------------------------------------------------------------------------------------------
    // (En relation avec l'action sur le clic mChatAddBtn, plus haut dans le main)
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        //               *************************** POUR L'IMAGE *********************************

        //Si l'entrée dans la gallerie est ok et que le code de résultat est ok
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            //les images seront stockées dans le dossier messages_images
            final StorageReference filepath = mImageStorage.child( "profile_images/" ).child( mCurrentUserId + ".jpg" );

            //On affecte le lien à notre variable imageUri
            filepath.putFile( imageUri ).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        //Nous récupérons l'Url de notre image que nous stockons dans une variable de type Uri
                        filepath.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Nous mettons la valeur de Uri  dans notre String downloadUrl
                                String download_url = uri.toString();

                                Toast.makeText( ActivityInfo.this, "Image téléchargée", Toast.LENGTH_SHORT ).show();

                                    mUsersDatabase.child( mCurrentUserId ).child( "image" ).setValue( download_url );
                                progressDialog.hide();

                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                String error = exception.getMessage().toString();
                                Toast.makeText( ActivityInfo.this, "Erreur : " + error, Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                }
            } );

        }
    }
    */
}
