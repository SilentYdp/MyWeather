
package com.edu.pku.ss.atp3.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.pku.ss.atp3.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.ss.atp3.bean.TodayWeather;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER =1;
    private ImageView mUpdateBtn;                          //在UI线程中，为更新按钮（ImageView）增加单击事件
    private ImageView mCitySelect;                         //为选择城市ImageView添加OnClick事件
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,pmQualityTv,temperatureTv, climateTv, windTv, city_name_Tv;           //初始化界面控件
    private ImageView weatherImg, pmImg;
    private Handler mHandle = new Handler(){

        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    //*************************************************************************
    private ProgressBar progressBar;

    //*************************************************************************

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);             //通过调用setContentView方法加载布局
        //*************************************************************************
        progressBar = (ProgressBar) findViewById(R.id.progress_large);
        //*************************************************************************

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {             //通过该语句检测网络是否连接
            Log.d("myWeather", "网络OK！");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了！");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

        mCitySelect=(ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();             //在onCreate方法中调用initView函数


    }

    /**
     * 初始化控件内容
     */
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_date);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }


    /**
     *
     * @param xmldate 解析函数，解析出城市名称已经更新时间信息
     */
   private TodayWeather parseXML(String xmldate){
       TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldate));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather !=null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                        //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一元素并处罚相应事件
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }


    /**
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {                         //获取网络数据
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);               //在获取网络数据后，调用解析函数
                    if (todayWeather !=null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandle.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        if(climateTv.getText().equals("暴雪")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_baoxue    )));
        }else if (climateTv.getText().equals("暴雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_baoyu)));
        }else if (climateTv.getText().equals("大暴雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_dabaoyu)));
        }else if (climateTv.getText().equals("大雪")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_daxue)));
        }else if (climateTv.getText().equals("大雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_dayu)));
        }else if (climateTv.getText().equals("多云")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_duoyun)));
        }else if (climateTv.getText().equals("雷阵雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_leizhenyu)));
        }else if (climateTv.getText().equals("雷阵雨冰雹")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_leizhenyubingbao)));
        }else if (climateTv.getText().equals("晴")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_qing)));
        }else if (climateTv.getText().equals("沙尘暴")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_shachenbao)));
        }else if (climateTv.getText().equals("特大暴雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_tedabaoyu)));
        }else if (climateTv.getText().equals("雾")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_wu)));
        }else if (climateTv.getText().equals("小雪")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_xiaoxue)));
        }else if (climateTv.getText().equals("小雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_xiaoyu)));
        }else if (climateTv.getText().equals("阴")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_yin)));
        }else if (climateTv.getText().equals("雨夹雪")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_yujiaxue)));
        }else if (climateTv.getText().equals("阵雪")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_zhenxue)));
        }else if (climateTv.getText().equals("阵雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_zhenyu)));
        }else if (climateTv.getText().equals("中雪")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_zhongxue)));
        }else if (climateTv.getText().equals("中雨")){
            weatherImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_zhongyu)));
        }

        if(pmQualityTv.getText().equals("优")){
            pmImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_0_50)));
        } else if (pmQualityTv.getText().equals("良")) {
            pmImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_51_100)));
        }else if (pmQualityTv.getText().equals("轻度污染")) {
            pmImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_101_150)));
        }else if (pmQualityTv.getText().equals("中度污染")) {
            pmImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_151_200)));
        }else if (pmQualityTv.getText().equals("重度污染")) {
            pmImg.setImageDrawable(getResources().getDrawable((R.drawable.biz_plugin_weather_201_300)));
        }


        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.title_city_manager){                    //在UI线程中，为更新按钮（ImageView）增加单击事件
            Intent i=new Intent(this,SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);          //修改更新按钮的单击事件处理程序
        }

        if (view.getId() == R.id.title_update_btn) {                   //从SharedPreferences中读取城市的id
            //****************************************************************
            progressBar.setVisibility(View.VISIBLE);
            //****************************************************************
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK！");
                queryWeatherCode(cityCode);
                //****************************************************************
                progressBar.setVisibility(View.GONE);
                //****************************************************************
            } else {
                Log.d("myWeather", "网络挂了！");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
                //****************************************************************
                //progressBar.setVisibility(View.GONE);
                //****************************************************************
            }

        }

    }
    protected void onActivityResult(int requestCode, int resultCode,Intent data){                 //onActivityResult函数用于接收返回的数据
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode =data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+ newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK！");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了！");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }
}
