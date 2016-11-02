package com.spvan.spvanwebsite.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.spvan.spvanwebsite.ActivityMainBinding;
import com.spvan.spvanwebsite.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
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

    private final  String baseUrl = "609bb3f9.s501.now.top";

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
        UpdateCode();

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
        webView.loadUrl("http://"+baseUrl+"/wapshop");
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if(view.getUrl().contains("ProductDetails"))
                {
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


    public void UpdateCode(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://api.fir.im/apps/latest/com.spvan.spvanwebsite?api_token=6ff3c5dc2d03addc9dce664612be62ca&type=android").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("更新获取错误,请检查网络");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

               ShowDialog(response.body().string());

            }
        });
    }

    public  void GetImages(String idStr)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+baseUrl+"/API/VshopProcess.ashx?action=GetProductImg&productId="+idStr).build();
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
                    titleDetail = adapter.Title;

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

    private int old_Verson = 1;
    public void ShowDialog(final  String data)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                PackageManager manager = MainActivity.this.getPackageManager();
                PackageInfo info;
                try {
                    info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);
                    old_Verson = info.versionCode;
                    Gson gson = new Gson();
                    UpdateAdapter adapter = gson.fromJson(data,UpdateAdapter.class);
                    if(!TextUtils.isEmpty(adapter.name))
                    {
                        String gx_rizi = adapter.changelog;
                        String gx_ver = adapter.versionShort;
                        String gx_version = adapter.version;
//                        String apkUrl = adapter.install_url;
                        String apkUrl = "http://609bb3f9.s501.now.top/app/naboya_jiagu_sign.apk";
                        String vername = "_"
                                + adapter.versionShort;
                        // //判断版本号是不是最新的
                        if (Double.valueOf(gx_version) > old_Verson) {
                            Utils_GX.dialog_gengxing(gx_rizi,
                                    gx_ver, gx_version, apkUrl,
                                    vername, MainActivity.this,adapter.name);
                        }

                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        private String Title;


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

    public static  class UpdateAdapter{

        /**
         * name : 纳伯雅
         * version : 1
         * changelog : 1、修复发布bug
         * updated_at : 1473320469
         * versionShort : 1.0
         * build : 1
         * installUrl : http://download.fir.im/v2/app/install/57d11126ca87a865bb00101f?download_token=a9d63ec2ef0275a27da568b6e60b5dfd
         * install_url : http://download.fir.im/v2/app/install/57d11126ca87a865bb00101f?download_token=a9d63ec2ef0275a27da568b6e60b5dfd
         * direct_install_url : http://download.fir.im/v2/app/install/57d11126ca87a865bb00101f?download_token=a9d63ec2ef0275a27da568b6e60b5dfd
         * update_url : http://fir.im/kc69
         * binary : {"fsize":2100555}
         */

        private String name;
        private String version;
        private String changelog;
        private int updated_at;
        private String versionShort;
        private String build;
        private String installUrl;
        private String install_url;
        private String direct_install_url;
        private String update_url;
        /**
         * fsize : 2100555
         */

        private BinaryBean binary;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getChangelog() {
            return changelog;
        }

        public void setChangelog(String changelog) {
            this.changelog = changelog;
        }

        public int getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(int updated_at) {
            this.updated_at = updated_at;
        }

        public String getVersionShort() {
            return versionShort;
        }

        public void setVersionShort(String versionShort) {
            this.versionShort = versionShort;
        }

        public String getBuild() {
            return build;
        }

        public void setBuild(String build) {
            this.build = build;
        }

        public String getInstallUrl() {
            return installUrl;
        }

        public void setInstallUrl(String installUrl) {
            this.installUrl = installUrl;
        }

        public String getInstall_url() {
            return install_url;
        }

        public void setInstall_url(String install_url) {
            this.install_url = install_url;
        }

        public String getDirect_install_url() {
            return direct_install_url;
        }

        public void setDirect_install_url(String direct_install_url) {
            this.direct_install_url = direct_install_url;
        }

        public String getUpdate_url() {
            return update_url;
        }

        public void setUpdate_url(String update_url) {
            this.update_url = update_url;
        }

        public BinaryBean getBinary() {
            return binary;
        }

        public void setBinary(BinaryBean binary) {
            this.binary = binary;
        }

        public static class BinaryBean {
            private int fsize;

            public int getFsize() {
                return fsize;
            }

            public void setFsize(int fsize) {
                this.fsize = fsize;
            }
        }
    }
}
