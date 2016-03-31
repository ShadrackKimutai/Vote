package ke.ac.rvtti.incubator.vote.choises;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import ke.ac.rvtti.incubator.vote.MainActivity;
import ke.ac.rvtti.incubator.vote.vote.R;

/**
 * Created by Shady on 17/03/2016.
 */
public class ChoiceActivity extends Activity {
    String Password="";
    ProgressBar progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    Boolean flag=false;
    public ChoiceActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EditText userInput;


        String getM;
        LayoutInflater layoutInflater=LayoutInflater.from(ChoiceActivity.this);
        View view=layoutInflater.inflate(R.layout.dialog, null);

        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(ChoiceActivity.this);
        alertDialogBuilder.setView(view);

        userInput=(EditText)view.findViewById(R.id.editTextDialogUserInput);
        alertDialogBuilder.setIcon(android.R.drawable.ic_lock_lock);
        alertDialogBuilder.setTitle("Voter Authentication");
        alertDialogBuilder.setCancelable(false).setPositiveButton("Login",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // testLogin

                login(userInput.getText().toString());


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                System.exit(0);

            }
        });



        AlertDialog ald=alertDialogBuilder.create();
        ald.show();

    }
    public String getImei() {
        TelephonyManager telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }
    public void login(final String password){

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    progressBarStatus = 0;
                    VoterUtilities voterUtilities = new VoterUtilities();

                    String pass;
                    pass = voterUtilities.encryptPassword(password);
                    String imei = getImei();
                    System.out.println("Pass:" + password + "-->" + pass);

                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://192.168.5.1/m-vote/login.php";
                        HttpPost post = new HttpPost(postURL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Voter_IMEI", imei));
                        // params.add(new BasicNameValuePair("Voter_Pass", pass));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse responsePOST = client.execute(post);
                        HttpEntity resEntity = responsePOST.getEntity();
                        if (resEntity != null) {
                            String Result = EntityUtils.toString(resEntity);
                            Result = Result.trim();
                            System.out.println("Result" + Result);
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
                /*
                    progressBarbHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
               */
                }
            }
        }).start();
    }

}

