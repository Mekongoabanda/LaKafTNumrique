package esirem.com.lakaftnumrique;

//FoodViewHolder.class, Food.class, FoodList.class, food_item.xml, activity_food_list.xml

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import esirem.com.lakaftnumrique.Interface.ItemClickListener;
import esirem.com.lakaftnumrique.Model.Food;
import esirem.com.lakaftnumrique.ViewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_food_list );

        //Firebase
        database = FirebaseDatabase.getInstance();
        //reference vers la table foods
        foodList = database.getReference("Food");

        //Mise en place du RecyclerView
        recyclerView = findViewById( R.id.recycler_food );
        recyclerView.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( layoutManager );

        //Nous récupérons notre intent ici, pris depuis Home.class
        if(getIntent() != null){

            categoryId = getIntent().getStringExtra( "CategoryId" );
            if (!categoryId.isEmpty() && categoryId != null ){

                loadListFood(categoryId);
            }

        }


    }

    //-------------------------------------------------------  procedure pour charger les food -------------------------------------------------------------
    private void loadListFood(String categoryId) {

        adapter =  new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                //on affiche la liste par ordre de menuId = à la category correspondante
                foodList.orderByChild( "MenuId" ).equalTo( categoryId )) { // Comme si on faisait en SQL SELECT * from Foods where MenuId =
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {

                //On charge le nom de la nourriture
                viewHolder.food_name.setText( model.getName() );
                //Ainsi que l'image
                Picasso.get().load( model.getImage() ).into( viewHolder.food_image );

                final Food local = model;
                //Action sur le click
                viewHolder.setItemClickListener( new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Lorsque l'on clique sur un aliment, on entre dans sa fiche détaillée
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        //On envoit FoodId dans la nouvelle activité
                        foodDetail.putExtra( "FoodId", adapter.getRef(position).getKey() );
                        startActivity( foodDetail );

                    }
                } );

            }
        };
        //set Adapter
        Log.d( "TAG", ""+adapter.getItemCount() );
        recyclerView.setAdapter( adapter );

    }
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
