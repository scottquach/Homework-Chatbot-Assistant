package com.scottquach.homeworkchatbotassistant.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.scottquach.homeworkchatbotassistant.models.TimeModel;

import java.util.Calendar;
import timber.log.Timber;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public TimePickerFragment() {
        // Required empty public constructor
    }

    int tag = -1;

    public static TimePickerFragment newInstance(int tag) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("tag", tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            tag = args.getInt("tag");
        } else Timber.e("saved bundle was null");

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getContext(), this, hour, minute,
                false);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        ((CreateClassFragment) getTargetFragment()).setEndTime(tag, new TimeModel((long) hour,(long) minute));
    }
}
