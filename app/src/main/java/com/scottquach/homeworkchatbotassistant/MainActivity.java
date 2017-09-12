package com.scottquach.homeworkchatbotassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.scottquach.homeworkchatbotassistant.databinding.ActivityMainBinding;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.android.AIConfiguration;
import ai.api.model.Result;
import timber.log.Timber;

import com.google.gson.JsonElement;
import com.scottquach.homeworkchatbotassistant.models.MessageModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AIListener{

    ActivityMainBinding binding;

    private AIService aiService;
    private AIDataService aiDataService;

    private List<MessageModel> messageModels;
    private RecyclerChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final AIConfiguration config = new AIConfiguration("35b6e6bf57cf4c6dbeeb18b1753471ab",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        messageModels = new ArrayList<>();
        setupRecyclerView();
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
//        binding.respondText.setText("Query:" + result.getResolvedQuery() +
//                "\nAction: " + result.getAction() +
//                "\nParameters: " + parameterString);
    }

    @Override
    public void onError(AIError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        Timber.e(error.toString());
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

    private void setupRecyclerView() {
        adapter = new RecyclerChatAdapter(messageModels, this);
        binding.recyclerMessages.setAdapter(adapter);
        binding.recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addMessage(final int messageType, final String message) {
        Handler mainHandler = new Handler(MainActivity.this.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MessageModel model = new MessageModel(messageType, message, new Timestamp(System.currentTimeMillis()));
                messageModels.add(model);
                adapter.addMessage(messageModels);
            }
        };
        mainHandler.post(myRunnable);

    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, 0);

        }
    }

    public void sendButtonClicked(View view) throws AIServiceException {
        String text = binding.editInput.getText().toString();
        addMessage(MessageType.SENT, text);
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

                String textResponse = result.getFulfillment().getSpeech();

                Timber.d("text response was" + textResponse);
                Timber.d("Query:" + result.getResolvedQuery() +
                        "\nAction: " + result.getAction() +
                        "\nParameters: " + parameterString);

                addMessage(MessageType.RECEIVED, result.getFulfillment().getSpeech());

            } catch (Exception e) {
                Timber.d(e);
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
