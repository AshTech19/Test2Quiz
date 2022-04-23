package com.project.test2quiz.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class QuizContract {
    private QuizContract(){}
    //Content Authority
    public static final String CONTENT_AUTHORITY = "com.project.test2quiz";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_QUESTIONS = "questions";

    public static class QuestionEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_QUESTIONS);
        // MIME type for list of questions
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTIONS;
        // MIME type for a single question
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTIONS;
        public static final String TABLE_NAME = "questions";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_ANSWER = "answer";

        //different values for answers
        public static final int CHOOSE_ANSWER = 0;
        public static final int ANSWER_1 = 1;
        public static final int ANSWER_2 = 2;
        public static final int ANSWER_3 = 3;

        public static boolean isValidAnswer(int selectAns) {
            return selectAns == ANSWER_1 || selectAns == ANSWER_2 || selectAns == ANSWER_3;
        }


    }
}
