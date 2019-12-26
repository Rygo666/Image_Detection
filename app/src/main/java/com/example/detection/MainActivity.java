package com.example.detection;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.Duration;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Bitmap mBitmap;
    private ImageView mImageView;
    private TextView mTextView;
    private DatabaseReference baza;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        mImageView = findViewById(R.id.imageView);
        findViewById(R.id.btn_device).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        mTextView.setText(null);
        final String d = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        switch (view.getId()) {
            case R.id.btn_device:
                if (mBitmap != null) {
                    FirebaseVisionOnDeviceImageLabelerOptions options = new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                            .setConfidenceThreshold(0.7f)
                            .build();
                    final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);
                    baza = FirebaseDatabase.getInstance().getReference();
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler(options);
                    labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            for (FirebaseVisionImageLabel label : labels) {
                                mTextView.append("Объект на фото:\n");
                                mTextView.append(label.getText() + "\n");
                                mTextView.append("Уровень уверенности:\n");
                                mTextView.append(label.getConfidence() + "\n\n");
                                baza.child("Image").child(d).child(label.getText()).setValue(label.getConfidence());




                            }

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mTextView.setText(e.getMessage());
                        }
                    });

                }
        }

    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == RESULT_OK) {
             switch (requestCode) {
                 case RC_STORAGE_PERMS1:
                     checkStoragePermission(requestCode);
                     break;
                 case RC_SELECT_PICTURE:
                     Uri dataUri = data.getData();
                     String path = MyHelper.getPath(this, dataUri);
                     String str = dataUri.getPath();

                     if (path == null) {
                         mBitmap = MyHelper.resizeImage(imageFile, path, mImageView);
                     } else {
                         mBitmap = MyHelper.resizeImage(imageFile, path, mImageView);
                     }
                     if (mBitmap != null) {
                         mTextView.setText(null);
                         mImageView.setImageBitmap(mBitmap);
                         Log.i("Loggg:", str);
                     }
                     break;

             }
         }
    }

}