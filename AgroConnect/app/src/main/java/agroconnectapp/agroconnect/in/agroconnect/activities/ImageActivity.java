package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import agroconnectapp.agroconnect.in.agroconnect.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        String url = getIntent().getStringExtra("url");
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
        if(url != null)
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.drawable.crop_protection_placeholder)
                    .error(R.drawable.crop_protection_placeholder)
                    .into(imageView);
        mAttacher.update();
    }
}
