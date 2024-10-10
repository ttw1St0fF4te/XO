package com.example.sharedreference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    ImageButton imageBtn;
    Editor setEditor;

    private boolean isX = true;
    private boolean isBot = false;
    private int[][] desk = new int[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        theme();
        setContentView(R.layout.activity_main);
        imageBtn = findViewById(R.id.imageButton);


        imageBtn.setOnClickListener(v -> {
            setEditor = sharedPreferences.edit();

            boolean isNightMode = !sharedPreferences.getBoolean("MODE_NIGHT_ON", false);
            AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            setEditor.putBoolean("MODE_NIGHT_ON", isNightMode);
            setEditor.apply();


            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        });

        resetBoard();
    }

    public void onButtonClick(View view) {
        if (isX) {
            Button button = (Button) view;
            Object tagObject = button.getTag();

            if (tagObject != null) {
                String tag = tagObject.toString();
                int row = Character.getNumericValue(tag.charAt(0));
                int col = Character.getNumericValue(tag.charAt(1));

                if (row >= 0 && row < desk.length && col >= 0 && col < desk[0].length) {
                    if (desk[row][col] == 0) {
                        desk[row][col] = 1;
                        button.setText("x");
                        button.setEnabled(false);

                        if (checkWin()) {
                            Toast.makeText(this, "x wins", Toast.LENGTH_LONG).show();
                            disable();
                        } else if (isFull()) {
                            Toast.makeText(this, "draw", Toast.LENGTH_SHORT).show();
                        } else {
                            isX = false;
                            if (isBot) {
                                botMove();
                            }
                        }
                    }
                }
            }
        } else if (!isBot) {
            Button button = (Button) view;
            Object tagObject = button.getTag();

            if (tagObject != null) {
                String tag = tagObject.toString();
                int row = Character.getNumericValue(tag.charAt(0));
                int col = Character.getNumericValue(tag.charAt(1));

                if (row >= 0 && row < desk.length && col >= 0 && col < desk[0].length) {
                    if (desk[row][col] == 0) {
                        desk[row][col] = 2;
                        button.setText("o");
                        button.setEnabled(false);

                        if (checkWin()) {
                            Toast.makeText(this, "o wins", Toast.LENGTH_SHORT).show();
                            disable();
                        } else if (isFull()) {
                            Toast.makeText(this, "draw", Toast.LENGTH_SHORT).show();
                        } else {
                            isX = true;
                        }
                    }
                }
            }
        }
    }


    public void resetGame(View view) {
        resetBoard();
        enable();
        isX = true;
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                desk[i][j] = 0;
            }
        }
    }



    private void botMove() {
        if (!isFull()) {
            int i, j;
            do {
                i = (int) (Math.random() * 3);
                j = (int) (Math.random() * 3);
            } while (desk[i][j] != 0);

            desk[i][j] = 2;
            Button button = findButtonByTag(i, j);
            button.setText("o");
            button.setEnabled(false);

            if (checkWin()) {
                Toast.makeText(this, "o wins", Toast.LENGTH_LONG).show();
                disable();
            } else if (isFull()) {
                Toast.makeText(this, "draw", Toast.LENGTH_LONG).show();
            } else {
                isX = true;
            }
        }
    }

    private Button findButtonByTag(int row, int col) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                Object tagObject = button.getTag();
                if (tagObject != null) {
                    String tag = tagObject.toString();
                    int buttonRow = Character.getNumericValue(tag.charAt(0));
                    int buttonCol = Character.getNumericValue(tag.charAt(1));
                    if (buttonRow == row && buttonCol == col) {
                        return button;
                    }
                }
            }
        }
        return null;
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (desk[i][0] == desk[i][1] && desk[i][1] == desk[i][2] && desk[i][0] != 0) {
                return true;
            }
            if (desk[0][i] == desk[1][i] && desk[1][i] == desk[2][i] && desk[0][i] != 0) {
                return true;
            }
        }
        if ((desk[0][0] == desk[1][1] && desk[1][1] == desk[2][2] && desk[0][0] != 0) ||
                (desk[0][2] == desk[1][1] && desk[1][1] == desk[2][0] && desk[0][2] != 0)) {
            return true;
        }
        return false;
    }

    private boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (desk[i][j] == 0) return false;
            }
        }
        return true;
    }

    private void disable() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                child.setEnabled(false);
            }
        }
    }

    private void enable() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setEnabled(true);
                button.setText("");
            }
        }
    }

    private void theme()
    {
        boolean isNightMode = sharedPreferences.getBoolean("MODE_NIGHT_ON", false);
        AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }


    public void botGame(View view) {
        resetBoard();
        enable();
        isX = true;
        if(isBot) {
            isBot = false;
            ((Button) view).setText("BOT");
        }
        else {
            isBot = true;
            ((Button) view).setText("1v1");
        }
    }
}