package com.scottquach.homeworkchatbotassistant.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scottquach.homeworkchatbotassistant.BaseApplication;
import com.scottquach.homeworkchatbotassistant.InstrumentationUtils;
import com.scottquach.homeworkchatbotassistant.R;

import timber.log.Timber;

public class SignInActivity extends AppCompatActivity{

    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();

        this.findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("Sign in button clicked");
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BaseApplication.getInstance().isFirstOpen()) {
            FirebaseAuth.getInstance().signOut();
        }
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Timber.d("Current user is " + currentUser.getDisplayName());
            Toast.makeText(SignInActivity.this, R.string.signed_in, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        } else Timber.d("Current user is null");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Timber.d("Signed in successfully");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
            } else {
                Timber.d("Signed in failed");
                Timber.d(result.getStatus().getStatusMessage());
                if (result.getStatus().hasResolution()) Timber.d(result.getStatus().getResolution().toString());
                Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Timber.d("Firebase auth with google successful");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(SignInActivity.this, getString(R.string.signed_in), Toast.LENGTH_SHORT).show();
                            createUserInDatabase(user);
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            BaseApplication.getInstance().instrumentation.logEvent(FirebaseAnalytics.Event.LOGIN);
                            finish();
                        } else {
                            BaseApplication.getInstance().instrumentation.logEvent(InstrumentationUtils.Companion.getLOGIN_FAIL());
                            Timber.d("Sign in failed to authenticate google with firebase");
                        }
                    }
                });
    }

    private void createUserInDatabase(FirebaseUser user) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        Timber.d("Create user in database called");
    }

    private void signIn() {
        Timber.d("Attempting to sign in");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public void signInButtonClicked(View view) {
        signIn();
    }
}
