package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminViewUserDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<StudentModel> studentList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user_details);

        recyclerView = findViewById(R.id.recyclerStudentDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        fetchStudentData();
    }

    private void fetchStudentData() {
        firestore.collection("Students")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String stuName = document.getString("stuname");
                        String stuEmail = document.getString("stuemail");
                        String stuPhone = document.getString("stuphno");
                        String stuId = document.getString("stuid");

                        StudentModel student = new StudentModel(stuName, stuEmail, stuPhone, stuId);
                        studentList.add(student);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching students: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
