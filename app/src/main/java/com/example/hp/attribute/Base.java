package com.example.hp.attribute;

import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * Created by shxioyang on 2018/7/31.
 */

public abstract class Base {
    public MapView mapView;
    private MapView.OnTouchListener mMapOnTouchListener;
    
    public void active(){
        //当前面板活动，其他所有面板关闭
//        EventBus.getDefault().post(new BaseWidgetMsgEvent(id+"-open"));
        mMapOnTouchListener = (MapView.OnTouchListener) mapView.getOnTouchListener();
//        isActiveView =true;
    };
}
