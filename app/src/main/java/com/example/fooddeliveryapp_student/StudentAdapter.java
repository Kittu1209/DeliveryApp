package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<StudentModel> studentList;
    private FirebaseFirestore firestore;

    public StudentAdapter(List<StudentModel> studentList) {
        this.studentList = studentList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        StudentModel student = studentList.get(position);
        holder.tvStuName.setText(student.getStuName());
        holder.tvStuEmail.setText(student.getStuEmail());
        holder.tvStuPhone.setText(student.getStuPhone());
        holder.tvStuId.setText(student.getStuId());

        // Set up delete button click listener
        holder.btnDeleteStudent.setOnClickListener(v -> {
            deleteStudent(student.getStuId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // Delete student from Firestore
    private void deleteStudent(String stuId, int position) {
        firestore.collection("Students").document(stuId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove student from the list and notify adapter
                    studentList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(firestore.getApp().getApplicationContext(), "Student deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(firestore.getApp().getApplicationContext(), "Error deleting student: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView tvStuName, tvStuEmail, tvStuPhone, tvStuId;
        Button btnDeleteStudent;

        public StudentViewHolder(View itemView) {
            super(itemView);
            tvStuName = itemView.findViewById(R.id.tvStuName);
            tvStuEmail = itemView.findViewById(R.id.tvStuEmail);
            tvStuPhone = itemView.findViewById(R.id.tvStuPhone);
            tvStuId = itemView.findViewById(R.id.tvStuId);
            btnDeleteStudent = itemView.findViewById(R.id.btnDeleteStudent);
        }
    }
}
