package esirem.com.lakaftnumrique;

//MenuViewHolder.class, Home.class, Category.class, menu_item.xml, home.xml, activity_home_drawer.xml, activity_home.xml
// app_bar_home.xml, content_home.xml, nav_header_home.xml

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import esirem.com.lakaftnumrique.Authentification.LauncherActivity;
import esirem.com.lakaftnumrique.Authentification.LoadingLaunchActivity;
import esirem.com.lakaftnumrique.Interface.ItemClickListener;
import esirem.com.lakaftnumrique.Model.Category;
import esirem.com.lakaftnumrique.ViewHolder.MenuViewHolder;
import io.fabric.sdk.android.Fabric;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category, mUsersDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser mCurrentUser;
    TextView textFullName;
    CircleImageView imageView;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        Fabric.with(this, new Crashlytics());
        setContentView( R.layout.activity_home );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "Menu" );
        setSupportActionBar( toolbar );


        //Initialisation de la base de donnée
        database = FirebaseDatabase.getInstance();
        //Stockage du chemin d'accès à notre table category
        category = database.getReference( "Category" );
        //Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Utilisateur courant
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        Button fab = (Button) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
                     //   .setAction( "Action", null ).show();
                Intent cart = new Intent (Home.this, Cart.class);
                startActivity( cart );
            }
        } );


        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

//***************************************    Action si l'utilisateur est authentifié    *************************************************************************************************************
        //Afficher le nom de l'utilisateur
        // Si l'utilisateur est déjà connecté / Si il est authentifié
        View headerView = navigationView.getHeaderView( 0 );
        if (firebaseAuth.getCurrentUser() != null) {

            final String current_uid = mCurrentUser.getUid();
            mUsersDatabase = database.getReference().child( "Users" ).child( current_uid );

            textFullName = headerView.findViewById( R.id.txtFullName );
            imageView = headerView.findViewById( R.id.imageView );

            mUsersDatabase.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //synchroniser les champs statuts, nom d'utilisateur, image de profile entre ceux du projet et de la BD firebase
                    String nom = dataSnapshot.child("nom").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();

                    textFullName.setText( nom );
                    if(!image.equals("default")) {


                        //Charger l'image avec Picasso, méthode très éfficace. La méthode standard ne marche pas ici
                        //Mais si nous voulons voir la photo même en étant hors ligne nous utilisons ceci pour la mettre dans la mémoire cache
                        Picasso.get().load( image ).networkPolicy( NetworkPolicy.OFFLINE ).placeholder( R.drawable.boy )
                                .into( imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {

                                        Picasso.get().load( image ).placeholder( R.drawable.boy ).into( imageView );

                                    }
                                } );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );

        }
//*************************************************************************************************************************************************************************************************

        //Charger le menu
        recycler_menu = findViewById( R.id.recycler_menu );
        recycler_menu.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this );
        recycler_menu.setLayoutManager( layoutManager );

        loadMenu();


    }

    //-------------------------------------------------   METHODE CHARGEMENT DU MENU  -------------------------------------------------------------------------------------------------------------------------
    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                category
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText( model.getName() );
                Picasso.get().load( model.getImage() ).into( viewHolder.imageView );

                final Category clickItem = model;
                viewHolder.setItemClickListener( new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Si l'utilisateur n'est pas authentifié
                        if (!(firebaseAuth.getCurrentUser() != null)) {
                            Intent AuthIntent = new Intent( Home.this, LoadingLaunchActivity.class );
                            startActivity( AuthIntent );
                            //Toast.makeText( Home.this, ""+clickItem.getName(), Toast.LENGTH_SHORT ).show();

                        }else{

                            //Nous allons récupérer l'id de l'item sur lequel on a cliqué et l'envoyer dans l'activité suivante
                            Intent foodList = new Intent( Home.this, FoodList.class );
                            // Car CategoryId est la clé, donc nous allons juste récupérer la clé de cet item
                            foodList.putExtra( "CategoryId", adapter.getRef( position ).getKey() );
                            startActivity( foodList );

                        }
                    }
                } );


            }
        };

        recycler_menu.setAdapter( adapter );

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {

            //Si l'utilisateur n'est pas authentifié
            if (!(firebaseAuth.getCurrentUser() != null)) {

                Toast.makeText( this, "Vous n'êtes pas authentifié", Toast.LENGTH_SHORT ).show();
                startActivity( new Intent( Home.this, LoadingLaunchActivity.class ) );

            } else{
                firebaseAuth.signOut();
            //Déconnexion pour pour nos providers---------------------------------------------------DEBUT
            AuthUI.getInstance()
                    .signOut( Home.this ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    startActivity( new Intent( Home.this, LauncherActivity.class ) );
                    finish();

                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            } );
        }
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {

            Intent panier = new Intent (Home.this, Cart.class);
            startActivity( panier );

        } else if (id == R.id.nav_orders) {

            Intent Order = new Intent (Home.this, OrderStatus.class);
            startActivity( Order );

        }  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}
