package com.example.dungit.gallery.presentation.uis.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.support.v7.widget.Toolbar;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.uis.animation.DepthPageTransformer;
import com.example.dungit.gallery.presentation.uis.animation.FadePageTransformer;
import com.example.dungit.gallery.presentation.uis.animation.ZoomOutPageTransformer;

public class SettingActivity extends AppCompatActivity {

    RadioGroup rgAnimation;
    RadioButton rbDefault;
    RadioButton rbZoom;
    RadioButton rbDepth;
    RadioButton rbFade;

    RadioGroup rgSlideTime;
    RadioButton rb2s;
    RadioButton rb5s;
    RadioButton rb10s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = findViewById(R.id.tb_top_setting);
        setSupportActionBar(toolbar);


        rgAnimation = findViewById(R.id.rgAnimation);
        rbDefault = findViewById(R.id.rbDefault);
        rbZoom = findViewById(R.id.rbZoom);
        rbDepth = findViewById(R.id.rbDepth);
        rbDepth = findViewById(R.id.rbFade);

        rgSlideTime = findViewById(R.id.rgSlideTime);
        rb2s = findViewById(R.id.rb2s);
        rb5s = findViewById(R.id.rb5s);
        rb10s = findViewById(R.id.rb10s);


        int slideShowDelay = PreviewPhotoActivity.getSlideShowDelay();
        ViewPager.PageTransformer animation = PreviewPhotoActivity.getSlideAnimation();

        switch (slideShowDelay) {
            case 2000:
                rb2s.setChecked(true);
                break;
            case 10000:
                rb10s.setChecked(true);
                break;
            default:
                rb5s.setChecked(true);
        }

        if (animation == null) {
            rbDefault.setChecked(true);
        } else if (animation.getClass().equals(DepthPageTransformer.class)) {
            rbDepth.setChecked(true);
        } else if (animation.getClass().equals(FadePageTransformer.class)) {
            rbFade.setChecked(true);
        } else if (animation.getClass().equals(ZoomOutPageTransformer.class)) {
            rbZoom.setChecked(true);
        }

        rgAnimation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbZoom:
                        PreviewPhotoActivity.setSlideAnimation(new ZoomOutPageTransformer());
                        break;
                    case R.id.rbDepth:
                        PreviewPhotoActivity.setSlideAnimation(new DepthPageTransformer());
                        break;
                    case R.id.rbFade:
                        PreviewPhotoActivity.setSlideAnimation(new FadePageTransformer());
                        break;
                    default:
                        PreviewPhotoActivity.setSlideAnimation(null);
                        break;
                }
            }
        });

        rgSlideTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb2s:
                        PreviewPhotoActivity.setSlideShowDelay(2000);
                        break;
                    case R.id.rb10s:
                        PreviewPhotoActivity.setSlideShowDelay(10000);
                        break;
                    default:
                        PreviewPhotoActivity.setSlideShowDelay(5000);
                        break;
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
