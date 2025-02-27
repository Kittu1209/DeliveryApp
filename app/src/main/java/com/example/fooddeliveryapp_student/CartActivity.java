import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private TextView totalPrice;
    private Button checkoutBtn;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerCart);
        totalPrice = findViewById(R.id.totalPrice);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchCartItems();
    }

    private void fetchCartItems() {
        db.collection("carts").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) return;

                    List<Map<String, Object>> cartItems = (List<Map<String, Object>>) documentSnapshot.get("items");
                    if (cartItems == null) return;

                    cartItemList.clear();
                    double total = 0;
                    for (Map<String, Object> item : cartItems) {
                        CartItem cartItem = new CartItem(
                                item.get("productId").toString(),
                                item.get("name").toString(),
                                ((Number) item.get("price")).doubleValue(),
                                ((Number) item.get("quantity")).intValue(),
                                item.get("imageUrl").toString(),
                                item.get("shopId").toString()
                        );
                        cartItemList.add(cartItem);
                        total += cartItem.getTotal();
                    }
                    cartAdapter = new CartAdapter(cartItemList);
                    recyclerView.setAdapter(cartAdapter);
                    totalPrice.setText("Total: ₹" + total);
                });
    }
}
