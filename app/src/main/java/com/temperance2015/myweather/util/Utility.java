package com.temperance2015.myweather.util;

import android.text.TextUtils;

import com.temperance2015.myweather.model.City;
import com.temperance2015.myweather.model.County;
import com.temperance2015.myweather.model.MyWeatherDB;
import com.temperance2015.myweather.model.Province;

/**
 * Created by Isabel on 2015/9/23.
 */
public class Utility {

    //解析并处理服务器返回的省级数据
    public synchronized static boolean handleProvincesResponse(MyWeatherDB myWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    myWeatherDB.saveProvince(province);//存入Province表
                }
                return true;
            }
        }
        return false;
    }

    //解析并处理服务器返回的市级数据
    public static boolean handleCitiesResponse(MyWeatherDB myWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    myWeatherDB.saveCity(city);//存入City表
                }
                return true;
            }
        }
        return false;
    }

    //解析并处理服务器返回的县级数据
    public static boolean handleCountiesResponse(MyWeatherDB myWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0){
                for (String c : allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    myWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
