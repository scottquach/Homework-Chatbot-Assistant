package com.scottquach.homeworkchatbotassistant;

/**
 * Created by Scott Quach on 9/16/2017.
 * Contains Constant values, usually keys or defaults
 */

public class Constants {
    public static final String ACTION_ASSIGNMENT_SPECIFIC_CLASS = "add-assignment-specific-class";
    public static final String ACTION_ASSIGNMENT_PROMPTED_CLASS = "add-assignment-prompted-class";
    public static final String ACTION_NEXT_ASSIGNMENT = "next-assignment";
    public static final String ACTION_REQUEST_HELP = "request-help";
    public static final String ACTION_OVERDUE_ASSIGNMENTS = "overdue_assignments";

    public static final int TIME_PICKER_START = 120;
    public static final int TIME_PICKER_END = 121;

    //Contexts for API.AI
    public static final String CONETEXT_DEFAULT = "default";
    public static final String CONTEXT_PROMPT_HOMEWORK = "prompt-assignment";

    //Argument key values for AlertdialogFragment
    public static final String ALERT_TITLE = "alert_title";
    public static final String ALERT_MESSAGE = "alert_message";
    public static final String ALERT_POSITIVE = "alert_positive";
    public static final String ALERT_NEGATIVE = "alert_negative";
    public static final String ALERT_HAVE_NEGATIVE = "alert_have_negative";

    public static final int JOB_CLASS_MANAGER = 1998;
    public static final String CLASS_NAME = "class_name";
    public static final String CLASS_END_TIME = "class_end_time";
    public static final String USER_CLASS = "user_class";
    public static final String USER_ASSIGNMENT = "user_assignment";
}
