package com.dh.easyweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String KEY = "3whssd4a4b7jrimf";

    private TextView cityName;
    private TextView weatherText;
    private TextView temperature;
    private TextView lastUpdate;
    private Button receiveBtn;
    private EditText cityNameEdit;

    private Context mContext;
    private ProgressDialog progressDialog;

    String cityNameString = "";
    String weatherTextString = "";
    String temperatureString = "";
    String lastUpdateString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        cityName = (TextView) findViewById(R.id.city_name_id);
        weatherText = (TextView) findViewById(R.id.weather_text_id);
        temperature = (TextView) findViewById(R.id.temperature_id);
        lastUpdate = (TextView) findViewById(R.id.update_time_id);
        cityNameEdit = (EditText) findViewById(R.id.city_name_edit);
        receiveBtn = (Button) findViewById(R.id.receive_btn);
        receiveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.receive_btn){
            if(cityNameEdit.getText().toString().isEmpty()){
                Toast.makeText(mContext,"请输入城市名！",Toast.LENGTH_SHORT).show();
                return;
            }
            String urlString = "";
            try {
                urlString = "https://api.thinkpage.cn/v3/weather/now.json?key="
                        + KEY + "&location="
                        + URLEncoder.encode(cityNameEdit.getText().toString(), "UTF-8")
                        + "&language=zh-Hans&unit=c";
            } catch (Exception e){
                e.printStackTrace();
            }
            showProgressDialog();
            sendHttpRequest(urlString);
        }
    }

    private void sendHttpRequest(final String urlString) {

        new Thread(new Runnable() {
            HttpURLConnection connection = null;
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        builder.append(line);
                    }
                    String response = builder.toString();
                    //Log.d("MainActivity", response);
                    parseJSON(response);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }

    private void parseJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject results = jsonObject.getJSONArray("results").getJSONObject(0);

            JSONObject location = results.getJSONObject("location");
            JSONObject now = results.getJSONObject("now");

            cityNameString = location.getString("name");
            weatherTextString = now.getString("text");
            temperatureString = now.getString("temperature");
            lastUpdateString = results.getString("last_update");
            //Log.d("MainActivity",cityNameString + "---" + weatherTextString + "---"
                    //+ temperatureString + "---" + lastUpdateString);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cityName.setText("城市： " + cityNameString);
                    weatherText.setText("天气： " + weatherTextString);
                    temperature.setText("温度： " + temperatureString);
                    lastUpdate.setText("上次更新： " + lastUpdateString);
                    closeProgressDialog();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog() {
        if(progressDialog != null)
            progressDialog.dismiss();
    }

}
