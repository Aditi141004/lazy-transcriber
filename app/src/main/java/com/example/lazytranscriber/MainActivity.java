package com.example.lazytranscriber;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MIC_PERMISSION_CODE = 1;

    private boolean isHinglishMode = false;
    private boolean isListening = false;

    // Speech
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;

    // UI
    private FloatingActionButton btnMic;
    private Button btnCopy, btnClear;
    private TextView txtOutput, txtStatus;
    private ChipGroup chipGroupLanguage;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupAnimation();
        setupLanguageToggle();
        setupButtons();
        requestMicPermission();
        setupSpeechRecognizer();
    }

    private void bindViews() {
        btnMic = findViewById(R.id.btnMic);
        btnCopy = findViewById(R.id.btnCopy);
        btnClear = findViewById(R.id.btnClear);
        txtOutput = findViewById(R.id.txtOutput);
        txtStatus = findViewById(R.id.txtStatus);
        chipGroupLanguage = findViewById(R.id.chipGroupLanguage);
    }

    @SuppressWarnings("deprecation")
    private void setupLanguageToggle() {
        chipGroupLanguage.setOnCheckedChangeListener(
                (group, checkedId) ->
                        isHinglishMode = (checkedId == R.id.chipHinglish)
        );
    }

    private void setupAnimation() {
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
    }

    private void setupButtons() {

        btnMic.setOnClickListener(v -> {
            if (isListening) {
                stopListening();
            } else {
                startListening();
            }
        });

        btnCopy.setOnClickListener(v -> {
            String text = txtOutput.getText().toString();
            if (!text.equals(getString(R.string.speech_placeholder))) {
                copyToClipboard(text);
            }
        });

        btnClear.setOnClickListener(v -> {
            txtOutput.setText(getString(R.string.speech_placeholder));
            txtStatus.setText(getString(R.string.idle_status));
        });
    }

    /**
     * START listening — recreates recognizer safely
     */
    private void startListening() {
        isListening = true;

        txtStatus.setText(getString(R.string.listening));
        btnMic.setImageResource(R.drawable.ic_mic_off);
        btnMic.startAnimation(pulseAnimation);

        // 🔑 VERY IMPORTANT: recreate recognizer each time
        recreateSpeechRecognizer();

        speechIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                isHinglishMode ? "hi-IN" : "en-IN"
        );

        speechRecognizer.startListening(speechIntent);
    }

    /**
     * STOP listening — safe user stop
     */
    private void stopListening() {
        isListening = false;

        btnMic.clearAnimation();
        btnMic.setImageResource(R.drawable.ic_mic_on);
        txtStatus.setText(getString(R.string.idle_status));

        if (speechRecognizer != null) {
            // cancel() is REQUIRED instead of stopListening()
            speechRecognizer.cancel();
        }
    }

    /**
     * Recreate recognizer to avoid "works once" bug
     */
    private void recreateSpeechRecognizer() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        setupSpeechRecognizer();
    }

    private void requestMicPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_CODE
            );
        }
    }

    private void setupSpeechRecognizer() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        speechIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechIntent.putExtra(
                RecognizerIntent.EXTRA_PREFER_OFFLINE,
                false
        );
        speechIntent.putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
        );

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partial =
                        partialResults.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION);

                if (partial != null && !partial.isEmpty()) {
                    txtOutput.setText(partial.get(0));
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches =
                        results.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    txtOutput.setText(text);
                    copyToClipboard(text);
                }
                stopListening();
            }

            @Override
            public void onError(int error) {
                stopListening();
                txtStatus.setText(getString(R.string.recognition_error, error));
            }

            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        clipboard.setPrimaryClip(
                ClipData.newPlainText("Transcription", text)
        );

        Toast.makeText(
                this,
                getString(R.string.copied_clipboard),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
