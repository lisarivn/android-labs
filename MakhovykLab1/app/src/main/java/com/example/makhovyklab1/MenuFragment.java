package com.example.makhovyklab1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MenuFragment extends BaseFragment {

    private static final String KEY_STUDENT = "STUDENT";

    private Button getVariantButton;
    private Student student;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        registerListener(Student.class, receivedStudent -> {
            this.student = receivedStudent;
            updateView();
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            student = savedInstanceState.getParcelable(KEY_STUDENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_menu,
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

        view.findViewById(R.id.optionsButton)
                .setOnClickListener(v ->
                        getAppContract().toOptionsScreen(this, student)
                );

        view.findViewById(R.id.quitButton)
                .setOnClickListener(v ->
                        getAppContract().cancel()
                );

        getVariantButton = view.findViewById(R.id.getVariantButton);

        getVariantButton.setOnClickListener(v ->
                getAppContract().toResultsScreen(this, student)
        );

        TextView versionTextView =
                view.findViewById(R.id.versionTextView);

        if (versionTextView != null) {
            versionTextView.setText(
                    getString(
                            R.string.app_version,
                            BuildConfig.VERSION_NAME
                    )
            );
        }

        updateView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STUDENT, student);
    }

    private void updateView() {
        if (getVariantButton != null) {
            getVariantButton.setEnabled(
                    student != null && student.isValid()
            );
        }
    }
}