package com.example.hp.attribute;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;

import java.util.List;

/**
 * Created by hp on 2018/7/12.
 */

public class AttributeListAdapter extends BaseAdapter{

    public View itemView;
    public TextView txtName;
    public EditText editTxtValue;
    public TextView txtValue;
    public Spinner spinnerValue;

//    public class AdapterHolder{//列表绑定项
//
////        public CheckBox checkBox;
//    }

    private Context context;
    private List<KeyAndValueBean> keyAndValueBeans;
    private Feature feature;

    public AttributeListAdapter(Context context, List<KeyAndValueBean> keyAndValueBeans,Feature feature) {
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

        if(keyAndValueBeans.get(position).getKey().equals("DD")){
            convertView = LayoutInflater.from(context).inflate(R.layout.query_attribute_fielditem, null);
            itemView = convertView.findViewById(R.id.query_attribute_fielditem_view);
            txtName = (TextView) convertView.findViewById(R.id.query_attribute_fielditem_txtName);
            txtValue = (TextView) convertView.findViewById(R.id.query_attribute_fielditem_txtValue);

            txtName.setText("DD");
            txtValue.setText(keyAndValueBeans.get(position).getValue());
        }else if(keyAndValueBeans.get(position).getKey().equals("JGDB")) {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_attribute_spinneritem, null);
            itemView = convertView.findViewById(R.id.edit_attribute_spinneritem_view);
            txtName = (TextView) convertView.findViewById(R.id.edit_attribute_spinneritem_txtName);
            spinnerValue = (Spinner) convertView.findViewById(R.id.edit_attribute_spinneritem_txtValue);

            final String[] value={" ","是","否"};
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_item,value);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerValue.setAdapter(adapter);
//            spinnerValue.setSelection(adapter.getPosition(value.toString()));
            txtName.setText(keyAndValueBeans.get(position).getKey());

            spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                    String s = value[position1];
//                    String s = spinnerValue.getSelectedItem().toString();
//                    String s=parent.getItemAtPosition(position1).toString();
                    String key = keyAndValueBeans.get(position).getKey();
                    keyAndValueBeans.get(position).setValue(s);
                    feature.getAttributes().replace(key,s);//更新
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }else {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_attribute_fielditem, null);
            itemView = convertView.findViewById(R.id.edit_attribute_fielditem_view);
            txtName = (TextView) convertView.findViewById(R.id.edit_attribute_fielditem_txtName);
            editTxtValue = (EditText) convertView.findViewById(R.id.edit_attribute_fielditem_txtValue);

            txtName.setText(keyAndValueBeans.get(position).getKey());
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

//        //点击字段时，弹出窗口显示属性信息
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String msg = keyAndValueBeans.get(position).getKey() + ":" + keyAndValueBeans.get(position).getValue();
//                DialogUtils.showDialog(context, "字段属性", msg);
//            }
//        });

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

}
