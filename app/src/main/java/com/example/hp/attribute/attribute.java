package com.example.hp.attribute;

import android.view.View;

import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * Created by shxioyang on 2018/7/31.
 */

public class attribute extends Base {
    private View.OnTouchListener defauleOnTouchListener;//默认点击事件
    
    
    public void create() {
        defauleOnTouchListener = super.mapView.getOnTouchListener();//默认点击事件
    }
   
}
