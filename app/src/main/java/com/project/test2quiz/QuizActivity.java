package com.project.test2quiz;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.test2quiz.data.QuizContract.QuestionEntry;

public class QuizActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int QUESTION_LOADER = 0;

    QuestionCursorAdapter mCursorAdapter;

    public static void start(Context context){
        Intent intent = new Intent(context, QuizActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        ListView questionListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        questionListView.setEmptyView(emptyView);

        mCursorAdapter = new QuestionCursorAdapter(this,null);
        questionListView.setAdapter(mCursorAdapter);

        questionListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent = new Intent(QuizActivity.this,QuizCreation.class);
            Uri currentQuestionUri = ContentUris.withAppendedId(QuestionEntry.CONTENT_URI,id);

            intent.setData(currentQuestionUri);
            startActivity(intent);

        });

        getLoaderManager().initLoader(QUESTION_LOADER,null,this);
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
        return new CursorLoader(this,QuestionEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
