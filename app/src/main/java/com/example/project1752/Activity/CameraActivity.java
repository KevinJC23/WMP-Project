package com.example.project1752.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.project1752.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.hardware.camera2.*;

public class CameraActivity extends AppCompatActivity {

    private TextureView textureView;
    private Button btnCapture;
    private ImageView btnBack;

    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;

    private String cameraId;
    private File capturedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textureView = findViewById(R.id.textureView);
        btnCapture = findViewById(R.id.btnCapture);
        btnBack = findViewById(R.id.btnBack);

        // Handle back button click
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CameraActivity.this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // Check and request permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        // Initialize TextureView
        textureView.setSurfaceTextureListener(textureListener);

        // Set Capture Button action
        btnCapture.setOnClickListener(v -> takePicture());
    }

    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
    };

    private void openCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

            imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, null);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
                return;
            }
            cameraManager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            cameraCaptureSession = session;
                            try {
                                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(CameraActivity.this, "Configuration failed", Toast.LENGTH_SHORT).show();
                        }
                    }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = reader -> {
        Image image = reader.acquireLatestImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        saveImage(bytes);
        image.close();
    };

    private void takePicture() {
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            cameraCaptureSession.capture(captureRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void saveImage(byte[] bytes) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        capturedImageFile = new File(storageDir, "profile_image.jpg");

        if (capturedImageFile.exists()) {
            boolean deleted = capturedImageFile.delete();
            if (deleted) {
                Log.d("CameraActivity", "Previous image deleted.");
            } else {
                Log.e("CameraActivity", "Failed to delete previous image.");
            }
        }

        try (FileOutputStream fos = new FileOutputStream(capturedImageFile)) {
            // Write the bytes to file
            fos.write(bytes);

            // Correct the orientation
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap rotatedBitmap = rotateBitmap(bitmap);

            // Save the rotated image
            FileOutputStream out = new FileOutputStream(capturedImageFile);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            // Save the file path in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("ProfileImagePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profileImagePath", capturedImageFile.getAbsolutePath());
            editor.apply();

            // Go back to ProfileActivity
            Intent intent = new Intent(CameraActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // Rotate 90 degrees clockwise
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    @Override
    protected void onPause() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        super.onPause();
    }
}
