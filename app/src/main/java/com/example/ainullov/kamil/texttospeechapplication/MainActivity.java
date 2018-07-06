package com.example.ainullov.kamil.texttospeechapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private Button btnStart;
    private Button btnStop;
    private Button btnSpeechRate;
    private Button btnLanguage;
    private EditText editTextToSpeech;
    private TextToSpeech textToSpeech;
    private int result;
    private Locale localeRus;
    private SharedPreferences sPref;
    final String SAVED_TEXT = "saved_text";
    private boolean languageEn = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        textToSpeech = new TextToSpeech(this, this);
        result = textToSpeech.setLanguage(localeRus);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintlayout);

        //Поле для ввода текста
        editTextToSpeech = new EditText(this);
        editTextToSpeech.setId(View.generateViewId());
        editTextToSpeech.setHint("Put your text here");
        editTextToSpeech.setTextAppearance(this,R.style.textStyle);
        editTextToSpeech.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (width <= 800 && height <= 1300 )
        editTextToSpeech.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1100));
        else if(width <= 1500 && height <= 2400)
            editTextToSpeech.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1650));
        else
            editTextToSpeech.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 2200));
        constraintLayout.addView(editTextToSpeech);

        //Загрузка сохраненных данных
        loadData();

        //Кнопка для переключения языка между EN и RU
        btnLanguage = new Button(this);
        btnLanguage.setId(View.generateViewId());
        btnLanguage.setBackgroundResource(R.drawable.button_language);
        btnLanguage.setTextAppearance(this,R.style.textStyle);
        btnLanguage.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnLanguage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!languageEn) {
                    result = textToSpeech.setLanguage(localeRus);
                    Toast.makeText(MainActivity.this, "Language: RU", Toast.LENGTH_SHORT).show();
                    languageEn = true;
                } else {
                    result = textToSpeech.setLanguage(Locale.US);
                    Toast.makeText(MainActivity.this, "Language: EN", Toast.LENGTH_SHORT).show();
                    languageEn = false;
                }
            }
        });
        constraintLayout.addView(btnLanguage);

        //Кнопка для установки скорости речи
        btnSpeechRate = new Button(this);
        btnSpeechRate.setId(View.generateViewId());
        btnSpeechRate.setTextAppearance(this,R.style.textStyle);
        btnSpeechRate.setBackgroundResource(R.drawable.button_rate);
        btnSpeechRate.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnSpeechRate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"0.5", "1.0", "1.5", "2.0", "2.5", "3.0"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pick a speech rate");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                        switch (item) {
                            case 0:
                                textToSpeech.setSpeechRate(0.5f);
                                break;
                            case 1:
                                textToSpeech.setSpeechRate(1.0f);
                                break;
                            case 2:
                                textToSpeech.setSpeechRate(1.5f);
                                break;
                            case 3:
                                textToSpeech.setSpeechRate(2.0f);
                                break;
                            case 4:
                                textToSpeech.setSpeechRate(2.5f);
                                break;
                            case 5:
                                textToSpeech.setSpeechRate(3.0f);
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        constraintLayout.addView(btnSpeechRate);

        //Кнопка для остановка воспроизведения текста
        btnStop = new Button(this);
        btnStop.setId(View.generateViewId());
        btnStop.setTextAppearance(this,R.style.textStyle);
        btnStop.setBackgroundResource(R.drawable.button_stop);
        btnStop.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.stop();
            }
        });
        constraintLayout.addView(btnStop);

        //Кнопка для воспроизведения текста
        btnStart = new Button(this);
        btnStart.setId(View.generateViewId());
        btnStart.setTextAppearance(this,R.style.textStyle);
        btnStart.setBackgroundResource(R.drawable.button_start);
        btnStart.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editTextToSpeech.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        constraintLayout.addView(btnStart);



         // Создание ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        // Связываени элементов
        constraintSet.connect(editTextToSpeech.getId(), ConstraintSet.BOTTOM, btnLanguage.getId(), ConstraintSet.TOP);
        constraintSet.connect(editTextToSpeech.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(btnLanguage.getId(), ConstraintSet.RIGHT, btnSpeechRate.getId(), ConstraintSet.LEFT);
        constraintSet.connect(btnLanguage.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(btnSpeechRate.getId(), ConstraintSet.RIGHT, btnStop.getId(), ConstraintSet.LEFT);
        constraintSet.connect(btnSpeechRate.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(btnStop.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(btnStop.getId(), ConstraintSet.RIGHT, btnStart.getId(), ConstraintSet.LEFT);
        constraintSet.connect(btnStart.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(btnStart.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 8);
        constraintSet.applyTo(constraintLayout);

        result = textToSpeech.setLanguage(Locale.US);
        localeRus = new Locale("ru");
    }


    void saveData() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, editTextToSpeech.getText().toString());
        ed.commit();
    }

    void loadData() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        editTextToSpeech.setText(savedText);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            } else {
                btnStart.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Ошибка!");
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}