package esirem.com.lakaftnumrique.Authentification;

/*  LIAISONS : ProgressBarAnimations.java */

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import esirem.com.lakaftnumrique.ProgressBarAnimation;
import esirem.com.lakaftnumrique.R;
import io.fabric.sdk.android.Fabric;

public class LoadingLaunchActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView textView, txt_animation;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        Fabric.with(this, new Crashlytics());
        setContentView( R.layout.activity_loading_launch );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        progressBar = findViewById( R.id.progress_bar );
        textView = findViewById( R.id.text_view );
        txt_animation = findViewById( R.id.txt_animation );

        progressBar.setMax( 100 );
        progressBar.setScaleY( 3f );

        //on appelle la procédure
        progressAnimation();

    }

    //-------------------------------------------------------------------FONCTION POUR NOTRE ANIMATION-------------------------------------------------------------------------------------------------------------
    public void progressAnimation(){

        //On crée un objet de type ProgressBarAnimation
        ProgressBarAnimation anim = new ProgressBarAnimation( this, progressBar, textView, 0f, 100f );
        anim.setDuration( 6000 );
        progressBar.setAnimation( anim );
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onStart(){
        super.onStart();

        startAnimationOfTextView1();

    }

    private void startAnimationOfTextView1 (){

        //On lit nos variables d'animations à nos fichiers qui contiennent les configurations d'animations
        Animation animation = AnimationUtils.loadAnimation( this, R.anim.anim );
        txt_animation.startAnimation( animation );

    }

    private void startAnimationOfTextView2 (){

        //On lit nos variables d'animations à nos fichiers qui contiennent les configurations d'animations
        Animation animation1 = AnimationUtils.loadAnimation( this, R.anim.anim1 );
        txt_animation.startAnimation( animation1 );



    }

}
