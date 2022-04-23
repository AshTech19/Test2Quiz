package com.project.test2quiz;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.test2quiz.data.QuizContract.QuestionEntry;

public class QuizCreation extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_QUESTION_LOADER=0;
    private Uri mCurrentQuestionUri;

    private EditText questionText, btnText1, btnText2, btnText3;
    private Spinner actualAns;
    Button addBtn, doneBtn, updateBtn, deleteBtn, previewBtn;

    String Question,opt1, opt2, opt3;

    private int mAns = QuestionEntry.CHOOSE_ANSWER;

    public static void start(Context context){
        Intent intent = new Intent(context, QuizCreation.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        Intent intent = getIntent();
        mCurrentQuestionUri = intent.getData();
        if(mCurrentQuestionUri == null){
            setTitle(getString(R.string.title_new_question));
        } else {
            setTitle(getString(R.string.title_edit_question));
            getLoaderManager().initLoader(EXISTING_QUESTION_LOADER, null,this);
        }
        questionText = findViewById(R.id.questionBox);
        btnText1 = findViewById(R.id.textOption1);
        btnText2 = findViewById(R.id.textOption2);
        btnText3 = findViewById(R.id.textOption3);
        actualAns = findViewById(R.id.spinner_answer);

        doneBtn = findViewById(R.id.btnDone);
        doneBtn.setOnClickListener(v -> homeScreen());

        addBtn = findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(v -> saveQuestion());

        deleteBtn = findViewById(R.id.btnDelete);
        deleteBtn.setOnClickListener(v-> deleteDialog());


        previewBtn = findViewById(R.id.btnPreview);
        previewBtn.setOnClickListener(v -> {
            previewQuiz();
            finish();
        });

        setupSpinner();

    }

    private void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, (dialog, id) -> deleteQuestion());
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            if(dialog != null){
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteQuestion(){
        if(mCurrentQuestionUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentQuestionUri,null,null);
            if(rowsDeleted == 0){
                Toast.makeText(this, getString(R.string.delete_question_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_question_successful), Toast.LENGTH_SHORT).show();
            }
         }
        finish();
    }
    private void previewQuiz(){
        QuizActivity.start(this);
    }

    private void setupSpinner(){
        ArrayAdapter<CharSequence> answerSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.array_answer_options, android.R.layout.simple_spinner_item);
        answerSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        actualAns.setAdapter(answerSpinnerAdapter);

        actualAns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals(getString(R.string.answer_1))){
                        mAns = QuestionEntry.ANSWER_1;
                    } else if(selection.equals(getString(R.string.answer_2))){
                        mAns = QuestionEntry.ANSWER_2;
                    } else if(selection.equals(getString(R.string.answer_3))){
                        mAns = QuestionEntry.ANSWER_3;
                    } else {
                        mAns = QuestionEntry.CHOOSE_ANSWER;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mAns = QuestionEntry.CHOOSE_ANSWER;
            }
        });
    }
    private boolean sanityCheck(){

        if(TextUtils.isEmpty(Question)){
            Toast.makeText(this, getString(R.string.insert_question_missing), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(opt1) || TextUtils.isEmpty(opt2) || TextUtils.isEmpty(opt3)){
            Toast.makeText(this, getString(R.string.insert_options_missing), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!QuestionEntry.isValidAnswer(mAns)){
            Toast.makeText(this, getString(R.string.insert_answer_missing), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void saveQuestion(){

        Question = questionText.getText().toString().trim();
        opt1 = btnText1.getText().toString().trim();
        opt2 = btnText2.getText().toString().trim();
        opt3 = btnText3.getText().toString().trim();


        if(sanityCheck()){
            ContentValues values = new ContentValues();
            values.put(QuestionEntry.COLUMN_QUESTION,Question);
            values.put(QuestionEntry.COLUMN_OPTION1,opt1);
            values.put(QuestionEntry.COLUMN_OPTION2,opt2);
            values.put(QuestionEntry.COLUMN_OPTION3,opt3);
            values.put(QuestionEntry.COLUMN_ANSWER,mAns);

            if(mCurrentQuestionUri == null){
                Uri newUri = getContentResolver().insert(QuestionEntry.CONTENT_URI,values);

                if(newUri == null){
                    Toast.makeText(this, getString(R.string.insert_question_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_question_success), Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentQuestionUri,values,null,null);

                if(rowsAffected == 0){
                    Toast.makeText(this, getString(R.string.update_question_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.update_question_success), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(this, getString(R.string.insert_field_missing), Toast.LENGTH_SHORT).show();
        }
    }

    private void homeScreen(){
        MainActivity.start(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                QuestionEntry._ID,
                QuestionEntry.COLUMN_QUESTION,
                QuestionEntry.COLUMN_OPTION1,
                QuestionEntry.COLUMN_OPTION2,
                QuestionEntry.COLUMN_OPTION3,
                QuestionEntry.COLUMN_ANSWER
        };
        return new CursorLoader(this,mCurrentQuestionUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount()<1){
            return;
        }

        if(cursor.moveToFirst()){
            int questionColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_QUESTION);
            int option1ColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_OPTION1);
            int option2ColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_OPTION2);
            int option3ColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_OPTION3);
            int answerColumnIndex = cursor.getColumnIndex(QuestionEntry.COLUMN_ANSWER);

            String question = cursor.getString(questionColumnIndex);
            String option1 = cursor.getString(option1ColumnIndex);
            String option2 = cursor.getString(option2ColumnIndex);
            String option3 = cursor.getString(option3ColumnIndex);
            int answer = cursor.getInt(answerColumnIndex);

            questionText.setText(question);
            btnText1.setText(option1);
            btnText2.setText(option2);
            btnText3.setText(option3);

            switch (answer){
                case QuestionEntry.ANSWER_1:
                    actualAns.setSelection(1);
                    break;
                case QuestionEntry.ANSWER_2:
                    actualAns.setSelection(2);
                    break;
                case QuestionEntry.ANSWER_3:
                    actualAns.setSelection(3);
                    break;
                default:
                    actualAns.setSelection(0);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        questionText.setText("");
        btnText1.setText("");
        btnText2.setText("");
        btnText3.setText("");
        actualAns.setSelection(0);

    }
}
