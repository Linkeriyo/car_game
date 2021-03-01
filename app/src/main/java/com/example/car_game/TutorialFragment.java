package com.example.car_game;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TutorialFragment extends Fragment {

    TextView backTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View view) {
        backTextView = view.findViewById(R.id.tutorial_back_textview);

        backTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                backTextView.setTextColor(Color.MAGENTA);
                backTextView.setBackgroundColor(Color.CYAN);
                NavDirections direction = TutorialFragmentDirections.actionTutorialFragmentToMainMenuFragment();
                Navigation.findNavController(v).navigate(direction);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                backTextView.setTextColor(Color.BLACK);
                backTextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });
    }
}