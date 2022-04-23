package com.project.test2quiz.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.project.test2quiz.data.QuizContract.QuestionEntry;

public class QuestionProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = QuestionProvider.class.getSimpleName();

    private static final int QUESTIONS = 100;
    private static final int QUESTION_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(QuizContract.CONTENT_AUTHORITY,QuizContract.PATH_QUESTIONS,QUESTIONS);
        sUriMatcher.addURI(QuizContract.CONTENT_AUTHORITY,QuizContract.PATH_QUESTIONS + "/#", QUESTION_ID);
    }

    private QuizDbHelper dbHelper;
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Creating a dbHelper object to gain access to a database
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        dbHelper = new QuizDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match){
            case QUESTIONS:
                cursor = db.query(QuizContract.QuestionEntry.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder);
                break;
            case QUESTION_ID:
                selection = QuizContract.QuestionEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(QuizContract.QuestionEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Set notification URI on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        if (match == QUESTIONS) {
            return insertQuestion(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    private Uri insertQuestion(Uri uri, ContentValues values){
        // Data Validation
        /*
        String question = values.getAsString(QuestionEntry.COLUMN_QUESTION);
        if(question == null){
            throw new IllegalArgumentException("Question is required.");
        }

        String option1 = values.getAsString(QuestionEntry.COLUMN_OPTION1);
        String option2 = values.getAsString(QuestionEntry.COLUMN_OPTION2);
        String option3 = values.getAsString(QuestionEntry.COLUMN_OPTION3);
        if(option1 == null || option2 == null || option3 == null){
            throw new IllegalArgumentException("All options are required.");
        }

        Integer answer = values.getAsInteger(QuestionEntry.COLUMN_ANSWER);
        if(answer == null || !QuestionEntry.isValidAnswer(answer)){
            throw new IllegalArgumentException("You have to choose an answer.");
        }
        */
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = db.insert(QuestionEntry.TABLE_NAME,null,values);

        if(id == -1){
            Log.e(LOG_TAG,"Failed to insert row for " + uri);
            return null;
        }

        //Notifying all listeners that data has changed
        getContext().getContentResolver().notifyChange(uri,null);

        //Return new URI with ID
        return ContentUris.withAppendedId(uri, id);
    }
    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case QUESTIONS:
                return updateQuestion(uri,contentValues,selection,selectionArgs);
            case QUESTION_ID:
                selection = QuestionEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateQuestion(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    public int updateQuestion(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if(values.containsKey(QuestionEntry.COLUMN_QUESTION)){
            String question = values.getAsString(QuestionEntry.COLUMN_QUESTION);
            if(question == null){
                throw new IllegalArgumentException("Question not written");
            }
        }
        if(values.containsKey(QuestionEntry.COLUMN_OPTION1) && values.containsKey(QuestionEntry.COLUMN_OPTION2) && values.containsKey(QuestionEntry.COLUMN_OPTION3)){
            String option1 = values.getAsString(QuestionEntry.COLUMN_OPTION1);
            String option2 = values.getAsString(QuestionEntry.COLUMN_OPTION2);
            String option3 = values.getAsString(QuestionEntry.COLUMN_OPTION3);
            if(option1 == null || option2 == null || option3 == null){
                throw new IllegalArgumentException("All options are required.");
            }
        }
        if(values.containsKey(QuestionEntry.COLUMN_ANSWER)){
            Integer answer = values.getAsInteger(QuestionEntry.COLUMN_ANSWER);
            if(answer == null || !QuestionEntry.isValidAnswer(answer)){
                throw new IllegalArgumentException("You have to choose an answer.");
            }
        }
        if(values.size() == 0){
            return 0;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(QuestionEntry.TABLE_NAME,values,selection,selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case QUESTIONS:
                return db.delete(QuestionEntry.TABLE_NAME,selection,selectionArgs);
            case QUESTION_ID:
                selection = QuestionEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.delete(QuestionEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case QUESTIONS:
                return QuestionEntry.CONTENT_LIST_TYPE;
            case QUESTION_ID:
                return QuestionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
