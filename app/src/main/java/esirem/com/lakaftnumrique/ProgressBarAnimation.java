
/*
*  FAIT LE 28/03/2019 à 2:09 du matin
*  By MEKONGO ABANDA Yannick Edouard
* */

/*  LIAISONS : LoadingLaunchActivity.java */

package esirem.com.lakaftnumrique;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import esirem.com.lakaftnumrique.Authentification.LauncherActivity;
import esirem.com.lakaftnumrique.Authentification.WhoItIs;

//Cette classe hérite des attributs de la classe Animation (Classe prédéfinie par Android studio pour les animations)
public class ProgressBarAnimation extends Animation {

    private Context context;
    private ProgressBar progressBar;
    private TextView textView;
    private float from;
    private float to;

    //Notre constructeur
    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView textView, float from, float to){

        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation( interpolatedTime, t );

        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress( (int)value );
        textView.setText((int)value + " %"  );

        if (value == to){
            //On démarre l'activity principale
            Intent mainIntent = new Intent(context, WhoItIs.class);
            //quand on appuie sur retour ça quitte l'app si on est coonnecté
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(mainIntent);
        }
    }
}
