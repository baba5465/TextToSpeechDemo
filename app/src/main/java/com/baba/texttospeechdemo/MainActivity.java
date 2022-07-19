package com.baba.texttospeechdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static int MAX_QUESTION = 10;
    private static int questionCount = 0;
    private static int score = 0;
    private static int questionNum = 0;
    private static Question question;
    private TextView textViewQuestionNum;
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewAnswer;
    private ImageView speakButton;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewQuestionNum = findViewById(R.id.textViewQuestionNum);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewScore = findViewById(R.id.textViewScore);
        textViewAnswer = findViewById(R.id.textViewAnswer);
        speakButton = findViewById(R.id.imageViewButton);
        question = new Question();


        //Check record audio permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), i -> {
            if (i != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.UK);
            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                nextQuestion();
                textToSpeech();
                //speechToText();
            }
        }, 500);

        speakButton.setOnClickListener(view -> speechToText());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                closeNow();
            }
        }
    }

    protected void textToSpeech() {
        String text = textViewQuestion.getText().toString() + " equals";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void speechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        speechRecognizer.startListening(intent);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            //Do something after 100ms
            speechRecognizer.stopListening();
        }, 2000);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println(data.get(0));
                check(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    //Check if the answer is right or wrong
    @SuppressLint("SetTextI18n")
    protected void check(String answer) {
        if (answer.equals(question.getAnswer())) {
            textToSpeech.speak("Correct", TextToSpeech.QUEUE_FLUSH, null);
            score++;
        } else {
            textToSpeech.speak("Incorrect", TextToSpeech.QUEUE_FLUSH, null);
        }
        textViewAnswer.setText(answer);
        textViewScore.setText(score + "/" + MAX_QUESTION);
        questionCount++;

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            nextQuestion();
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textToSpeech();
    }

    //Close the app
    private void closeNow() {
        finishAffinity();
    }

    @SuppressLint("SetTextI18n")
    private void nextQuestion() {
        if (questionCount < MAX_QUESTION) {
            questionNum++;
            question.generateQuestion();
            textViewQuestionNum.setText("Question number " + questionNum);
            textViewQuestion.setText(question.getQuestion());
            textToSpeech();
        } else {
            speakButton.setClickable(false);
            textViewAnswer.setText("Your score is " + score);
        }
    }
}