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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StopwatchFragment extends Fragment implements StopwatchMvp.View {

    public static String TAG = StopwatchFragment.class.getSimpleName();

    // TODO do I want to bind this as a StopwatchView or as a TimeTeller?
    @BindView(R.id.stopwatch)
    TimeTeller stopwatchView;

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
        presenter.start();
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
        stopwatchView.setTime(timeInMillis);
    }

    // Because of backpressure, this will log AT MOST every second
    private void logEverySecond(long timeInMillis) {
        if (timeInMillis % 1000L == 0L) {
            Log.d(TAG, "onTick(): " + timeInMillis);
        }
    }
}