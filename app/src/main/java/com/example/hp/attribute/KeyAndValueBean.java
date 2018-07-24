package com.example.hp.attribute;

import android.view.View;
import com.esri.arcgisruntime.data.Field;

/**
 * Created by hp on 2018/7/12.
 */

public class KeyAndValueBean {
    private String Key;
    private String Value;

    public String getKey() {
        return Key;
    }

    public String getValue() {
        return Value;
    }

    public void setKey(String key) {
        Key = key;
    }

    public void setValue(String value) {
        Value = value;
    }
}

