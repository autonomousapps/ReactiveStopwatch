package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.R;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;
import com.autonomousapps.reactivestopwatch.view.StopwatchView;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StopwatchFragment extends Fragment implements StopwatchMvp.View {

    public static String TAG = StopwatchFragment.class.getSimpleName();

    @BindView(R.id.stopwatch)
    StopwatchView stopwatch;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onTick(long tick) {

    }
}