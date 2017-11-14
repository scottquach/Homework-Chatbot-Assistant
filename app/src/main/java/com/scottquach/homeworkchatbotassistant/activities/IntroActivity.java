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

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard(getString(R.string.intro_card1),
                getString(R.string.intro_welcome) , R.drawable.ic_homework);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
//        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(6, this));
//        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(4, this));

        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard(getString(R.string.intro_card2),
                getString(R.string.intro_natural_language), R.drawable.ic_think);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setTitleColor(R.color.white);
        ahoyOnboarderCard2.setDescriptionColor(R.color.grey_200);
//        ahoyOnboarderCard2.setTitleTextSize(dpToPixels(6, this));
//        ahoyOnboarderCard2.setDescriptionTextSize(dpToPixels(4, this));


        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard(getString(R.string.intro_card3),
                getString(R.string.intro_set_classes), R.drawable.ic_blackboard);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setTitleColor(R.color.white);
        ahoyOnboarderCard3.setDescriptionColor(R.color.grey_200);

//        ahoyOnboarderCard3.setTitleTextSize(dpToPixels(6, this));
//        ahoyOnboarderCard3.setDescriptionTextSize(dpToPixels(4, this));



        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard(getString(R.string.intro_card4),
                getString(R.string.intro_provide_homework), R.drawable.ic_chat);
        ahoyOnboarderCard4.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard4.setTitleColor(R.color.white);
        ahoyOnboarderCard4.setDescriptionColor(R.color.grey_200);
//        ahoyOnboarderCard4.setTitleTextSize(dpToPixels(6, this));    



        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);
        setOnboardPages(pages);

        setInactiveIndicatorColor(R.color.darkGrey);
        setActiveIndicatorColor(R.color.white);

        setGradientBackground();
        setFinishButtonTitle(R.string.intro_start);
    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(IntroActivity.this, SignInActivity.class));
        finish();
    }
}
