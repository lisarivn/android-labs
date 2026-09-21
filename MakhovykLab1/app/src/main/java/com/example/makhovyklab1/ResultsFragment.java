package com.example.makhovyklab1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultsFragment extends BaseFragment {

    private static final String TAG =
            ResultsFragment.class.getSimpleName();

    private static final String ARG_STUDENT = "STUDENT";

    private ViewGroup resultsTable;
    private Button doneButton;
    private Button tryAgainButton;
    private ProgressBar progress;
    private TextView variantTextView;
    private TextView errorTextView;

    private final ExecutorService io =
            Executors.newSingleThreadExecutor();

    private final Handler main =
            new Handler(Looper.getMainLooper());

    public static ResultsFragment newInstance(Student student) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_STUDENT, student);

        ResultsFragment fragment = new ResultsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_results,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        resultsTable = view.findViewById(R.id.resultsTable);
        doneButton = view.findViewById(R.id.doneButton);
        tryAgainButton = view.findViewById(R.id.tryAgainButton);
        progress = view.findViewById(R.id.progress);
        variantTextView = view.findViewById(R.id.variantTextView);
        errorTextView = view.findViewById(R.id.errorTextView);

        TextView firstNameTextView =
                view.findViewById(R.id.firstNameTextView);

        TextView lastNameTextView =
                view.findViewById(R.id.lastNameTextView);

        TextView groupTextView =
                view.findViewById(R.id.groupTextView);

        Student student = getStudent();

        firstNameTextView.setText(student.getFirstName());
        lastNameTextView.setText(student.getLastName());
        groupTextView.setText(student.getGroup());

        doneButton.setOnClickListener(
                v -> getAppContract().cancel()
        );

        tryAgainButton.setOnClickListener(
                v -> fetchVariant(student)
        );

        fetchVariant(student);
    }

    private void toPendingState() {
        progress.setVisibility(View.VISIBLE);
        resultsTable.setVisibility(View.INVISIBLE);
        doneButton.setVisibility(View.INVISIBLE);
        tryAgainButton.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
    }

    private void toSuccessState(int variant) {
        doneButton.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.GONE);
        resultsTable.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);

        variantTextView.setText(String.valueOf(variant));
    }

    private void toErrorState(Throwable error) {
        Log.e(TAG, "Error!", error);

        doneButton.setVisibility(View.INVISIBLE);
        tryAgainButton.setVisibility(View.VISIBLE);
        resultsTable.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);

        if (error instanceof IOException) {
            errorTextView.setText(R.string.error);
        } else {
            errorTextView.setText(error.getMessage());
        }
    }

    public static int computeVariant(Student student) {

        String f = student.getFirstName() == null
                ? ""
                : student.getFirstName()
                .trim()
                .toLowerCase();

        String l = student.getLastName() == null
                ? ""
                : student.getLastName()
                .trim()
                .toLowerCase();

        String g = student.getGroup() == null
                ? ""
                : student.getGroup()
                .trim()
                .toUpperCase();

        String key = f + "|" + l + "|" + g;

        try {
            java.security.MessageDigest md =
                    java.security.MessageDigest.getInstance("SHA-256");

            byte[] d = md.digest(
                    key.getBytes(
                            java.nio.charset.StandardCharsets.UTF_8
                    )
            );

            int h =
                    ((d[0] & 0xFF) << 24)
                            | ((d[1] & 0xFF) << 16)
                            | ((d[2] & 0xFF) << 8)
                            | (d[3] & 0xFF);

            int p = (h == Integer.MIN_VALUE)
                    ? 0
                    : Math.abs(h);

            return (p % Student.MAX_VARIANT) + 1;

        } catch (Exception e) {

            int p = Math.abs(key.hashCode());

            return (p % Student.MAX_VARIANT) + 1;
        }
    }

    private void fetchVariant(Student student) {

        toPendingState();

        io.execute(() -> {
            try {
                int variant = computeVariant(student);

                main.post(() ->
                        toSuccessState(variant)
                );

            } catch (Throwable t) {

                main.post(() ->
                        toErrorState(t)
                );
            }
        });
    }

    private Student getStudent() {
        return getArguments()
                .getParcelable(ARG_STUDENT);
    }
}