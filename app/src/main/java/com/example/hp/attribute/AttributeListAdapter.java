package com.example.hp.attribute;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

/**
 * Created by hp on 2018/7/12.
 */

public class AttributeListAdapter extends BaseAdapter{

    private View itemView;
    private TextView txtName;
    private EditText editTxtValue;
    private TextView txtValue;
    private Spinner spinnerValue;

    private Context context;
    private List<KeyAndValueBean> keyAndValueBeans;
    private Feature feature;
    private String DH;
    boolean isSpinnerFirst=true;
    
    private final int TYPE_1 = 0;     //类型1
    private final int TYPE_2 = 1;     //类型2
    private final int VIEW_TYPE = 2;  //总布局数

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

        if(keyAndValueBeans.get(position).getKey().equals("DD")||keyAndValueBeans.get(position).getKey().equals("DH")
        ||keyAndValueBeans.get(position).getKey().equals("KDCZWT")){
            convertView = LayoutInflater.from(context).inflate(R.layout.query_attribute_fielditem, null);
            itemView = convertView.findViewById(R.id.query_attribute_fielditem_view);
            txtName = (TextView) convertView.findViewById(R.id.query_attribute_fielditem_txtName);
            txtValue = (TextView) convertView.findViewById(R.id.query_attribute_fielditem_txtValue);

            if(keyAndValueBeans.get(position).getKey().equals("DD")){
                txtName.setText("地点");
            }  else if(keyAndValueBeans.get(position).getKey().equals("DH")){
                DH=keyAndValueBeans.get(position).getValue();
                txtName.setText("点号");
            } else if(keyAndValueBeans.get(position).getKey().equals("KDCZWT")){
                txtName.setText("存在问题");
            }
            txtValue.setText(keyAndValueBeans.get(position).getValue());
        }else if(keyAndValueBeans.get(position).getKey().equals("JGDB")) {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_attribute_spinneritem, null);
            itemView = convertView.findViewById(R.id.edit_attribute_spinneritem_view);
            txtName = (TextView) convertView.findViewById(R.id.edit_attribute_spinneritem_txtName);
            spinnerValue = (Spinner) convertView.findViewById(R.id.edit_attribute_spinneritem_txtValue);

            final String[] value={"1","对","错","漏"};
            final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_item,value);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerValue.setAdapter(adapter);
//            spinnerValue.setSelection(adapter.getPosition(value.toString()));
            txtName.setText("与解译结果对比");

            //spinner会重新layout，spinner禁止OnItemSelectedListener默认自动调用一次
            spinnerValue.setSelection(0, true);
            spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {

                    if(isSpinnerFirst){
                        view.setVisibility(View.INVISIBLE);
                    }
                    isSpinnerFirst=false;
                    
                    String s = value[position1];
                    String key = keyAndValueBeans.get(position).getKey();
                    keyAndValueBeans.get(position).setValue(s);
                    feature.getAttributes().replace(key,s);//更新

                   editTxtValue.clearFocus();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }else if(keyAndValueBeans.get(position).getKey().equals("LX")) {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_attribute_spinneritem, null);
            itemView = convertView.findViewById(R.id.edit_attribute_spinneritem_view);
            txtName = (TextView) convertView.findViewById(R.id.edit_attribute_spinneritem_txtName);
            spinnerValue = (Spinner) convertView.findViewById(R.id.edit_attribute_spinneritem_txtValue);

            final String[] value={" ","新建","已更改"};
            final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_item,value);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerValue.setAdapter(adapter);
//            spinnerValue.setSelection(adapter.getPosition(value.toString()));
            txtName.setText("类型");

            //spinner会重新layout，spinner禁止OnItemSelectedListener默认自动调用一次
            spinnerValue.setSelection(0, true);
            spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {

                    String s = value[position1];
                    String key = keyAndValueBeans.get(position).getKey();
                    keyAndValueBeans.get(position).setValue(s);
                    feature.getAttributes().replace(key,s);//更新

                    editTxtValue.clearFocus();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }else if(keyAndValueBeans.get(position).getKey().equals("SFYCZ")) {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_attribute_spinneritem, null);
            itemView = convertView.findViewById(R.id.edit_attribute_spinneritem_view);
            txtName = (TextView) convertView.findViewById(R.id.edit_attribute_spinneritem_txtName);
            spinnerValue = (Spinner) convertView.findViewById(R.id.edit_attribute_spinneritem_txtValue);

            final String[] value={" ","是","否"};
            final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_item,value);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerValue.setAdapter(adapter);
//            spinnerValue.setSelection(adapter.getPosition(value.toString()));
            txtName.setText("是否已查证");

            //spinner会重新layout，spinner禁止OnItemSelectedListener默认自动调用一次
            spinnerValue.setSelection(0, true);
            spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {

                    String s = value[position1];
                    int s1=0;
                    if(s=="是"){s1=1;}
                    else if(s=="否"){s1=2;}
                    String key = keyAndValueBeans.get(position).getKey();
                    keyAndValueBeans.get(position).setValue(String.valueOf(s1));
                    feature.getAttributes().replace(key,s);//更新

                    editTxtValue.clearFocus();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_attribute_fielditem, null);
            itemView = convertView.findViewById(R.id.edit_attribute_fielditem_view);
            txtName = (TextView) convertView.findViewById(R.id.edit_attribute_fielditem_txtName);
            editTxtValue = (EditText) convertView.findViewById(R.id.edit_attribute_fielditem_txtValue);

//            txtName.setText(keyAndValueBeans.get(position).getKey());
            if(keyAndValueBeans.get(position).getKey().equals("KDLX")){
                txtName.setText("矿种类型");
            }else if(keyAndValueBeans.get(position).getKey().equals("KDMC")){
                txtName.setText("矿山名称");
            }else if(keyAndValueBeans.get(position).getKey().equals("KDKCFS")){
                txtName.setText("开采方式");
            }else if(keyAndValueBeans.get(position).getKey().equals("KDKCZT")){
                txtName.setText("开采状态");
            }else if(keyAndValueBeans.get(position).getKey().equals("MS")){
                txtName.setText("野外描述");
            }
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

    private String getValue(String key){
        for(KeyAndValueBean bean:keyAndValueBeans){
            if(key.equals(bean.getKey())){
                return bean.getValue();
            }
        }
        return "";
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

    public String getdh(){
        return DH;
    }
}
