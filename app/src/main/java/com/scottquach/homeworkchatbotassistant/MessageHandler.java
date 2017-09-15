package com.scottquach.homeworkchatbotassistant;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scottquach.homeworkchatbotassistant.models.MessageModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott Quach on 9/15/2017.
 */

public class MessageHandler {

    private DatabaseReference databaseReference;
    private FirebaseUser user;

    public MessageHandler() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void saveToDatabase(List<MessageModel> messageModels) {
        for (MessageModel model: messageModels) {
            databaseReference.child("users").child(user.getUid()).child("messages").child(model.getKey()).setValue(model);
        }
    }

    public List<MessageModel> receiveWelcomeMessage() {
        String[] stringMessages = new String[]{
                "Welcome to App Name",
                "If you haven't done so please specify your classes in the classes tab",
                "Every time you finish a class, I'll be here to ask you what homework you have whether it be a simple assignment or a big project",
                "Using advanced machine learning, you can answer naturally such as \"I have a chapter 3 summary due next class\" or \"I have to finish" +
                        "exam in 3 days\"",
                "Or you can add assignments later by saying something such as \"I have a summary assignment for Research Writing due in 4 days",
                "Remember that the above statements are just basic examples, feel free to speak the way YOU would naturally speak"};
        List<MessageModel> messagesModels = new ArrayList<MessageModel>();

        for(String message : stringMessages) {
            MessageModel model = new MessageModel();
            String key = databaseReference.child("users").child(user.getUid()).child("messages").push().getKey();

            model.setMessage(message);
            model.setType(MessageType.RECEIVED);
            model.setKey(key);
            model.setTimestamp(new Timestamp(System.currentTimeMillis()));
            messagesModels.add(model);
        }
        saveToDatabase(messagesModels);
        return messagesModels;
    }


}
