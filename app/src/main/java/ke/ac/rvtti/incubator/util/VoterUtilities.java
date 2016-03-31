package ke.ac.rvtti.incubator.util;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
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
import ke.ac.rvtti.incubator.vote.vote.R;
import ke.ac.rvtti.incubator.vote.voter.RegisterActivity;

/**
 * Created by Shady on 16/03/2016.
 */
public class VoterUtilities {
  public  String password="";
    Context context;
    final Handler x=null;
    final EditText result=null;
    public String displayVoterAuth(Context cntext){
        context=cntext;
        // Toast.makeText(this,"Some text",Toast.LENGTH_LONG).show();
    new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
        final EditText userInput;


        String getM;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.dialog, null);

        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);

        userInput=(EditText)view.findViewById(R.id.editTextDialogUserInput);
        alertDialogBuilder.setIcon(android.R.drawable.ic_lock_lock);
        alertDialogBuilder.setTitle("Voter Authentication");
        alertDialogBuilder.setCancelable(false).setPositiveButton("Login",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      // testLogin
                        password=userInput.toString();
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
    });
return password;

    }

    public static String encryptPassword(String password)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest()).toUpperCase();
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
