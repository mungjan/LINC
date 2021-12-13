package com.example.linc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Result extends AppCompatActivity {

    private Button sendbt;
    RadioButton Q1_Y,Q1_N,Q2_Y,Q2_N,Q3_Y,Q3_N,Q4_Y,Q4_N,Q5_Y,Q5_N;
    private RadioGroup A1,A2,A3,A4,A5;
    private String string1, string2, string3, string4, string5, string_all;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    int i = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        sendbt = (Button) findViewById((R.id.button));

        Q1_Y = findViewById(R.id.Q1_Y);
        Q1_N = findViewById(R.id.Q1_N);
        Q2_Y = findViewById(R.id.Q2_Y);
        Q2_N = findViewById(R.id.Q2_N);
        Q3_Y = findViewById(R.id.Q3_Y);
        Q3_N = findViewById(R.id.Q3_N);
        Q4_Y = findViewById(R.id.Q4_Y);
        Q4_N = findViewById(R.id.Q4_N);
        Q5_Y = findViewById(R.id.Q5_Y);
        Q5_N = findViewById(R.id.Q5_N);
        A1 = findViewById(R.id.A1);
        A2 = findViewById(R.id.A2);
        A3 = findViewById(R.id.A3);
        A4 = findViewById(R.id.A4);
        A5 = findViewById(R.id.A5);

        databaseReference = firebaseDatabase.getInstance().getReference().child("User");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    i = (int)dataSnapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ////

            }
        });

        A1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.Q1_Y){
                    string1 = Q1_Y.getText().toString();
                } else if (i == R.id.Q1_N){
                    string1 = Q1_N.getText().toString();
                }
            }
        });

        A2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.Q2_Y){
                    string2 = Q2_Y.getText().toString();
                } else if (i == R.id.Q2_N){
                    string2 = Q2_N.getText().toString();
                }
            }
        });

        A3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.Q3_Y){
                    string3 = Q3_Y.getText().toString();
                } else if (i == R.id.Q3_N){
                    string3 = Q3_N.getText().toString();
                }
            }
        });

        A4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.Q4_Y){
                    string4 = Q4_Y.getText().toString();
                } else if (i == R.id.Q4_N){
                    string4 = Q4_N.getText().toString();
                }
            }
        });

        A5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.Q5_Y){
                    string5 = Q5_Y.getText().toString();
                } else if (i == R.id.Q5_N){
                    string5 = Q5_N.getText().toString();
                }
            }
        });



        sendbt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(string1 !=null && string2 !=null && string3 !=null && string4 !=null && string5 !=null ){
                    string_all = "1." + string1 + "2." + string2 + "3." + string3 + "4." + string4 + "5." + string5;
                    databaseReference.child(String.valueOf(i+1)).setValue(string_all);
                } else {
                    Toast.makeText(Result.this,"항목을 모두 선택해주세요", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}