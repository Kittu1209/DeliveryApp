package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Fragment_DeliveryAddressStudent extends Fragment implements AddressAdapter.OnAddressSelectedListener {
    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<AddressModel> addressList;
    private Button btnAddNewAddress;
    private FirestoreHelper firestoreHelper;

    public Fragment_DeliveryAddressStudent() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__delivery_address_student, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAddresses);
        btnAddNewAddress = view.findViewById(R.id.btnAddNewAddress);
        firestoreHelper = new FirestoreHelper();

        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getContext(), addressList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(addressAdapter);

        loadAddresses();
        btnAddNewAddress.setOnClickListener(v -> showAddAddressDialog());

        return view;
    }

    private void loadAddresses() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        CollectionReference addressRef = FirebaseFirestore.getInstance().collection("Delivery_address");

        addressRef.whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    addressList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AddressModel address = document.toObject(AddressModel.class);
                        address.setId(document.getId());
                        addressList.add(address);
                    }
                    addressAdapter.notifyDataSetChanged();
                });
    }

    private void showAddAddressDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_address);

        EditText etStudentName = dialog.findViewById(R.id.etStudentName);
        EditText etPhoneNumber = dialog.findViewById(R.id.etPhoneNumber);
        EditText etHostel = dialog.findViewById(R.id.etHostel);
        EditText etRoom = dialog.findViewById(R.id.etRoom);
        Button btnSave = dialog.findViewById(R.id.btnSaveAddress);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            if (user == null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = user.getUid();
            String name = etStudentName.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();
            String hostel = etHostel.getText().toString().trim();
            String room = etRoom.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(hostel) || TextUtils.isEmpty(room)) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            AddressModel newAddress = new AddressModel(null, name, phone, hostel, room, userId);
            firestoreHelper.addAddress(newAddress, () -> {
                loadAddresses();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // Handle Address Selection (Move to Payment Page)
    @Override
    public void onAddressSelected(AddressModel selectedAddress) {
        Intent intent = new Intent(getContext(), PaymentPage.class);
        intent.putExtra("selectedAddress", selectedAddress);
        startActivity(intent);
    }
}
