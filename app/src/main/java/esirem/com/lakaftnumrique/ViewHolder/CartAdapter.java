package esirem.com.lakaftnumrique.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import esirem.com.lakaftnumrique.Interface.ItemClickListener;
import esirem.com.lakaftnumrique.Model.Order;
import esirem.com.lakaftnumrique.R;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    //Constructeur
    public CartViewHolder(@NonNull View itemView) {
        super( itemView );

        txt_cart_name = itemView.findViewById( R.id.cart_item_name );
        txt_price = itemView.findViewById( R.id.cart_item_Price );
        img_cart_count = itemView.findViewById( R.id.cart_item_count );
    }

    @Override
    public void onClick(View v) {

    }
}


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData =new ArrayList<>(  );
    private Context context;

    //Constructeur
    public CartAdapter (List<Order> listData, Context context){

        this.listData = listData;
        this.context = context;

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //On inflate notre Layout cart_layout
        LayoutInflater inflater = LayoutInflater.from( context );
        View itemView = inflater.inflate( R.layout.cart_layout, parent, false );
        return  new CartViewHolder( itemView );
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        TextDrawable drawable = TextDrawable.builder()
                .buildRound( ""+ listData.get(position).getQuantity(), Color.RED );
        holder.img_cart_count.setImageDrawable( drawable );


        double price = (Double.parseDouble( listData.get( position ).getPrice() )* Integer.parseInt( listData.get( position ).getQuantity() )
                - Double.parseDouble( listData.get( position ).getDiscount() ) * Double.parseDouble( listData.get( position ).getQuantity() ));

        holder.txt_price.setText( String.format("€%s", price ) );
        holder.txt_cart_name.setText( listData.get( position ).getProductName() );

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
