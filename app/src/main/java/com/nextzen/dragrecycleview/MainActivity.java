package com.nextzen.dragrecycleview;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pronabsen.dragrecyclerview.DragRecyclerView;
import com.pronabsen.dragrecyclerview.listener.SimpleDragListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DragRecyclerView profilePhotoList;
    List<UserMultiplePhoto> imagesList;
    ProfilePhotosAdapter profilePhotosAdapter;


    String imageBas64;
    List<String> selectedImagesList = new ArrayList<>();
    UserMultiplePhoto from_image_model;
    UserMultiplePhoto to_image_model;
    int toPos, fromPos;
    int currentPosition = 0;


    Date c;
    SimpleDateFormat df;
    int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy", Locale.getDefault());
        currentYear = Integer.parseInt(df.format(c));


        profilePhotoList = findViewById(R.id.Profile_photos_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        profilePhotoList.setLayoutManager(layoutManager);
        profilePhotoList.setHasFixedSize(false);

        imagesList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            imagesList.add(new UserMultiplePhoto());
        }


        profilePhotosAdapter = new ProfilePhotosAdapter(MainActivity.this, imagesList, true, new ProfilePhotosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserMultiplePhoto item, int postion, View view) {
                currentPosition = postion;
                switch (view.getId()) {
                    case R.id.add_btn:
                        selectImage();
                        break;

                    case R.id.cross_btn:
                        for (int j = 0; j < selectedImagesList.size(); j++) {
                            if (item.getImage().equals(selectedImagesList.get(j))) {
                                selectedImagesList.remove(j);
                                break;
                            }
                        }
                        imagesList.remove(postion);
                        imagesList.add(new UserMultiplePhoto());
                        profilePhotosAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });


        profilePhotosAdapter.setOnItemDragListener(new SimpleDragListener() {
            @Override
            public void onDrop(int fromPosition, int toPosition) {
                super.onDrop(fromPosition, toPosition);
                from_image_model = imagesList.get(fromPosition);
                to_image_model = imagesList.get(toPosition);

                toPos = toPosition;
                fromPos = fromPosition;

                if (to_image_model.getImage() == null || from_image_model.getImage() == null) {
                    imagesList.remove(toPosition);
                    imagesList.add(toPosition, from_image_model);

                    imagesList.remove(from_image_model);
                    imagesList.add(fromPosition, to_image_model);
                }
                profilePhotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSwiped(int pos) {
                super.onSwiped(pos);
            }
        });

        profilePhotoList.setAdapter(profilePhotosAdapter);


    }

    // open the gallery when user press button to upload a picture
    private void selectImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultCallback.launch(intent);

    }

    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        beginCrop(selectedImage);
                    }
                }
            });


    // bottom there function are related to crop the image
    private void beginCrop(Uri source) {
        Intent intent = CropImage.activity(source).setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1).getIntent(MainActivity.this);
        cropResultCallback.launch(intent);
    }

    ActivityResultLauncher<Intent> cropResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        CropImage.ActivityResult result1 = CropImage.getActivityResult(data);
                        handleCrop(result1.getUri());
                    }
                }
            });


    private void handleCrop(Uri userImageUri) {
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(userImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);

        String path = userImageUri.getPath();
        Matrix matrix = new Matrix();
        android.media.ExifInterface exif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                exif = new android.media.ExifInterface(path);
                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        imageBas64 = bitmapToBase64(MainActivity.this, rotatedBitmap);
        selectedImagesList.add(imageBas64);
        for (int i = 0; i < imagesList.size(); i++) {
            UserMultiplePhoto model = imagesList.get(i);
            if (imagesList.get(currentPosition).getImage() != null && !imagesList.get(currentPosition).getImage().equals("")) {
                imagesList.remove(i);
                model.setImage(imageBas64);
                model.setOrderSequence(i);
                imagesList.add(i, model);
                break;
            } else if (model.getImage() == null || model.getImage().equals("")) {
                imagesList.remove(i);
                model.setImage(imageBas64);
                model.setOrderSequence(i);
                imagesList.add(i, model);
                break;
            }
        }

    }

    public static String bitmapToBase64(Activity activity, Bitmap imageBitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos .toByteArray();
        String base64= Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

}