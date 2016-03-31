package ke.ac.rvtti.incubator.vote;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
import ke.ac.rvtti.incubator.vote.choises.ChoiceActivity;
import ke.ac.rvtti.incubator.vote.vote.R;
import ke.ac.rvtti.incubator.vote.voter.RegisterActivity;


public class MainActivity extends Activity {
    VoterUtilities voterUtilities = new VoterUtilities();
    public Handler innerHandler;
    private Boolean flag = true;
    public Context context = this;
    public String passWord;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Initializing...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {

                    String imei = getImei();


                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://192.168.5.1/m-vote/index.php";
                        HttpPost post = new HttpPost(postURL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Voter_IMEI", imei));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse responsePOST = client.execute(post);
                        HttpEntity resEntity = responsePOST.getEntity();
                        if (resEntity != null) {
                            String Result = EntityUtils.toString(resEntity);
                            Result = Result.trim();
                            System.out.println(Result);
                            if (Result.equals("0")) {
                                flag = false;
                                progressBarStatus = 100;
                            } else {
                                if (Result.equals("1")) {
                                    flag = true;
                                    progressBarStatus = 100;
                                }
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

                    if (flag.equals(false)) {
                        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(i);
                        //finish();
                    } else {
                        Intent i = new Intent(MainActivity.this, ChoiceActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        }).start();


    }
    public String getImei() {
        TelephonyManager telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }

}





