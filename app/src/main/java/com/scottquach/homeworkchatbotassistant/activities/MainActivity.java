package com.scottquach.homeworkchatbotassistant.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottquach.homeworkchatbotassistant.BaseApplication;
import com.scottquach.homeworkchatbotassistant.Constants;
import com.scottquach.homeworkchatbotassistant.MessageHandler;
import com.scottquach.homeworkchatbotassistant.MessageType;
import com.scottquach.homeworkchatbotassistant.R;
import com.scottquach.homeworkchatbotassistant.adapters.RecyclerChatAdapter;
import com.scottquach.homeworkchatbotassistant.databinding.ActivityMainBinding;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIService;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.android.AIConfiguration;
import ai.api.model.Result;
import timber.log.Timber;

import com.google.gson.JsonElement;
import com.scottquach.homeworkchatbotassistant.models.MessageModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AIListener {

    ActivityMainBinding binding;

    private AIService aiService;

    private List<MessageModel> messageModels;
    private String convoContext;
    private String classContext;
    private RecyclerChatAdapter adapter;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        user = FirebaseAuth.getInstance().getCurrentUser();

        messageHandler = new MessageHandler();

        final AIConfiguration config = new AIConfiguration("35b6e6bf57cf4c6dbeeb18b1753471ab",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        messageModels = new ArrayList<>();

        if (BaseApplication.getInstance().isFirstOpen()) {
            Timber.d("first open");
            messageHandler.receiveWelcomeMessage();
            BaseApplication.getInstance().getSharePref().edit().putBoolean("first_open", false).apply();
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Timber.d("Retrieved DataSnapshot");
                loadData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.d("error retrieving data" + databaseError.toString());
            }
        });
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

    private void loadData(DataSnapshot dataSnapshot) {
        messageModels.clear();
        for (DataSnapshot ds : dataSnapshot.child("users").child(user.getUid()).child("messages").getChildren()) {
            MessageModel messageModel = new MessageModel();
            messageModel.setType((Long) ds.child("type").getValue());
            messageModel.setMessage((String) ds.child("message").getValue());
            messageModel.setTimestamp(new Timestamp((Long) ds.child("timestamp").child("time").getValue()));
            messageModels.add(messageModel);
        }
        convoContext = (String) dataSnapshot.child("users").child(user.getUid()).child("contexts").child("conversation").getValue();
        classContext = (String) dataSnapshot.child("users").child(user.getUid()).child("contexts").child("class").getValue();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        adapter = new RecyclerChatAdapter(messageModels, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.recyclerMessages.setAdapter(adapter);
        binding.recyclerMessages.setLayoutManager(manager);
    }

    private void defaultContext() {
        databaseReference.child("users").child(user.getUid()).child("contexts").child("conversation")
                .setValue(Constants.CONETEXT_DEFAULT);
    }

    private void addMessage(int messageType, String message) {
        String key = databaseReference.child("users").child(user.getUid()).child("messages").push().getKey();

        MessageModel model = new MessageModel(messageType, message, new Timestamp(System.currentTimeMillis()), key);
        messageModels.add(model);
        adapter.addMessage(model);

        databaseReference.child("users").child(user.getUid()).child("messages").child(key).setValue(model);
    }

    private void determineResponseActions(Result result) {
        switch (result.getAction()) {
            case Constants.ACTION_ASSIGNMENT_SPECIFIC_CLASS:
                Timber.d("Action was specific class");
                break;
            case Constants.ACTION_ASSIGNMENT_PROMPTED_CLASS:
                Timber.d("Action was prompted class");
                final HashMap<String, JsonElement> params = result.getParameters();
                String date = params.get("date").getAsString();
                String assignment = params.get("assignment-official").getAsString();
                Timber.d("Received words were " + date + " " + assignment);
                messageHandler.confirmNewHomework(assignment, classContext, date);
                defaultContext();
                break;
            default:
                String textResponse = result.getFulfillment().getSpeech();
                addMessage(MessageType.RECEIVED, textResponse);
                break;
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        }
    }

    public void sendButtonClicked(View view) throws AIServiceException {
        String text = binding.editInput.getText().toString().trim();
        addMessage(MessageType.SENT, text);
        new DoTextRequestTask().execute(text);
        binding.editInput.setText("");
    }

    public void classButtonClicked(View view) {
        startActivity(new Intent(this, DisplayHomeworkActivity.class));
//        startActivity(new Intent(this, ClassScheduleActivity.class));
    }

    class DoTextRequestTask extends AsyncTask<String, Void, AIResponse> {
        private Exception exception = null;

        protected AIResponse doInBackground(String... text) {
            AIResponse resp = null;
            try {
                if (convoContext != null) {
                    List<AIContext> contexts = new ArrayList<>();
                    contexts.add(new AIContext(convoContext));
                    Timber.d("context is " + convoContext);
                    RequestExtras requestExtras = new RequestExtras(contexts, null);
                    resp = aiService.textRequest(text[0], requestExtras);
                } else {
                    Timber.d("context was null");
                    resp = aiService.textRequest(text[0], new RequestExtras());
                }
            } catch (Exception e) {
                Timber.d(e);
            }
            return resp;
        }

        protected void onPostExecute(AIResponse response) {
            if (response != null && !response.isError()) {
                Result result = response.getResult();

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Timber.d(String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }

                Timber.d("Query:" + result.getResolvedQuery() +
                        "\nAction: " + result.getAction());
                determineResponseActions(result);
            } else {
                Timber.d("API.AI response was an error ");
            }
        }
    }
}
