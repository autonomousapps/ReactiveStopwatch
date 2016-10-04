package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.R;
import com.autonomousapps.reactivestopwatch.di.DaggerUtil;
import com.autonomousapps.reactivestopwatch.time.Lap;
import com.autonomousapps.reactivestopwatch.view.TimeTeller;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
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

    @BindView(R.id.stopwatch)
    TimeTeller timeTeller;

    @BindView(R.id.btn_reset_lap)
    Button resetLapButton;

    @BindView(R.id.btn_start_stop)
    Button startStopButton;

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

        DaggerUtil.INSTANCE.getStopwatchComponent().inject(this);
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

    @Override
    public void onPause() {
        presenter.detachView();
        super.onPause();
    }

    @Override
    public void onTick(long timeInMillis) {
        logEverySecond(timeInMillis);
        timeTeller.tellTime(timeInMillis);
    }

    private long lastTimeLogged = -1L;

    // Because of backpressure, this will log AT MOST every second
    private void logEverySecond(long timeInMillis) { // TODO use TimeUnit instead of assuming callers are passing correct units?
        if (timeInMillis % 1000L == 0L && timeInMillis != lastTimeLogged) {
            lastTimeLogged = timeInMillis; // sometimes we get two consecutive ticks with the same time.
            Log.d(TAG, "onTick(): " + timeInMillis);
        }
    }

    @Override
    public void onStopwatchStarted() {
        setStartStopButtonText(R.string.stop);
        setResetLapButton(R.string.lap); // TODO test
    }

    @Override
    public void onStopwatchStopped() {
        setStartStopButtonText(R.string.start);
        setResetLapButton(R.string.reset); // TODO test
    }

    @Override
    public void onStopwatchReset() {
        timeTeller.tellTime(0L);
        setStartStopButtonText(R.string.start);
    }

    @Override
    public void onNewLap(@NonNull Lap lap) {
        // TODO implement. Should the view subscribe to the presenter for new laps? What would happen on rotation, backgrounding and foregrounding the app?
    }

    private void setStartStopButtonText(@StringRes int stringResId) {
        startStopButton.setText(stringResId);
    }

    private void setResetLapButton(@StringRes int stringResId) {
        resetLapButton.setText(stringResId);
    }

    @OnClick(R.id.btn_start_stop)
    void onClickStartStop() {
        presenter.startOrStop();
    }

    @OnClick(R.id.btn_reset_lap)
    void onClickResetOrLap() {
        presenter.resetOrLap();
    }

    @VisibleForTesting
    @Override
    public void setTimeTeller(@NonNull TimeTeller timeTeller) {
        this.timeTeller = timeTeller;
    }
}