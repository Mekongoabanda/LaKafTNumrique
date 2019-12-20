package esirem.com.lakaftnumrique;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import esirem.com.lakaftnumrique.Database.Database;
import esirem.com.lakaftnumrique.Model.Order;
import esirem.com.lakaftnumrique.Model.Request;
import esirem.com.lakaftnumrique.ViewHolder.CartAdapter;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requestDatabase, mUserDatabase;
    FirebaseUser mCurrentUser;
    String CurrentUid;

    TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>(  );
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cart );

        //Firebase
        database = FirebaseDatabase.getInstance();
        requestDatabase = database.getReference("Requests");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        CurrentUid = mCurrentUser.getUid();
        mUserDatabase = database.getReference().child( "Users" ).child( CurrentUid );

        //Initialisation
        recyclerView = (RecyclerView) findViewById( R.id.listCart );
        recyclerView.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( layoutManager );

        txtTotalPrice = (TextView) findViewById( R.id.total );
        btnPlace = findViewById( R.id.btnValidOrder );

        btnPlace.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowAlertDialog();

            }
        } );

        //Procédure pour charger la liste du panier
        LoadListFood();

    }

    //---------------------------------------------       ALERTE DIALOG      ------------------------------------------------------------------------------------------
    private void ShowAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);// création du Alertedialog
        alertDialog.setTitle( " Encore une étape s'il-vous-plaît!" ); // Text dans notre Dialog
        alertDialog.setMessage( "Veuillez renseigner votre place : Exemple: << Je assis(e) près de... >> " );

        final EditText edtAddress = new EditText(Cart.this);//Création d'un editText
        edtAddress.setHint( "Entrez votre emplacement ici" );
        edtAddress.setHintTextColor( Color.GRAY );
        LinearLayout.LayoutParams dimensions = new LinearLayout.LayoutParams( // dimensions
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        edtAddress.setLayoutParams( dimensions ); //affectation des dimensions à l'editText
        alertDialog.setView( edtAddress ); // Ajouter le l'editText à notre alerte dialog
        alertDialog.setIcon( R.drawable.shopbis ); //On met une icône

        alertDialog.setPositiveButton( "VALIDER", new DialogInterface.OnClickListener() { //Bouton positif "Valider" et action sur son click
            @Override
            public void onClick(DialogInterface dialog, int i) {

                //On cherche le nom et le numéro de tél dans la base de donnée
                mUserDatabase.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //synchroniser les champs statuts, nom d'utilisateur, image de profile entre ceux du projet et de la BD firebase
                        String nom = dataSnapshot.child("nom").getValue().toString();
                        String phone = dataSnapshot.child("phone_number").getValue().toString();

                        //Création d'une nouvelle requête
                        Request request =new Request(
                                phone,
                                nom,
                                edtAddress.getText().toString(),
                                txtTotalPrice.getText().toString(),
                                cart
                        );
                        //On envoit la requête à Firebase
                        requestDatabase.child( String.valueOf( System.currentTimeMillis() ) )
                                .setValue( request );

                        //Suppression de la carte
                        new Database( getBaseContext() ).cleanCart( );
                        Toast.makeText( Cart.this, "Merci, Commande envoyée", Toast.LENGTH_SHORT ).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );

            }
        } );

        alertDialog.setNegativeButton( "ANNULER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        } );

        alertDialog.show();
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void LoadListFood() {

     cart =  new Database(this).getCarts();
     adapter = new CartAdapter( cart,this );
     recyclerView.setAdapter( adapter );

     //Calculer le prix total
     double total = 0;
     for(Order order:cart)
         total += (Double.parseDouble( order.getPrice() ) * Double.parseDouble (order.getQuantity() )
                 - Double.parseDouble( order.getDiscount() ) * Double.parseDouble( order.getQuantity() ));


        txtTotalPrice.setText( String.format("€%s" , total ) );

    }
}
