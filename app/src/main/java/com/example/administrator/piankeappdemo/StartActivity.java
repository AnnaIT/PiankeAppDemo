package com.example.administrator.piankeappdemo;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.piankeappdemo.baseclass.AppApplication;
import com.example.administrator.piankeappdemo.baseclass.BaseActivity;
import com.example.administrator.piankeappdemo.internet.InternetConnect;
import com.example.administrator.piankeappdemo.internet.MyAllHttpMethod;
import com.example.administrator.piankeappdemo.toolsclass.GsonUtils;
import com.example.administrator.piankeappdemo.toolsclass.NetworkUtils;
import com.example.administrator.piankeappdemo.toolsclass.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * 由于不能在主线程中访问网络，这里利用handler
 */
public class StartActivity extends BaseActivity {
    private SimpleDraweeView mImageviewStart;
    private String urlString;
    private final int HANDLER_MESSAGE_WHAT = 0x22;
    private final int HANDLER_MESSAGE_WHAT_END = 0x23;
    private Bitmap bitmap;
    private boolean isStartThread=true;
    private Animator objanimator;
    private TextView mTextview;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_MESSAGE_WHAT:
                    mImageviewStart.setImageURI(Uri.parse(urlString));
                    mTextview.setText("世界很美，而正好你有空");
                    objanimator.start();
                    isStartThread = false;
                    //跳转到mainactivity
                    handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_WHAT_END,4000);//1秒后开启主界面
                    break;

                case HANDLER_MESSAGE_WHAT_END:
                    Intent intentmain = new Intent(AppApplication.getApplication(),MainActivity.class);
                    startActivity(intentmain);
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
         mImageviewStart = (SimpleDraweeView ) findViewById(R.id.image_fresco);
        mTextview = (TextView) findViewById(R.id.text_start);
        //图片动画
         objanimator = AnimatorInflater.loadAnimator(AppApplication.getApplication(),R.animator.anim_img_scale_start);
        objanimator.setTarget(mImageviewStart);
//        //创建图片
//        mImageviewStart = new ImageView(this);
//        mImageviewStart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));//设置宽高
//        mImageviewStart.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //判断网络wifi是否连接,在子线程中访问网络
        if(NetworkUtils.isWifiConnected()){
            //连接网络获取图片url,并设置到图片
            startThread();//开启线程
        }else {
            mImageviewStart.setImageResource(R.mipmap.about);
            objanimator.start();
        }

        Log.d("response", NetworkUtils.isWifiConnected() + "");
    }


    /**
     * 开启线程访问网络
     */
    private void startThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStartThread){

                    try {
                        getInterImageUrl();//进行网络连接
                        URL url = new URL(urlString);
                        Log.d("url", "url:"+urlString);
//                        InputStream is=url.openStream();
//                        bitmap= BitmapFactory.decodeStream(is);
//                        is.close();
                        handler.sendEmptyMessage(HANDLER_MESSAGE_WHAT);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }





            }
        }).start();

    }

    /**
     * 得到网络img的url
     */
    private void getInterImageUrl() {

        HashMap<String,String> params =  new HashMap<String, String>();
        MyAllHttpMethod.getStartImg(params, new InternetConnect.onConnectionListener() {
            @Override
            public void onNullInternet() {
                ToastUtils.showToast("没有网络，请打开网络！");
            }

            @Override
            public void onFailConnection(int errorCode) {
                ToastUtils.showToast("网络连接错误" + errorCode);
            }

            @Override
            public void onSuccess(String response) {
                //成功后。将返回的json数据进行解析,可以通过所写的gsonutil工具解析json
                JSONObject object = null;//建立response的jsonobject
                try {
                    object = new JSONObject(response);
                    // JSONArray array = object.getJSONArray("data");
                    JSONObject array = object.getJSONObject("data");
                    //解析好json并存到list中
                     urlString = array.getString("picurl");

//                    mImageviewStart.setImageURI(Uri.parse(url));//将网络获得到的图片url设置到图片

                } catch (JSONException e) {
                e.printStackTrace();
            }

            }
        });
    }


}
