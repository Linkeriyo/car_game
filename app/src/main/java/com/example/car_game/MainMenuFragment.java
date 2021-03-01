package com.example.car_game;

import android.annotation.SuppressLint;
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

public class MainMenuFragment extends Fragment {

    TextView playTextView, tutorialTextView, exitTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupViews(View view) {
        playTextView = view.findViewById(R.id.play_textview);
        tutorialTextView = view.findViewById(R.id.tutorial_textview);
        exitTextView = view.findViewById(R.id.exit_textview);

        playTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                playTextView.setTextColor(Color.MAGENTA);
                playTextView.setBackgroundColor(Color.CYAN);
                NavDirections direction = MainMenuFragmentDirections.actionMainMenuFragmentToLevelsFragment();
                Navigation.findNavController(v).navigate(direction);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                playTextView.setTextColor(Color.BLACK);
                playTextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });

        tutorialTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                tutorialTextView.setTextColor(Color.MAGENTA);
                tutorialTextView.setBackgroundColor(Color.CYAN);
                NavDirections direction = MainMenuFragmentDirections.actionMainMenuFragmentToTutorialFragment();
                Navigation.findNavController(v).navigate(direction);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                tutorialTextView.setTextColor(Color.BLACK);
                tutorialTextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });

        exitTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                exitTextView.setTextColor(Color.MAGENTA);
                exitTextView.setBackgroundColor(Color.CYAN);
                getActivity().finish();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                exitTextView.setTextColor(Color.BLACK);
                exitTextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });
    }
}