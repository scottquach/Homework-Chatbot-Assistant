package com.scottquach.homeworkchatbotassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.scottquach.homeworkchatbotassistant.databinding.ActivityMainBinding;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AIListener{

    ActivityMainBinding binding;

    private AIService aiService;
    private AIDataService aiDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final AIConfiguration config = new AIConfiguration("35b6e6bf57cf4c6dbeeb18b1753471ab",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    @Override
    public void onResult(AIResponse response) {
        Log.d("stuff", "on response was called");
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
        binding.respondText.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameters: " + parameterString);
    }

    @Override
    public void onError(AIError error) {
        binding.respondText.setText(error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    public void mainButtonClicked(View view) {
        aiService.startListening();
        Log.d("stuff", "starting aiService");
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, 0);

        }
    }

    public void textButtonClicked(View view) throws AIServiceException {
        String text = binding.editText.getText().toString();
//        AIRequest aiRequest = new AIRequest();
//        aiRequest.setQuery(text);
//        aiService.textRequest(aiRequest);
        new DoTextRequestTask().execute(text);
    }

    class DoTextRequestTask extends AsyncTask<String, Void, AIResponse> {
        private Exception exception = null;
        protected AIResponse doInBackground(String... text) {
            AIResponse resp = null;
            try {
                resp = aiService.textRequest(text[0], new RequestExtras());
                Result result = resp.getResult();
                // Get parameters
                String parameterString = "";
                if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                    for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                        parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                    }
                }

                // Show results in TextView.
                binding.respondText.setText("Query:" + result.getResolvedQuery() +
                        "\nAction: " + result.getAction() +
                        "\nParameters: " + parameterString);
                // might depend on you implementation ; find out how to
                // retrieve the AIService instance and replace "aiDialog.getAIService()"
            } catch (Exception e) {
                this.exception = e;
            }
            return resp;
        }
        protected void onPostExecute(AIResponse response) {
            if (this.exception == null) {
                // todo : handle the exception
            }
        }
    }
}
