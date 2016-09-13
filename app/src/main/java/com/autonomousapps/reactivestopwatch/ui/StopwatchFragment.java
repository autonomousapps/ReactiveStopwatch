package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.R;
import com.autonomousapps.reactivestopwatch.di.DaggerStopwatchComponent;
import com.autonomousapps.reactivestopwatch.view.TimeTeller;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StopwatchFragment extends Fragment implements StopwatchMvp.View {

    public static String TAG = StopwatchFragment.class.getSimpleName();

    // TODO do I want to bind this as a StopwatchView or as a TimeTeller?
    @BindView(R.id.stopwatch)
    TimeTeller timeTeller;

    @BindView(R.id.btn_reset)
    Button resetButton;

    @BindView(R.id.btn_start)
    Button startPauseButton;

    @Inject StopwatchMvp.Presenter presenter;

    public StopwatchFragment() {
    }

    public static StopwatchFragment newInstance() {
        return new StopwatchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        DaggerStopwatchComponent.create().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    // TODO detaching will actually kill the timer, but I don't want that. I just want it to stop updating the view
    @Override
    public void onPause() {
        super.onPause();
        presenter.detachView();
    }

    @Override
    public void onTick(long timeInMillis) {
        logEverySecond(timeInMillis);
        timeTeller.setTime(timeInMillis);
    }

    private long lastTimeLogged = -1L;

    // Because of backpressure, this will log AT MOST every second
    private void logEverySecond(long timeInMillis) {
        if (timeInMillis % 1000L == 0L && timeInMillis != lastTimeLogged) {
            lastTimeLogged = timeInMillis; // sometimes we get two consecutive ticks with the same time.
            Log.d(TAG, "onTick(): " + timeInMillis);
        }
    }

    @Override
    public void onStopwatchStarted() {
        startPauseButton.setText(getString(R.string.pause));
    }

    @Override
    public void onStopwatchPaused() {
        startPauseButton.setText(getString(R.string.start));
    }

    @OnClick(R.id.btn_start)
    void onClickStartPause() {
        presenter.startOrPause();
    }

    @OnClick(R.id.btn_reset)
    void onClickReset() {
        presenter.reset();
        timeTeller.setTime(0L);
        startPauseButton.setText(getString(R.string.start));
    }
}