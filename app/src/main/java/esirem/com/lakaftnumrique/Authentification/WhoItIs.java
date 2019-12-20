package esirem.com.lakaftnumrique.Authentification;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import esirem.com.lakaftnumrique.R;

public class WhoItIs extends AppCompatActivity implements View.OnClickListener {

    private TextView mTitreAppli;
    private Button mAdminBtn, mClientBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_who_it_is );

        mTitreAppli = findViewById( R.id.titre_appli );
        mAdminBtn =  findViewById( R.id.adminBtn );
        mClientBtn = findViewById( R.id.ClientBtn );

        mAdminBtn.setOnClickListener( this );
        mClientBtn.setOnClickListener( this );

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/BRUSHSCI.ttf");
        mTitreAppli.setTypeface(face);

    }

    @Override
    public void onClick(View v) {

        if (v == mAdminBtn){
            Intent mAdminIntent = new Intent( WhoItIs.this, AdminSignin.class );
            startActivity( mAdminIntent );
        }

        if (v == mClientBtn){
            Intent mClientIntent = new Intent( WhoItIs.this, LauncherActivity.class );
            startActivity( mClientIntent );
        }

    }
}
