package esirem.com.lakaftnumrique.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import esirem.com.lakaftnumrique.Interface.ItemClickListener;
import esirem.com.lakaftnumrique.R;

//MenuViewHolder.class, Home.class, Category.class, menu_item.xml, home.xml, activity_home_drawer.xml, activity_home.xml
// app_bar_home.xml, content_home.xml, nav_header_home.xml

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super( itemView );

        txtMenuName = itemView.findViewById( R.id.menu_name );
        imageView = itemView.findViewById( R.id.menu_image );

        itemView.setOnClickListener( this );
    }

    public  void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick( v, getAdapterPosition(), false );

    }
}
