package com.example.hp.attribute;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.esri.arcgisruntime.data.Feature;

import java.util.List;

/**
 * 系统弹窗
 */
public class DialogUtils {

    /**
     * 系统弹窗提示信息
     */
    public static void showDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle("系统提示");
//        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    /**
     * 弹窗提示
     * @param context
     * @param title
     * @param msg
     */
    public static void showDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create().show();

    }
    /**
     * 弹窗提示
     * @param context
     * @param title
     * @param msg
     */
    // android components
    public static void showMyDialog(Context context, String title, String msg, List<KeyAndValueBean> keyAndValueBeans, Feature feature) {
         LayoutInflater inflator;
         ListView listView;
         View listLayout;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // set up local variables  设置局部变量
        inflator = LayoutInflater.from(context);
        listLayout = inflator.inflate(R.layout.list_layout, null);
        listView = (ListView) listLayout.findViewById(R.id.list_view);
        builder.setView(listLayout);


        AttributeListAdapter attributeAdapter = new AttributeListAdapter(context, keyAndValueBeans,feature);
        listView.setAdapter(attributeAdapter);
        builder.create().show();

    }


}
