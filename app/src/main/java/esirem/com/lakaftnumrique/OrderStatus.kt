package esirem.com.lakaftnumrique

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import esirem.com.lakaftnumrique.Model.Request
import esirem.com.lakaftnumrique.ViewHolder.OrderViewHolder

class OrderStatus : AppCompatActivity() {

    var recyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: FirebaseRecyclerAdapter<Request, OrderViewHolder>? = null
    var database: FirebaseDatabase? = null
    var requests: DatabaseReference? = null
    var userDatabase: DatabaseReference? = null
    var mCurrentuser: FirebaseUser? = null
    var mAuth: FirebaseAuth? = null
    var mCurrentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order_status)
        //Firebase Database
        database = FirebaseDatabase.getInstance()
        //Table requests
        requests = database!!.reference.child("Requests")
        //table User
        userDatabase = database!!.reference.child("Users")
        //Instance d'authentification
        mAuth = FirebaseAuth.getInstance()
        //Utilisateur courant
        mCurrentuser = mAuth!!.getCurrentUser()
        //Id de l'utilisateur courant
        mCurrentUserId = mCurrentuser!!.uid
        recyclerView = findViewById<View>(R.id.listOrders) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager
        userDatabase!!.child(mCurrentUserId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val phone_number = dataSnapshot.child("phone_number").value.toString()
                loadOrders(phone_number)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadOrders(phone: String) {
        adapter = object : FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request::class.java,
                R.layout.order_layout,
                OrderViewHolder::class.java,
                requests!!.orderByChild("phone")
                        .equalTo(phone)
        ) {
            override fun populateViewHolder(viewHolder: OrderViewHolder, request: Request, position: Int) { //On génère une clé et on la "set" sur txtOrderId
                viewHolder.txtOrderId.text = adapter!!.getRef(position).key
                viewHolder.txtOrderStatus.text = convertCodeToStatus(request.status)
                viewHolder.txtOrderAddress.text = request.address
                viewHolder.txtOrderPhone.text = request.phone
            }
        }
        recyclerView!!.adapter = adapter
    }

    private fun convertCodeToStatus(status: String?): String {
        return if (status == "0") "Placed" //Placé dans le panier
        else if (status == "1") "On my way" // en cours d'expédition
        else "Shipped" //Expédié / livré
    }
}