//选择城市布局文件
package com.edu.pku.ss.atp3.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import cn.edu.pku.ss.atp3.app.MyApplication;
import cn.edu.pku.ss.atp3.bean.City;
import db.CityDB;

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;                      //为选择城市界面的返回(ImageView)设置OnClick事件
    public String cityNumber =null;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);



        //*************************************************************************
        Button button = (Button) findViewById(R.id.button);
        editText=(EditText) findViewById(R.id.insert_city);
        editText.setOnClickListener(this);
        //*************************************************************************

        ////////////////////////////////////
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,MyApplication.cityList);
        ListView listView=(ListView) findViewById(R.id.city_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this,"你单击了："+MyApplication.cityList[position],Toast.LENGTH_SHORT).show();
                cityNumber=MyApplication.cityNumberList[position];
            }
        });
        ////////////////////////////////////

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                Intent i=new Intent();             //在finish之前传递数据
  //              i.putExtra("cityCode","101160101");
                i.putExtra("cityCode",cityNumber);
                setResult(RESULT_OK,i);
                finish();
                break;
            case R.id.insert_city:
                Toast.makeText(SelectCity.this,editText.getText(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
