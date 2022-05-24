package com.example.tasksensors;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Authentification extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);
        Button authbutton = (Button) findViewById(R.id.authbut);
        EditText login_username = (EditText) findViewById(R.id.login_username);
        EditText login_password = (EditText) findViewById(R.id.login_password);
        TextView login_result = (TextView) findViewById(R.id.auth_result) ;
        authbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Thread thread = new Thread(){

                    public void run(){
                        URL url = null;
                        try {
                            String entered_username = login_username.getText().toString();
                            String entered_password = login_password.getText().toString();


                            url = new URL(" https://httpbin.org/basic-auth/bob/sympa");
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            String basicAuth = "Basic " + Base64.encodeToString((entered_username+":"+entered_password).getBytes(),
                                    Base64.NO_WRAP);
                            urlConnection.setRequestProperty ("Authorization", basicAuth);
                            try {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                String s = readStream(in);
                                JSONObject json_result_auth = null;
                                Boolean res = null;
                                try {
                                    json_result_auth = new JSONObject(s);
                                    res = json_result_auth.getBoolean("authenticated");
                                }catch(JSONException err) {
                                    Log.d("Error when creating JSON", err.toString());
                                }

                                Boolean finalRes = res;
                                runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      if (finalRes) {
                                                          login_result.setText("Bravo, tu connais tes creds !");
                                                      }else {
                                                          login_result.setText("Non mais sérieux ? C'est pas du tout ça !");
                                                      }

                                                  }
                                              });

                            } finally {
                                urlConnection.disconnect();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            login_result.setText("Non mais sérieux ? C'est pas du tout ça !");
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();


            }

            });

    }
    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
