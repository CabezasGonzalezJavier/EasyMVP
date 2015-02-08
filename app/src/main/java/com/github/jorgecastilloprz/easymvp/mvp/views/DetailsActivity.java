/*
 * Copyright (C) 2014 Jorge Castillo Pérez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jorgecastilloprz.easymvp.mvp.views;

import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.easymvp.R;
import com.github.jorgecastilloprz.easymvp.mvp.model.Game;
import com.github.jorgecastilloprz.easymvp.mvp.presenters.GameDetailsPresenterImpl;
import com.github.jorgecastilloprz.easymvp.ui.BaseActivity;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Delegates all its presentation logic into the GameDetailsPresenter. Game model needs to be passed
 * to it at the beginning.
 *
 * @author Jorge Castillo Pérez
 */
public class DetailsActivity extends BaseActivity implements GameDetailsPresenterImpl.View {

    public static final String GAME_EXTRA = "com.github.jorgecastilloprz.easymvp.gameExtra";
    public static final String SHARED_IMAGE_EXTRA = "sharedImage";

    @Inject
    GameDetailsPresenterImpl gameDetailsPresenter;

    @InjectView(R.id.detailsToolbar)
    Toolbar toolbar;
    @InjectView(R.id.backgroundImage)
    ImageView bgImage;
    @InjectView(R.id.detailsFab)
    FloatingActionButton detailsFab;
    @InjectView(R.id.title)
    TextView titleText;
    @InjectView(R.id.description)
    TextView descriptionText;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_details);
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        excludeItemsFromTransitionIfLollipop();
        setImageTransition();

        Game game = Parcels.unwrap(getIntent().getExtras().getParcelable(GAME_EXTRA));

        setPresenterView();
        setPresenterGameModel(game);
    }

    private void excludeItemsFromTransitionIfLollipop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            transition.excludeTarget(R.id.detailsToolbar, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void setImageTransition() {
        final ImageView image = (ImageView) findViewById(R.id.backgroundImage);
        ViewCompat.setTransitionName(image, SHARED_IMAGE_EXTRA);
    }

    private void setPresenterView() {
        gameDetailsPresenter.setView(this);
    }

    private void setPresenterGameModel(Game game) {
        gameDetailsPresenter.setGameModel(game);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                gameDetailsPresenter.onUpButtonClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.detailsFab)
    public void onFavouriteButtonClick() {
        gameDetailsPresenter.onFavouriteButtonClicked();
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameDetailsPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameDetailsPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameDetailsPresenter.onPause();
    }

    @Override
    public void getBackToMainScreen() {
        onBackPressed();
    }

    @Override
    public void loadBackgroundImage(String imageUrl) {
        Picasso.with(this).load(imageUrl).into(bgImage, new Callback() {
            @Override
            public void onSuccess() {
                gameDetailsPresenter.onBackgroundLoaded(((BitmapDrawable) bgImage.getDrawable()).getBitmap());
            }

            @Override
            public void onError() {
            }
        });
    }

    @Override
    public void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    /**
     * @param color
     * @TargetApi annotation added to allow IDE compilation. The presenter will control the Build.VERSION.SDK_INT and
     * will take the decision about dispatching or not this method.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setStatusBarColor(int color) {
        getWindow().setStatusBarColor(color);
    }

    @Override
    public void setFloatingButtonNormalColor(int color) {
        detailsFab.setColorNormal(color);
    }

    @Override
    public void setFloatingButtonPressedColor(int color) {
        detailsFab.setColorPressed(color);
    }

    @Override
    public void setFloatingButtonRippleColor(int color) {
        detailsFab.setColorRipple(color);
    }


    @Override
    public void setTitle(String title) {
        titleText.setText(title);
    }

    @Override
    public void setDescription(String description) {
        descriptionText.setText(description);
    }

    @Override
    public void markGameAsFavourite() {
        detailsFab.setImageResource(R.drawable.ic_fav_white);
        Toast.makeText(this, R.string.game_fav, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayMarkAsFavouriteError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}
