package esirem.com.lakaftnumrique;

//Activity_food_detail.xml , FoodDetail.java, Order.java, Database.java

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import esirem.com.lakaftnumrique.Database.Database;
import esirem.com.lakaftnumrique.Model.Food;
import esirem.com.lakaftnumrique.Model.Order;

public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    Button btnCart;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton numberButton;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_food_detail );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "Détails du produit" );
        setSupportActionBar( toolbar );

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");


        //********************************      Initialisation des vues   ******************************************************************************************************************************************
        numberButton = findViewById( R.id.number_button );
        btnCart = findViewById( R.id.btnCart );
        food_description = findViewById( R.id.food_description );
        food_name = findViewById( R.id.food_name );
        food_price = findViewById( R.id.food_price);
        food_image = findViewById( R.id.img_food );

        collapsingToolbarLayout = findViewById( R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance( R.style.ExpandedAppbar );
        collapsingToolbarLayout.setCollapsedTitleTextAppearance( R.style.CollapsedAppbar );
        //********************************************************************************************************************************************************************************************************

        //On récupère notre FoodId depuis notre Intent transférer ici dans Home.class
        if(getIntent() != null){

            foodId = getIntent().getStringExtra( "FoodId" );

            if(!foodId.isEmpty()){

                getDetailFood(foodId);
            }

        }

        //***********************************    Click sur le bouton Panier    *************************************************************************************************************
        btnCart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On instancie appelle la méthode addToCart qui prend en paramètre une classe de type "Order",
                // sachant que la classe Order a un constructeur qui prend des éléments en paramètre
                // tels que l'Id du produit, le nom du produit courant, la quantité choisie, le prix , la remise
                new Database( getBaseContext() ).addToCart( new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ) );

               String nombre = numberButton.getNumber().toString();
                String nom = currentFood.getName().toString();

                Toast.makeText( FoodDetail.this, nombre +" "+ nom + " ajouté(s) dans le panier", Toast.LENGTH_LONG ).show();



            }
        } );
        //****************************************************************************************************************************************************************************




    }

    //----------------------------------------- METHODE POUR AFFICHER LES DONNEES DANS NOTRE LAYOUT DEPUIS LA BASE DE DONNEE  ---------------------------------------------------------------------------------------------------------

    private void getDetailFood(String foodId) {

        foods.child( foodId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //On récupère les valeurs de a classe Food.class
                currentFood = dataSnapshot.getValue( Food.class);

                //set Image
                Picasso.get().load( currentFood.getImage() ).into( food_image );

                collapsingToolbarLayout.setTitle( currentFood.getName() );
                //prix
                food_price.setText( currentFood.getPrice() );
                //nom de l'aliment
                food_name.setText( currentFood.getName());
                //description de l'aliment
                food_description.setText( currentFood.getDescription() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
