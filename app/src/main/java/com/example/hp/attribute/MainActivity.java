package com.example.hp.attribute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Region;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.CallScreeningService;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import java.io.File;
import java.security.Key;
import java.security.cert.PolicyNode;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    // arcgis 组件
    private MapView mMapView;
    private ArcGISMap mArcGISMap;
    private FeatureLayer mShapefileLayer;
    private ShapefileFeatureTable shapefileFeatureTable;
    private Feature selectFeature;//当前选中要素信息
    private FeatureLayer selectFeatureLayer;//选中要素图层
    // android 组件
    private android.graphics.Point pointClicked;
    private String layerName;
    private View listLayout;
    private String pictureName;
    private AttributeListAdapter attributeAdapter;
    private AttributeListAdapter2 attributeAdapter2;
    private String DH;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapView);

        requestWritePermission();

        //主布局中引入属性布局，判断是否可见，初始状态 不可见
        listLayout=findViewById(R.id.list_layout_include);
        listLayout.setVisibility(View.GONE);

        // set an on touch listener to listen for click events
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this,mMapView){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                pointClicked=new android.graphics.Point(Math.round(e.getX()),Math.round(e.getY()));
                clickMap();
                return super.onSingleTapConfirmed(e);
            }
        });
    }
    
    private void clickMap(){
        // get the point that was clicked and convert it to a point in map coordinates
        int tolerance=10;
        final ListenableFuture<List<IdentifyLayerResult>> future=mMapView.identifyLayersAsync(pointClicked,tolerance,false);
        future.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Feature> selectFeatureList = new ArrayList<>();
                    List<IdentifyLayerResult> identifyLayersResults = future.get();
                
                    for (IdentifyLayerResult identifyLayerResult : identifyLayersResults) {
                        for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                            identifyLayerResult.getLayerContent();
                            if (identifiedElement instanceof Feature) {
                                Feature identifiedFeature = (Feature) identifiedElement;
                                selectFeatureList.add(identifiedFeature);
                            }
                        }
                    }
                    selectFeature(selectFeatureList);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestWritePermission() {
        // define permission to request
        String[] reqPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int requestCode = 2;
        // For API level 23+ request permission at runtime
        if (ContextCompat.checkSelfPermission(this, reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            showShapefile();
        } else {
            // request permission
            ActivityCompat.requestPermissions(this, reqPermission, requestCode);
        }
    }

    private void showShapefile() {
        mMapView.setAttributionTextVisible(false);
        mArcGISMap = new ArcGISMap(new Basemap().createImageryWithLabelsVector());
        mMapView.setMap(mArcGISMap);
        shapefileFeatureTable = new ShapefileFeatureTable(
                getResources().getString(R.string.shapefile_path)
        );
        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                GeometryType gt = shapefileFeatureTable.getGeometryType();
                String name = shapefileFeatureTable.getTableName();
                String name1 = name;
                if(shapefileFeatureTable .getLoadStatus()== LoadStatus.LOADED) {
                    mShapefileLayer = new FeatureLayer(shapefileFeatureTable);
                    if (mShapefileLayer.getFullExtent() != null) {
                        mMapView.setViewpointGeometryAsync(mShapefileLayer.getFullExtent());
                    } else {
                        mShapefileLayer.addDoneLoadingListener(new Runnable() {
                            @Override
                            public void run() {
                                mMapView.setViewpointGeometryAsync(mShapefileLayer.getFullExtent());
                            }
                        });
                    }
                    mArcGISMap.getOperationalLayers().add(mShapefileLayer);
//                    startDrawing();
                }
            }
        });

        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
        //mainShapefileLayer.setRenderer(renderer);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showShapefile();
        } else {
            // report to user that permission was denied
            Toast.makeText(this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFeature(final List<Feature>selectFeatureList){
        clearAllFeatureSelect();//清空选择

        int num = selectFeatureList.size();
        if(num ==0){
            Toast.makeText(this,"当前没有选中任何要素",Toast.LENGTH_SHORT).show();
        }
        if (num ==1){
            FeatureLayer layer = selectFeatureList.get(0).getFeatureTable().getFeatureLayer();
            layerName = layer.getName();
            setFeatureSelect(selectFeatureList.get(0));
        }
    }

    public void setFeatureSelect(Feature feature) {
        //设置要素选中
        FeatureLayer identifiedidLayer=feature.getFeatureTable().getFeatureLayer();
        identifiedidLayer.setSelectionColor(Color.YELLOW);
        identifiedidLayer.setSelectionWidth(20);
        identifiedidLayer.selectFeature(feature);

        //设置要素属性结果
        List<KeyAndValueBean> keyAndValueBeans = new ArrayList<>();
        List<KeyAndValueBean> keyAndValueBeans2 = new ArrayList<>();
        Map<String,Object> attributes= feature.getAttributes();
        for (Map.Entry<String, Object> entry:attributes.entrySet()){
            String key=entry.getKey();
            Object object = entry.getValue();
            String value ="";
            if (object!=null){
                value = String.valueOf(object);
            }
            KeyAndValueBean keyAndValueBean = new KeyAndValueBean();
            keyAndValueBean.setKey(key);
            keyAndValueBean.setValue(value);
            if(selectAttribute(key)){
                keyAndValueBeans.add(keyAndValueBean);
            }
            if(selectAttribute2(key)){
                keyAndValueBeans2.add(keyAndValueBean);
            }
        }
        //选中要素
        selectFeatureLayer = identifiedidLayer;
        selectFeature = feature;
        showMyDialog("aa","msgsa",keyAndValueBeans,keyAndValueBeans2,selectFeature);
    }

    private static int RESULT_LOAD_IMAGE = 10;
    //显示属性信息表（list_layout）
    public  void showMyDialog(String title, String msg, final List<KeyAndValueBean> keyAndValueBeans,final List<KeyAndValueBean> keyAndValueBeans2, Feature mSelectFeature) {
//        View listLayout = View.inflate(this,R.layout.list_layout, null);
        ListView listView1 = (ListView) findViewById(R.id.list_view1);
        TextView txtLayerName=(TextView) findViewById(R.id.textview_layer);
        txtLayerName.setText(layerName);
        attributeAdapter = new AttributeListAdapter(this, keyAndValueBeans, mSelectFeature);
        listView1.setAdapter(attributeAdapter);
        ListView listView2=(ListView)findViewById(R.id.list_view2);
        TextView dataText=(TextView) findViewById(R.id.dataTextView);

        attributeAdapter2 = new AttributeListAdapter2(this, keyAndValueBeans2, mSelectFeature);
        listView2.setAdapter(attributeAdapter2);

        // 获取系统当前时间
        Calendar calendar=Calendar.getInstance();
        // 将当前时间在TextView中显示出来
        int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
        String week1 = new String("日一二三四五六");
        String week2=week1.substring(week, week+1);
        dataText.setText(calendar.get(Calendar.YEAR)+"年  "+(calendar.get(Calendar.MONTH )+1)+"月  "
                +calendar.get(Calendar.DATE)+"日  "+"星期"+ week2);

        //主布局中引入属性布局，判断是否可见
//        if(isVisible){}
        listLayout.setVisibility(View.VISIBLE);

        //保存按钮
        ImageButton btnSave=(ImageButton)findViewById(R.id.imgBtn_edit_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureTable featureTable=selectFeatureLayer.getFeatureTable();
                if(featureTable.canUpdate(selectFeature)) {
                        ListenableFuture<Void> updateFeatureAsyc = featureTable.updateFeatureAsync(selectFeature);//添加要素
                        updateFeatureAsyc.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "属性保存成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                    Toast.makeText(MainActivity.this,"属性保存失败",Toast.LENGTH_SHORT).show();
                }
//                listLayout.setVisibility(View.GONE);
            }
        });

        //关闭按钮
        ImageButton btnCancel=(ImageButton)findViewById(R.id.imgBtn_edit_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listLayout.setVisibility(View.GONE);
                clearAllFeatureSelect();
                //关闭软键盘
                InputMethodManager inputMethodManager=(InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        });

        //选择照片按钮
        ImageButton btnChoosePhoto=(ImageButton)findViewById(R.id.imgBtn_choosePhoto);
        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击事件，而重定向到图片库
                
//

//                Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile());
//判断是否是AndroidN以及更高的版本
//                Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", apth);
//                File sdDir= Environment.getRootDirectory();

//                File sdDir=Environment.getExternalStorageDirectory();
//                File path=new File(sdDir+File.separator+"ArcGIS".toString().trim());
//                Intent intent = new Intent(Intent.ACTION_VIEW);
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    Log.e("1","ssds");
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apth);
//                    Log.e("2","ssds");
//                    Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.example.hp.attribute.fileProvider", path);
//                    intent.setDataAndType(contentUri, "image/*");
//                } else {
//                    intent.setDataAndType(Uri.fromFile(path), "image/*");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                startActivity(intent);
//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//                File file = new File(文件路径);
//                intent.setDataAndType(Uri.fromFile(path), "image/*");
//                startActivity(intent);

//                Intent intent=new Intent(Intent.ACTION_PICK,Uri.fromFile(path));
//                Intent intent=new Intent(Environment.getExternalStorageDirectory().toString() + File.separator + "ArcGIS".trim());
//                intent.setAction(Intent.ACTION_PICK);
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //这里要传一个整形的常量RESULT_LOAD_IMAGE到startActivityForResult()方法。
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
    }

    //设置要显示的字段
   /*                     Data type    length
点号 DH    HN001          text   10位
地点 DD 海南省海口市秀英区text
矿种KDLX                 text    5
矿山名称KDMC             text   100
开采方式KDKCFS           text   5
开采状态KDKCZT           text   5
违法类型KDCZWT(存在问题)  text   2

野外点编号YWDBH   年月日+今天第几个2017010201   text  10位数
野外点经度YWDJD     float（6位小数）
野外点纬度 YWDWD    float（6位小数）
野外点高程YWDGC     float  2位小数
镜头指向JTZX        text  （东南 西北。。）   10
照片名称ZPMC        text
照片时间ZPSJ     年月日时分秒20170102 15：27：00   text  20位数

描述MS              text   500
与解译结果对比JGDB  text  3    （选择：对 错 漏）
类型LX             text  9    （选择：新建  已更改）
是否已查证SFYCZ    text  3    （选择：是 否（空？））
 */
    String[] fieldNames = new String[]{"DH","DD","KDLX","KDMC","KDKCFS","KDKCZT","KDCZWT","MS","JGDB","LX","SFYCZ"};
    public boolean selectAttribute(String fieldName){
        List<String> strings = Arrays.asList(fieldNames);
        return strings.contains(fieldName);
    }
    String[] fieldNames2 = new String[]{"YWDBH","YWDJD","YWDWD","YWDGC","JTZX","ZPMC"};
    public boolean selectAttribute2(String fieldName){
        List<String> strings = Arrays.asList(fieldNames2);
        return strings.contains(fieldName);
    }

    //清空选择
    public void clearAllFeatureSelect(){
        List<Layer> layers = mMapView.getMap().getOperationalLayers();
        for (int i=0;i<layers.size();i++){
            FeatureLayer featureLayer = (FeatureLayer)layers.get(i);
            featureLayer.clearSelection();
        }
    }
    
    //用户选择一张图片，onActivityResult()方法将会被调用，
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        
            Log.e("3","ssds");
            //我们需要判断requestCode是否是我们之前传给startActivityForResult()方法的RESULT_LOAD_IMAGE，并且返回的数据不能为空
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                Log.e("4","ssds");
                Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

//            File sdDir=Environment.getExternalStorageDirectory();
//            File apth=new File(sdDir+File.separator+"ArcGIS".toString().trim());
//            String[] filePathColumn={apth.toString()};
            //获取d盘中a文件夹下所有的图片路径
//            string[] dirs = Directory.GetFiles (@"d:\\a", ImageType [i]);
//            for (int j = 0; j < dirs.Length; j++) {
//                filePaths.Add (dirs [j]);

//                String[] filePathColumn={Environment.getExternalStorageDirectory().toString() + File.separator + "ArcGIS".trim()};
            //查询我们需要的数据
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
//            String picturePath = Environment.getExternalStorageDirectory().toString() + File.separator
//                    + "ArcGIS";
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            TextView imageText=(TextView)findViewById(R.id.imageText);
            imageText.setText("");

            File f = new File(picturePath);
            pictureName =  f.getName();
            attributeAdapter2.setPhotoName(pictureName);
            showShapefile();
            
         DH=attributeAdapter.getdh();
         imageText.setText("名称" +DH);
         
        }
    }

    //物理退出键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            listLayout.setVisibility(View.GONE);
            clearAllFeatureSelect();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
