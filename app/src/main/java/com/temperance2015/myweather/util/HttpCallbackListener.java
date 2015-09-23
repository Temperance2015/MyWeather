package com.temperance2015.myweather.util;

/**
 * Created by Isabel on 2015/9/23.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
