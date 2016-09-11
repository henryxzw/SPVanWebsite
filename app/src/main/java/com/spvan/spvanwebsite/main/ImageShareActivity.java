package com.spvan.spvanwebsite.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.repacked.apache.commons.codec.digest.Md5Crypt;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.spvan.spvanwebsite.ActivityShareBinding;
import com.spvan.spvanwebsite.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by apple on 16/9/10.
 */

public class ImageShareActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{
    private ActivityShareBinding binding;
    private ArrayList<String> imageList,pathList;
//    private
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activit_image_share);

       imageList =   getIntent().getStringArrayListExtra("imagelist");
        if(imageList == null)
        {
            imageList = new ArrayList<>();
        }
        pathList = new ArrayList<>();

        binding.toolBar.setTitle("请选择要分享的图片");
        binding.toolBar.setTitleTextColor(Color.BLACK);
        binding.toolBar.setNavigationIcon(R.mipmap.ic_back);
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.toolBar.getMenu().add(0,R.id.menu_share,0 ,"全选").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        binding.toolBar.setOnMenuItemClickListener(this);

        InitView();

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId()==R.id.menu_share)
        {
            ArrayList<Uri> imageUris = new ArrayList();
            for(int i=0;i<pathList.size();i++)
            {
                imageUris.add(Uri.parse("file://"+pathList.get(i)));
            }
            Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "Share images to.."));
        }
        return false;
    }


    public void InitView()
    {
         for(int i=0;i<imageList.size();i++)
         {
             Uri uri = Uri.parse(imageList.get(i));
             SimpleDraweeView draweeView = null;
             switch (i)
             {
                 case 0:
                     draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view1);
                     break;
                 case 1:
                     draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view2);
                     break;
                 case 2:
                     draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view3);
                     break;
                 case 3:
                     draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view4);
                     break;
                 case 4:
                     draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view5);
                     break;
             }
             final  ImageView iv = draweeView;
             Picasso.with(this).load(uri).into(draweeView, new Callback() {
                 @Override
                 public void onSuccess() {
//                     ImageView imageView;
                     Bitmap bitmap =((BitmapDrawable)iv.getDrawable()).getBitmap();
                     SavePict(bitmap);
                 }

                 @Override
                 public void onError() {

                 }
             });
         }
    }

    public void SavePict(Bitmap bm)
    {

        String path1 = Environment.getExternalStorageDirectory().getAbsolutePath();

//        File file = new File(path1);
//
//        if(!file.exists())
//        {
//            boolean ismake = file.mkdir();
//            if(ismake)
//            {
//                Toast.makeText(this,"chengg",Toast.LENGTH_LONG).show();
//            }
//            else
//            {
//                Toast.makeText(this,"shibai",Toast.LENGTH_LONG).show();
//            }
//        }

        try
        {
            String path = path1+File.separator+ System.currentTimeMillis()+".jpeg";
            FileOutputStream out = new FileOutputStream(new File(path));
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i("tag", "已经保存");
            pathList.add(path);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
