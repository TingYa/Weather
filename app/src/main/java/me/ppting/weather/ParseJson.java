package me.ppting.weather;

import android.app.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PPTing on 15/9/24.
 */
public class ParseJson
{
    Context context = MyApplication.getContext();
    public final static String TAG = ParseJson.class.getName();
    //用gson解析返回的数据
    public void parseJsonWithGson(String jsonData) {
        Log.d(TAG, "用gson进行解析");
        Gson gson = new Gson();
        WeatherInfo weatherInfo = gson.fromJson(jsonData,WeatherInfo.class);
        Log.d(TAG,"weatherInfo"+weatherInfo);
        //下面两行 解析为数组
        //List<WeatherInfo> weatherList = gson.fromJson(jsonData, new TypeToken<List<WeatherInfo>>(){}.getType());
        //for (WeatherInfo weatherInfo : weatherList)
        WeatherInfo.Results results = gson.fromJson(jsonData,WeatherInfo.Results.class);
        Log.d(TAG,"error is "+weatherInfo.getError());
        Log.d(TAG,"status is "+weatherInfo.getStatus());
        Log.d(TAG,"date is "+weatherInfo.getDate());
        /////////////////////////////////////////////////////

        WeatherInfo w = new WeatherInfo();
        WeatherInfo.Results results1 = new WeatherInfo.Results();
        Log.d(TAG, "" + results1.getCurrentCity());

    }
    //解析response
    public void parseJson(String jsonData)
    {
        try
        {
            Log.d(TAG,"解析json数据");
            JSONObject jsonObject = new JSONObject(jsonData);
            Log.d(TAG,"error is "+jsonObject.get("error"));
            Log.d(TAG,"status is "+jsonObject.get("status"));
            Log.d(TAG,"date is "+jsonObject.get("date"));
            //找到今天的日期
            String strGetDate = jsonObject.get("date").toString();
            String regExGetDate = "(\\d{4})-(\\d{2})-(\\d{2})";
            Pattern patternGetDate = Pattern.compile(regExGetDate);
            Matcher matcherGetDate = patternGetDate.matcher(strGetDate);
            boolean isFindDate = matcherGetDate.find();
            final String date = matcherGetDate.group();
            Log.d(TAG,"是否找到日期 "+isFindDate);
            Log.d(TAG,"今天日期是 "+date);

            JSONArray resultsJsonArray = jsonObject.getJSONArray("results");//遍历results
            for (int i = 0;i<resultsJsonArray.length();i++)
            {
                org.json.JSONObject jsonObjectInResults = resultsJsonArray.getJSONObject(i);
                Log.d(TAG,"pm25 is "+jsonObjectInResults.get("pm25"));

                String currentCity = jsonObjectInResults.get("currentCity").toString();
                Log.d(TAG,"currentCity is "+currentCity);
                //遍历index 该数据并不需要
                /*
                org.json.JSONArray indexArray = jsonObjectInResults.getJSONArray("index");
                for (int j = 0;j<indexArray.length();j++)
                {
                    JSONObject jsonObjectInIndex = indexArray.getJSONObject(j);
                    Log.d(TAG,"title is "+jsonObjectInIndex.get("title"));
                    Log.d(TAG,"zs is "+jsonObjectInIndex.get("zs"));
                    Log.d(TAG,"tips is "+jsonObjectInIndex.get("tipt"));
                    Log.d(TAG,"des is "+jsonObjectInIndex.get("des"));
                }
                */
                //遍历weather_data
                JSONArray weatherDataArray = jsonObjectInResults.getJSONArray("weather_data");
                for (int k = 0; k<weatherDataArray.length();k++)
                {
                    JSONObject jsonObjectInWeatherData = weatherDataArray.getJSONObject(k);
                    Log.d(TAG,"date is "+jsonObjectInWeatherData.get("date"));
                    Log.d(TAG,"dayPictureUrl is "+jsonObjectInWeatherData.get("dayPictureUrl"));
                    Log.d(TAG,"nightPictureUrl is "+jsonObjectInWeatherData.get("nightPictureUrl"));
                    Log.d(TAG,"weather is "+jsonObjectInWeatherData.get("weather"));
                    Log.d(TAG,"wind is "+jsonObjectInWeatherData.get("wind"));
                    Log.d(TAG,"temperature is "+jsonObjectInWeatherData.get("temperature"));

                    if (k==0)//当天全天温度
                    {
                        //获取当天全天温度
                        String strGetTodayTem = jsonObjectInWeatherData.get("temperature").toString();
                        String regExGetTodayTem = "\\d+\\s\\~\\s\\d+";
                        Pattern patternGetTodayTem = Pattern.compile(regExGetTodayTem);
                        Matcher matcherGetTodayTem = patternGetTodayTem.matcher(strGetTodayTem);
                        boolean isFindTodaytem = matcherGetTodayTem.find();
                        final String todayTem = matcherGetTodayTem.group();
                        Log.d(TAG, "是否找到了一天温度 " + isFindTodaytem);
                        Log.d(TAG, "正则表达式找到的一天温度 " + todayTem);
                        //获取白天天气图Url
                        String dayPictureUrl = jsonObjectInWeatherData.get("dayPictureUrl").toString();
                        Log.d(TAG,"dayPictureUrl is "+dayPictureUrl);


                        //获取实时温度
                        String strGetRealTem = jsonObjectInWeatherData.get("date").toString();
                        Log.d(TAG, "strGetRealTem is " + strGetRealTem);
                        String regExGetRealTem = "：\\d+";
                        Pattern patternGetRealTem = Pattern.compile(regExGetRealTem);
                        Matcher matcherGetRealTem = patternGetRealTem.matcher(strGetRealTem);
                        boolean isFindRealTemWithColon = matcherGetRealTem.find();
                        final String realTemWithColon = matcherGetRealTem.group();
                        Log.d(TAG, "realTemWithColon is " + realTemWithColon);

                        String regExWithoutColon = "\\d+";
                        Pattern patternWithoutColon = Pattern.compile(regExWithoutColon);
                        Matcher matcherWithoutColon = patternWithoutColon.matcher(realTemWithColon);
                        boolean isFindRealtem = matcherWithoutColon.find();
                        final String realTem = matcherWithoutColon.group();
                        Log.d(TAG, "是否找到了实时温度 " + isFindRealtem);
                        Log.d(TAG, "正则表达式找到的实时温度 realTem " + realTem);

                        //将解析到的天气信息存储到sharePerfence中 城市、日期、实时温度、一天温度、白天天气图Url
                        saveWeatherInfo(context, currentCity, date, realTem, todayTem,dayPictureUrl);
                    }
//                    if (k==1)//第二天全天温度
//                    {getTodayTem(jsonObjectInWeatherData);}
//                    if (k==2)//第三天全天温度
//                    {getTodayTem(jsonObjectInWeatherData);}


                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //存储天气信息
    public void saveWeatherInfo(Context context,String currentCity,String date,String realTem,String todayTem,String dayPictureUrl)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("currentCity", currentCity);
        editor.putString("date",date);
        editor.putString("realTem",realTem);
        editor.putString("todayTem",todayTem);
        editor.putString("dayPictureUrl",dayPictureUrl);
        editor.commit();
    }
}
