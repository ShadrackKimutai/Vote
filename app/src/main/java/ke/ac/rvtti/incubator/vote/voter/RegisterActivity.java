package ke.ac.rvtti.incubator.vote.voter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

import ke.ac.rvtti.incubator.util.VoterUtilities;
import ke.ac.rvtti.incubator.vote.MainActivity;
import ke.ac.rvtti.incubator.vote.vote.R;

public class RegisterActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_SIGNUP = 0;
private Boolean flag,success;
    final Context context=this;
    @Bind(R.id.input_fname) EditText FirstName;
    @Bind(R.id.input_lname) EditText LastName;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_id) EditText _ID;
    @Bind(R.id.btn_login) Button _loginButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    public void register() {


        Log.d(TAG, "Register");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressBar=new ProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.setMessage("Registering "+FirstName.getText().toString().toUpperCase()+" "+LastName.getText().toString().toUpperCase()+"...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();

        new Thread(new Runnable() {
            int progressBarStatus = 0;
            private Handler progressBarbHandler = new Handler();

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Success();
            }

            public void run() {
                while (progressBarStatus < 100) {

                    String imei = getImei();
                    String Result = null;
                    final String Result_ = null;
                    String firstname = FirstName.getText().toString().toUpperCase();
                    String lastname = LastName.getText().toString().toUpperCase();
                    String password = _passwordText.getText().toString();
                    password= VoterUtilities.encryptPassword(password);
                    String id = _ID.getText().toString();


                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://192.168.5.1/m-vote/register.php";
                        HttpPost post = new HttpPost(postURL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Voter_ID", id));
                        params.add(new BasicNameValuePair("Voter_IMEI", getImei()));
                        params.add(new BasicNameValuePair("Voter_FName", firstname));
                        params.add(new BasicNameValuePair("Voter_SName", lastname));
                        params.add(new BasicNameValuePair("Voter_Pass", password));


                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse responsePOST = client.execute(post);
                        HttpEntity resEntity = responsePOST.getEntity();
                        if (resEntity != null) {
                             Result = EntityUtils.toString(resEntity);
                            Result=Result.trim();
                            System.out.println(Result);
                            if (Result.equals("e")){
                                flag=false;
                                progressBarStatus=100;
                            }else if (Result.equals("r")) {
                                flag = true;
                                success=false;
                                progressBarStatus = 100;
                            }else if(Result.equals("s")) {
                                flag=true;
                                success=true;
                                progressBarStatus=100;

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    progressBarbHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }

                if (progressBarStatus >= 100) {
                    try {
                        Thread.sleep(3000);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.dismiss();

                    if (flag.equals(false)){
                        System.out.println("INFO: Attempt to reach the server failed");
                        Success();
                    }else if(flag.equals(true)){
                        if(success.equals(true)){
                            System.out.println("INFO: You have been Registered");
                            Success();
                        }else{
                            System.out.println("INFO: Registration attempt failed at the server");
                            Success();
                        }
                    }
                }
            }
        }).start();


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {


                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the RegisterActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String firstname = FirstName.getText().toString();
        String lastname=LastName.getText().toString();
        String password = _passwordText.getText().toString();
        String id=_ID.getText().toString();

        if (firstname.isEmpty()|| (firstname.matches(".*\\d+.*"))) {
            FirstName.setError("First Name cannot be empty or contain numbers");
            valid = false;
        } else {
            FirstName.setError(null);
        }
        if(lastname.isEmpty()|| (lastname.matches(".*\\d+.*"))) {
            LastName.setError("Last Name cannot be empty or contain numbers");
            valid = false;
        } else {
            LastName.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            _passwordText.setError("Between 6 and 20 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if (id.isEmpty()|| !(id.matches(".*\\d+.*")) || id.length() !=8  ) {
           _ID.setError("ID cannot be empty or contain letters and must be 8 numbers");
            valid = false;
        } else {
            _ID.setError(null);
        }
        return valid;
    }

    public String getImei() {
        TelephonyManager telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }
    public void Success(){
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
        this.finish();

    }

}