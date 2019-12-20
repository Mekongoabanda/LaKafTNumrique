package esirem.com.lakaftnumrique.ViewHolder;

//FoodViewHolder.class, Food.class, FoodList.class, food_item.xml, activity_food_list.xml

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import esirem.com.lakaftnumrique.Interface.ItemClickListener;
import esirem.com.lakaftnumrique.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name;
    public ImageView food_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super( itemView );

        food_name = itemView.findViewById( R.id.food_name );
        food_image = itemView.findViewById( R.id.food_image );

        itemView.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick( v, getAdapterPosition(), false );

    }
}
