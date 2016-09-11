package com.spvan.spvanwebsite.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.internal.Excluder;
import com.spvan.spvanwebsite.ActivityMainBinding;
import com.spvan.spvanwebsite.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity implements PlatformActionListener {

    private ActivityMainBinding binding;
    private WebView webView;

    private long timeTicks = 0;
    private boolean isFirstLauncher = true;

    private String titleDetail ="";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        if(SDK_INT<= Build.VERSION_CODES.KITKAT) {
            this.getWindow().getDecorView().setFitsSystemWindows(true);
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        webView = binding.webView;
        ShareSDK.initSDK(this);
        InitView();

    }

    public void InitView()
    {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true); // 支持缩放

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl("http://121.201.5.229:8008/wapShop/");
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if(view.getUrl().contains("ProductDetails"))
                {
                    titleDetail  = title;
                    binding.toolBar.setVisibility(View.VISIBLE);
                }
                else
                {

                }
            }


            @Override
           public void onProgressChanged(WebView view, int newProgress) {
               binding.progressBar.setProgress(newProgress);
               if(view.getUrl().contains("ProductDetails"))
               {

                   binding.toolBar.setVisibility(View.VISIBLE);
               }
                else
               {
                 binding.toolBar.setVisibility(View.INVISIBLE);
               }
               if(newProgress==100)
               {
                   binding.progressBar.setVisibility(View.GONE);
                   if(isFirstLauncher)
                   {
                       CountDownTimer timer = new CountDownTimer(2000,1000) {
                           @Override
                           public void onTick(long l) {

                           }

                           @Override
                           public void onFinish() {
                               TranslateAnimation translateAnimation = new TranslateAnimation(0,5000,0,0);
                               translateAnimation.setDuration(2000);
                               translateAnimation.setInterpolator(new AccelerateInterpolator());
                               translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                   @Override
                                   public void onAnimationStart(Animation animation) {

                                   }

                                   @Override
                                   public void onAnimationEnd(Animation animation) {
                                           binding.ivBg.setVisibility(View.GONE);
                                       isFirstLauncher = false;
                                   }

                                   @Override
                                   public void onAnimationRepeat(Animation animation) {

                                   }
                               });
                               binding.ivBg.startAnimation(translateAnimation);

                           }
                       };
                       timer.start();
                   }
               }
               else
               {
                   binding.progressBar.setVisibility(View.VISIBLE);
               }
               super.onProgressChanged(view, newProgress);
           }
       });
        webView.setWebViewClient(new WebViewClient());


        binding.toolBar.getMenu().add(0,R.id.menu_share,0,"").setIcon(R.mipmap.ic_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        binding.toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.menu_share)
                {
                    Log.e("tag","当前要分享的url链接为："+webView.getUrl());
                    String id = Uri.parse(webView.getUrl()).getQueryParameter("productId");
                    if(TextUtils.isEmpty(id))
                    {
                        Toast.makeText(MainActivity.this,"参数获取错误，无法分享",Toast.LENGTH_LONG).show();
                    }
                    else {
                        GetImages(id);
                    }

                }
                return false;
            }
        });
        binding.toolBar.setNavigationIcon(R.mipmap.ic_back);
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toolBar.setVisibility(View.GONE);
                webView.goBack();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else {
            if(timeTicks==0 || (System.currentTimeMillis()-timeTicks)>2000)
            {
                Toast.makeText(this,"再按一次退出软件",Toast.LENGTH_LONG).show();

                timeTicks = System.currentTimeMillis();
            }
            else
            {
                super.onBackPressed();
            }

        }
    }



    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(this,"分享成功",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(this,"分享失败！"+throwable.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(this,"取消分享",Toast.LENGTH_LONG).show();
    }

    public  void GetImages(String idStr)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://121.201.5.229:8008/API/VshopProcess.ashx?action=GetProductImg&productId="+idStr).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("分享失败,获取图片错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Share( response.body().string());

            }
        });
    }
    private Platform.ShareParams shareParams;
    public void  Share(final  String htmlstr)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    Gson gson = new Gson();
                    ImageAdapter adapter = gson.fromJson(htmlstr, ImageAdapter.class);
                    ArrayList<String> imageUris = new ArrayList();
                    imageUris.add(adapter.getImageUrl1());
                    imageUris.add(adapter.getImageUrl2());
                    imageUris.add(adapter.getImageUrl3());
                    imageUris.add(adapter.getImageUrl4());
                    imageUris.add(adapter.getImageUrl5());

                    Intent intent = new Intent(MainActivity.this,ImageShareActivity.class);
                    intent.putExtra("title",titleDetail);
                    intent.putStringArrayListExtra("imagelist",imageUris);
                    startActivity(intent);
                }catch (Exception ex)
                {
                    Toast.makeText(MainActivity.this,"分享失败,获取图片错误",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void  showToast(final String msg)
    {
       handler.post(new Runnable() {
           @Override
           public void run() {
               Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
           }
       });
    }
    public class  ImageAdapter{

        /**
         * ImageUrl1 : http://121.201.5.229:8008//storage/master/product/images/201608262339072281210.jpg
         * ImageUrl2 : http://121.201.5.229:8008//storage/master/product/images/201608262339105386360.jpg
         * ImageUrl3 : http://121.201.5.229:8008//storage/master/product/images/201608262339131011400.jpg
         * ImageUrl4 : http://121.201.5.229:8008//storage/master/product/images/201608262339159409860.jpg
         * ImageUrl5 :
         */

        private String ImageUrl1;
        private String ImageUrl2;
        private String ImageUrl3;
        private String ImageUrl4;
        private String ImageUrl5;

        public String getImageUrl1() {
            return ImageUrl1;
        }

        public void setImageUrl1(String ImageUrl1) {
            this.ImageUrl1 = ImageUrl1;
        }

        public String getImageUrl2() {
            return ImageUrl2;
        }

        public void setImageUrl2(String ImageUrl2) {
            this.ImageUrl2 = ImageUrl2;
        }

        public String getImageUrl3() {
            return ImageUrl3;
        }

        public void setImageUrl3(String ImageUrl3) {
            this.ImageUrl3 = ImageUrl3;
        }

        public String getImageUrl4() {
            return ImageUrl4;
        }

        public void setImageUrl4(String ImageUrl4) {
            this.ImageUrl4 = ImageUrl4;
        }

        public String getImageUrl5() {
            return ImageUrl5;
        }

        public void setImageUrl5(String ImageUrl5) {
            this.ImageUrl5 = ImageUrl5;
        }
    }
}
