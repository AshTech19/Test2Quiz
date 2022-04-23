package com.project.test2quiz;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.project.test2quiz.data.QuizContract.QuestionEntry;

public class QuestionCursorAdapter extends CursorAdapter {
    public QuestionCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.question_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView viewQuestion = view.findViewById(R.id.questionView);
        RadioButton btn1 = view.findViewById(R.id.Option1);
        RadioButton btn2 = view.findViewById(R.id.Option2);
        RadioButton btn3 = view.findViewById(R.id.Option3);

        int questionColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_QUESTION);
        int option1ColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_OPTION1);
        int option2ColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_OPTION2);
        int option3ColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_OPTION3);

        String question = cursor.getString(questionColumnIndex);
        String option1 = cursor.getString(option1ColumnIndex);
        String option2 = cursor.getString(option2ColumnIndex);
        String option3 = cursor.getString(option3ColumnIndex);

        viewQuestion.setText(question);
        btn1.setText(option1);
        btn2.setText(option2);
        btn3.setText(option3);

    }
}
