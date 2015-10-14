package com.temperance2015.myweather.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Window;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.temperance2015.myweather.R;
import com.temperance2015.myweather.service.AutoUpdateService;
import com.temperance2015.myweather.util.HttpCallbackListener;
import com.temperance2015.myweather.util.HttpUtil;
import com.temperance2015.myweather.util.Utility;

/**
 * Created by Isabel on 2015/9/23.
 */
public class WeatherActivity extends Activity implements OnClickListener {

    private Button switchCity;
    private Button refreshWeather;
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            //有县级代号时查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }
        else {
            showWeather();
        }
    }

    //查询县级代号对应的天气代号
    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
//        Toast.makeText(this, "查询县级代号对应的天气代号", Toast.LENGTH_SHORT).show();
    }

    //查询天气代号对应的天气
    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
//        Toast.makeText(this, "查询天气代号对应的天气", Toast.LENGTH_SHORT).show();
    }

    private void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    //从SharedPreferences中读取存储的天气信息用于显示
    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天"+prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    public void sendNotification(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(this);
        notification.setAutoCancel(false);
        notification.setContentTitle(prefs.getString("city_name", ""));
        notification.setContentText("今天"+prefs.getString("temp1", "") + "~" + prefs.getString("temp2", "") + prefs.getString("weather_desp", ""));
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker(prefs.getString("city_name", "") + prefs.getString("weather_desp", ""));
        //天气小图标待设
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setPriority(Notification.PRIORITY_DEFAULT);
        manager.notify(1,notification.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.auto_update:
                Intent startIntent = new Intent(this,AutoUpdateService.class);
                startService(startIntent);
                Toast.makeText(this,"自动更新已启动",Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_auto_update:
                Intent stopIntent = new Intent(this,AutoUpdateService.class);
                stopService(stopIntent);
                Toast.makeText(this,"自动更新已停止",Toast.LENGTH_SHORT).show();
                break;
            case R.id.start_notification:
                sendNotification();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
