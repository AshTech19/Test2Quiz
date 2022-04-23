package com.project.test2quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static void start(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private Button btnCreateQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        btnCreateQuiz.setOnClickListener(v -> {
            createQuiz();
            finish();
        });


    }
    private void initToolbar(){

        btnCreateQuiz = findViewById(R.id.createQuiz);

    }
    private void createQuiz(){

        QuizCreation.start(this);
    }
}