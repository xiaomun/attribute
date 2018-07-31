package com.example.hp.attribute.scanpic;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.attribute.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanActivity extends Activity {

    private static final String TAG = "scan";
    private List<MediaBean> mediaBeen;
    private ScanAdapter adapter;
    private GridView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initUI();
        initData();
    }

    private void initData() {
        getAllPhotoInfo();
    }

    private void initUI() {
        mediaBeen = new ArrayList<>();

        listview = findViewById(R.id.listview);
        adapter = new ScanAdapter(this,mediaBeen);
        listview.setAdapter(adapter);
    }

    private void getAllPhotoInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                List<MediaBean> mediaBeen = new ArrayList<>();
                HashMap<String,List<MediaBean>> allPhotosTemp = new HashMap<>();//所有照片
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String picturePath = Environment.getExternalStorageDirectory().toString() + File.separator + "ArcGIS";
                String[] projImage = { MediaStore.Images.Media._ID
                        , MediaStore.Images.Media.DATA
                        ,MediaStore.Images.Media.SIZE
                        ,MediaStore.Images.Media.DISPLAY_NAME};
                final Cursor mCursor = getContentResolver().query(mImageUri,
                        projImage,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED+" desc");

                if(mCursor!=null){
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE))/1024;
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //用于展示相册初始化界面
                        if(path.contains(picturePath)){
                            mediaBeen.add(new MediaBean(path,size,displayName));
                        }

//                        // 获取该图片的父路径名
//                        String dirPath = new File(path).getParentFile().getAbsolutePath();
//
//                        //存储对应关系
//                        if (allPhotosTemp.containsKey(dirPath)) {
//                            List<MediaBean> data = allPhotosTemp.get(dirPath);
//                            data.add(new MediaBean(path,size,displayName));
////                            Log.e(TAG,"getAllPhotoInfo  "+data.size()+",path="+data.get(0).getPath()+",name="+data.get(0).getDisplayName());
//                            continue;
//                        } else {
//                            List<MediaBean> data = new ArrayList<>();
//                            data.add(new MediaBean(path,size,displayName));
//                            allPhotosTemp.put(dirPath,data);
////                            Log.e(TAG,"getAllPhotoInfo  else "+data.size()+",path="+data.get(0).getPath()+",name="+data.get(0).getDisplayName());
//                        }
                    }
                    mCursor.close();
                }
                //更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //...
                        adapter.notifyDataSetChanged();
                        Log.e(TAG,"mediaBeen="+mediaBeen.size());
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(ScanActivity.this,"选中"+position+mediaBeen.get(position).displayname,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
