package com.scottquach.homeworkchatbotassistant.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.scottquach.homeworkchatbotassistant.R;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Welcome",
                "Organize your homework through the most natural way possible, chat" , R.drawable.ic_homework);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Natural Language",
                "Powered by powerful machine learning and NLP algorithms for constant improvement", R.drawable.ic_think);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Set your classes",
                "Remember to set your classes and I'll remind you to give me your homework!", R.drawable.ic_blackboard);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard("Provide Homework",
                "Once prompted, enter homework by replying. For example, \"Finish reading chapter 8 by next Monday\"", R.drawable.ic_chat);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);
        setOnboardPages(pages);

        setInactiveIndicatorColor(R.color.darkGrey);
        setActiveIndicatorColor(R.color.white);

        setGradientBackground();
        setFinishButtonTitle("Let's Get Started");
    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(IntroActivity.this, SignInActivity.class));
    }
}
