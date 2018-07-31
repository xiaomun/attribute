package com.example.hp.attribute;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;

import java.util.List;

/**
 * Created by T410 on 2018/7/27.
 */

public class AttributeListAdapter2 extends BaseAdapter {
    private View itemView;
    private TextView txtName;
    private EditText editTxtValue;
    private TextView txtValue;
    private Spinner spinnerValue;

    private Context context;
    private List<KeyAndValueBean> keyAndValueBeans;
    private Feature feature;
    private String pictureName;

    public AttributeListAdapter2(Context context, List<KeyAndValueBean> keyAndValueBeans,Feature feature) {
        this.context = context;
        this.keyAndValueBeans = keyAndValueBeans;
        this.feature=feature;
    }

    public void refreshData(){
        try {
            notifyDataSetChanged();//刷新数据
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        if (keyAndValueBeans ==null){
            return 0;
        }else {
            return keyAndValueBeans.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        final AdapterHolder holder = new AdapterHolder();

        if(keyAndValueBeans.get(position).getKey().equals("YWDJD")||keyAndValueBeans.get(position).getKey().equals("YWDWD")
                ||keyAndValueBeans.get(position).getKey().equals("YWDGC")||keyAndValueBeans.get(position).getKey().equals("JTZX")
                ||keyAndValueBeans.get(position).getKey().equals("ZPMC")||keyAndValueBeans.get(position).getKey().equals("ZPSJ")){
            convertView = LayoutInflater.from(context).inflate(R.layout.query_attribute_fielditem, null);
            itemView = convertView.findViewById(R.id.query_attribute_fielditem_view);
            txtName = (TextView) convertView.findViewById(R.id.query_attribute_fielditem_txtName);
            txtValue = (TextView) convertView.findViewById(R.id.query_attribute_fielditem_txtValue);

            if(keyAndValueBeans.get(position).getKey().equals("YWDJD")){
                txtName.setText("野外点经度");
            }else if(keyAndValueBeans.get(position).getKey().equals("YWDWD")){
                txtName.setText("野外点纬度");
            }else if(keyAndValueBeans.get(position).getKey().equals("JTZX")){
                txtName.setText("镜头指向");
    
                String s = "西南";
    
                String key = keyAndValueBeans.get(position).getKey();
                keyAndValueBeans.get(position).setValue(s);
                feature.getAttributes().replace(key,s);//更新
            }
            txtValue.setText(keyAndValueBeans.get(position).getValue());

            if(keyAndValueBeans.get(position).getKey().equals("YWDGC")){
                txtName.setText("野外点高程");
                txtValue.setText(keyAndValueBeans.get(position).getValue()+" mH");
            }else if(keyAndValueBeans.get(position).getKey().equals("ZPMC")){
                txtName.setText("照片名称");
                txtValue.setText(pictureName);
                
                String s = pictureName;
                
                String key = keyAndValueBeans.get(position).getKey();
                keyAndValueBeans.get(position).setValue(s);
                feature.getAttributes().replace(key,s);//更新
            }

        }else {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_attribute_fielditem, null);
            itemView = convertView.findViewById(R.id.edit_attribute_fielditem_view);
            txtName = (TextView) convertView.findViewById(R.id.edit_attribute_fielditem_txtName);
            editTxtValue = (EditText) convertView.findViewById(R.id.edit_attribute_fielditem_txtValue);

//            if(keyAndValueBeans.get(position).getKey().equals("YWDBH")){
                txtName.setText("野外点编号");
//            }
//            else if(keyAndValueBeans.get(position).getKey().equals("MS")){
//                txtName.setText("野外描述");
//            }
            editTxtValue.setText(keyAndValueBeans.get(position).getValue());

            editTxtValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String value = s.toString();
                    String key = keyAndValueBeans.get(position).getKey();
                    keyAndValueBeans.get(position).setValue(value);
                    feature.getAttributes().replace(key, value);//更新
                }
            });

            //光标置于文本的末尾
            editTxtValue.setSelection(keyAndValueBeans.get(position).getValue().length());

//            editTxtValue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showSoftInputFromWindow(context, editTxtValue);
//                }
//            });
        }

        //点击字段时，弹出窗口显示属性信息
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = keyAndValueBeans.get(position).getKey() + ":" + keyAndValueBeans.get(position).getValue();
                DialogUtils.showDialog(context, "字段属性", msg);
            }
        });

        return convertView;

    }

    /**
     * EditText获取焦点并显示软键盘
     */
//    public static void showSoftInputFromWindow(Context context, EditText editText) {
//        editText.setFocusable(true);
//        editText.setFocusableInTouchMode(true);
//        editText.requestFocus();
//        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//    }
    public void setPhotoName(String name){
        this.pictureName = name;
        notifyDataSetChanged();
    }
    
}
