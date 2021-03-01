package com.example.car_game;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.example.car_game.game.GameActivity;

public class LevelsFragment extends Fragment {

    TextView level1TextView, level2TextView, level3TextView, backTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_levels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupViews(View view) {
        level1TextView = view.findViewById(R.id.level1_textview);
        level2TextView = view.findViewById(R.id.level2_textview);
        level3TextView = view.findViewById(R.id.level3_textview);
        backTextView = view.findViewById(R.id.levels_back_textview);
        
        level1TextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                level1TextView.setTextColor(Color.MAGENTA);
                level1TextView.setBackgroundColor(Color.CYAN);
                startActivity(new Intent(getContext(), GameActivity.class).putExtra("level", 1));
                stopMusic();
                NavDirections direction = LevelsFragmentDirections.actionLevelsFragmentToMainMenuFragment();
                Navigation.findNavController(v).navigate(direction);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                level1TextView.setTextColor(Color.BLACK);
                level1TextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });

        level2TextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                level2TextView.setTextColor(Color.MAGENTA);
                level2TextView.setBackgroundColor(Color.CYAN);
                startActivity(new Intent(getContext(), GameActivity.class).putExtra("level", 2));
                stopMusic();
                NavDirections direction = LevelsFragmentDirections.actionLevelsFragmentToMainMenuFragment();
                Navigation.findNavController(v).navigate(direction);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                level2TextView.setTextColor(Color.BLACK);
                level2TextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });

        level3TextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                level3TextView.setTextColor(Color.MAGENTA);
                level3TextView.setBackgroundColor(Color.CYAN);
                startActivity(new Intent(getContext(), GameActivity.class).putExtra("level", 3));
                stopMusic();
                NavDirections direction = LevelsFragmentDirections.actionLevelsFragmentToMainMenuFragment();
                Navigation.findNavController(v).navigate(direction);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                level3TextView.setTextColor(Color.BLACK);
                level3TextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });

        backTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                backTextView.setTextColor(Color.MAGENTA);
                backTextView.setBackgroundColor(Color.CYAN);
                NavDirections direction = LevelsFragmentDirections.actionLevelsFragmentToMainMenuFragment();
                Navigation.findNavController(v).navigate(direction);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                backTextView.setTextColor(Color.BLACK);
                backTextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });
    }

    private void stopMusic() {
        MenuActivity activity = (MenuActivity) getActivity();
        activity.mediaPlayer.seekTo(0);
    }
}