package com.dh.easyweather;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private final static String KEY = "3whssd4a4b7jrimf";

    private TextView cityName;
    private TextView weatherText;
    private TextView temperature;
    private Button receiveBtn;
    private EditText cityNameEdit;

    private TextView todayWeather;
    private TextView todayTemperature;
    private TextView tomorrowWeather;
    private TextView tomorrowTemperature;
    private TextView afterTomorrowWeather;
    private TextView afterTomorrowTemperature;
    //private CheckBox autoCheckBox;
    private CheckBox isDefaultCheckBox;

    private Button btnAuthorInfo;
    private Button btnAuthorGithub;
    private Button btnAuthorWeibo;

    //private AutoCompleteTextView autoCompleteTextView;

    private Context mContext;
    private ProgressDialog progressDialog;

    private String cityNameString = "";
    private String weatherTextString = "";
    private String temperatureString = "";
    private String todayDayWeatherString = "";
    private String todayNightWeatherString = "";
    private String todayTemperatureHigh = "";
    private String todayTemperatureLow = "";
    private String tomorrowDayWeatherString = "";
    private String tomorrowNightWeatherString = "";
    private String tomorrowTemperatureHigh = "";
    private String tomorrowTemperatureLow = "";
    private String afterTomorrowDayWeatherString = "";
    private String afterTomorrowNightWeatherString = "";
    private String afterTomorrowTemperatureHigh = "";
    private String afterTomorrowTemperatureLow = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        cityName = (TextView) findViewById(R.id.city_name_id);
        weatherText = (TextView) findViewById(R.id.weather_text_id);
        temperature = (TextView) findViewById(R.id.temperature_id);
        cityNameEdit = (EditText) findViewById(R.id.city_name_edit);
        receiveBtn = (Button) findViewById(R.id.receive_btn);
        todayWeather = (TextView) findViewById(R.id.today_weather_text);
        todayTemperature = (TextView) findViewById(R.id.today_temperature);
        tomorrowWeather = (TextView) findViewById(R.id.tomorrow_weather_text);
        tomorrowTemperature = (TextView) findViewById(R.id.tomorrow_temperature);
        afterTomorrowWeather = (TextView) findViewById(R.id.aftertomorrow_weather_text);
        afterTomorrowTemperature = (TextView) findViewById(R.id.aftertomorrow_temperature);
        isDefaultCheckBox = (CheckBox) findViewById(R.id.isdefault_check_box);
        btnAuthorInfo = (Button) findViewById(R.id.btn_author_info);
        btnAuthorInfo.setOnClickListener(this);
        btnAuthorGithub = (Button) findViewById(R.id.btn_author_github);
        btnAuthorWeibo = (Button) findViewById(R.id.btn_author_zhihu);

        cityNameEdit.setOnKeyListener(this);

        receiveBtn.setOnClickListener(this);
        SharedPreferences sp = getSharedPreferences("weather", MODE_PRIVATE);
        boolean autoChecked = sp.getBoolean("autoChecked",false);
        if(autoChecked) {
            isDefaultCheckBox.setChecked(true);
            String defaultAddress = sp.getString("address","");
            cityNameEdit.setText(defaultAddress);
        }
        receiveBtn.performClick();
    }

    @Override
    protected void onPause() {
        if(isDefaultCheckBox.isChecked()){
            SharedPreferences.Editor editor = getSharedPreferences("weather", MODE_PRIVATE).edit();
            editor.putString("address",cityNameEdit.getText().toString());
            editor.putBoolean("autoChecked", true);
            editor.commit();
        }
        else {
            SharedPreferences.Editor editor = getSharedPreferences("weather", MODE_PRIVATE).edit();
            editor.putBoolean("autoChecked",false);
            editor.commit();
        }
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.receive_btn){
            String urlNowString = "";
            String urlDailyString = "";
            try {
                if(!cityNameEdit.getText().toString().isEmpty()) {
                    urlNowString = "https://api.thinkpage.cn/v3/weather/now.json?key="
                            + KEY + "&location="
                            + URLEncoder.encode(cityNameEdit.getText().toString(), "UTF-8")
                            + "&language=zh-Hans&unit=c";
                    urlDailyString = "https://api.thinkpage.cn/v3/weather/daily.json?key="
                            + KEY + "&location="
                            + URLEncoder.encode(cityNameEdit.getText().toString(), "UTF-8")
                            + "&language=zh-Hans&unit=c&start=0&days=3";
                } else {
                    urlNowString = "https://api.thinkpage.cn/v3/weather/now.json?key="
                            + KEY + "&location="
                            + "ip"
                            + "&language=zh-Hans&unit=c";
                    urlDailyString = "https://api.thinkpage.cn/v3/weather/daily.json?key="
                            + KEY + "&location="
                            + "ip"
                            + "&language=zh-Hans&unit=c&start=0&days=3";
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            showProgressDialog();
            sendHttpRequest(urlNowString, urlDailyString);
        }else if(view.getId() == R.id.btn_author_info){
            Dialog dialog = new Dialog(mContext);
            dialog.setTitle("关于作者");
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            View dialogView = View.inflate(mContext,R.layout.author_info,null);
            dialog.setContentView(dialogView);
            dialog.findViewById(R.id.btn_author_github).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Artyhacker"));
                        startActivity(intent);
                }
            });
            dialog.findViewById(R.id.btn_author_zhihu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zhihu.com/people/artyhacker"));
                    startActivity(intent);
                }
            });
            dialog.show();
        }
    }

    private void sendHttpRequest(final String urlNowString, final String urlDailyString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String responseNow = urlToString(urlNowString);
                parseNowJSON(responseNow);
                String responseDaily = urlToString(urlDailyString);
                parseDailyJSON(responseDaily);
            }
        }).start();
    }



    private String urlToString(String urlString){
        HttpURLConnection connection = null;
        String response = "";
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
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            response = builder.toString();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(connection != null)
                connection.disconnect();
            return response;
        }
    }

    private void parseNowJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject results = jsonObject.getJSONArray("results").getJSONObject(0);

            JSONObject location = results.getJSONObject("location");
            JSONObject now = results.getJSONObject("now");

            cityNameString = location.getString("name");
            weatherTextString = now.getString("text");
            temperatureString = now.getString("temperature");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cityName.setText(cityNameString);
                    weatherText.setText(weatherTextString);
                    temperature.setText(temperatureString + "°");
                    closeProgressDialog();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDailyJSON(String responseDaily) {
        try {
            JSONObject jsonObject = new JSONObject(responseDaily);
            JSONObject results = jsonObject.getJSONArray("results").getJSONObject(0);
            //Log.d("MainActivity", results.toString());
            JSONArray daily = results.getJSONArray("daily");
            JSONObject today = daily.getJSONObject(0);
            JSONObject tomorrow = daily.getJSONObject(1);
            JSONObject afterTomorrow = daily.getJSONObject(2);
            todayDayWeatherString = today.getString("text_day");
            todayNightWeatherString = today.getString("text_night");
            todayTemperatureHigh = today.getString("high");
            todayTemperatureLow = today.getString("low");
            tomorrowDayWeatherString = tomorrow.getString("text_day");
            tomorrowNightWeatherString = tomorrow.getString("text_night");
            tomorrowTemperatureHigh = tomorrow.getString("high");
            tomorrowTemperatureLow = tomorrow.getString("low");
            afterTomorrowDayWeatherString = afterTomorrow.getString("text_day");
            afterTomorrowNightWeatherString = afterTomorrow.getString("text_night");
            afterTomorrowTemperatureHigh = afterTomorrow.getString("high");
            afterTomorrowTemperatureLow = afterTomorrow.getString("low");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!todayDayWeatherString.equals(todayNightWeatherString)) {
                        todayWeather.setText(todayDayWeatherString + " 转 "
                                + todayNightWeatherString);
                    } else
                        todayWeather.setText(todayDayWeatherString);
                    todayTemperature.setText(todayTemperatureLow + " ~ "
                            + todayTemperatureHigh);

                    if(!tomorrowDayWeatherString.equals(tomorrowNightWeatherString)) {
                        tomorrowWeather.setText(tomorrowDayWeatherString + " 转 "
                                + tomorrowNightWeatherString);
                    } else
                        tomorrowWeather.setText(tomorrowDayWeatherString);
                    tomorrowTemperature.setText(tomorrowTemperatureLow + " ~ "
                            + tomorrowTemperatureHigh);

                    if(!afterTomorrowDayWeatherString.equals(afterTomorrowNightWeatherString)) {
                        afterTomorrowWeather.setText(afterTomorrowDayWeatherString + " 转 "
                                + afterTomorrowNightWeatherString);
                    } else
                        afterTomorrowWeather.setText(afterTomorrowDayWeatherString);
                    afterTomorrowTemperature.setText(afterTomorrowTemperatureLow + " ~ "
                            + afterTomorrowTemperatureHigh);
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

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            //修改回车键功能
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            receiveBtn.callOnClick();
        }
        return false;
    }


}
