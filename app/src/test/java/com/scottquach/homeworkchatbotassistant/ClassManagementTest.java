package com.scottquach.homeworkchatbotassistant;

import android.content.Context;

import com.scottquach.homeworkchatbotassistant.fragments.DisplayScheduleFragment;
import com.scottquach.homeworkchatbotassistant.presenters.DisplaySchedulePresenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Scott Quach on 10/27/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ClassManagementTest {

    @Mock
    Context context;
    @Mock
    DisplayScheduleFragment fragment = new DisplayScheduleFragment();

    @Test
    public void LoadClassData() {
        DisplaySchedulePresenter presenter = new DisplaySchedulePresenter(fragment);
        presenter.loadData();
    }

}
