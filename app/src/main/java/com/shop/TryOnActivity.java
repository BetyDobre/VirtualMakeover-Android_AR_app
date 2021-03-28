package com.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;


public class TryOnActivity extends AppCompatActivity {

    private ArFragment arCam;
    private int clickNo = 0, source = 0;
    private String productID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on);

        productID = getIntent().getStringExtra("pid");
        arCam = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arCameraArea);
        checkSystemSupport(TryOnActivity.this);

        arCam.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            clickNo++;

            // the 3d model comes to the scene only the first time we tap the screen
            if (clickNo == 1) {
                Anchor anchor = hitResult.createAnchor();

                if(productID.equals("24-03-202113:23:53 PM")){
                    source = R.raw.box;
                }
                else if (productID.equals("25-03-202117:10:34 PM")){
                    source = R.raw.vase1;
                }
                else if(productID.equals("28-03-202112:50:24 PM")){
                    source = R.raw.vase2;
                }
                else if (productID.equals("27-03-202118:54:57 PM")){
                    source = R.raw.tabledecoration;
                }
                else{
                    source = R.raw.basketball;
                }

                ModelRenderable.builder()
                        .setSource(this, source)
                        .setIsFilamentGltf(true)
                        .build()
                        .thenAccept(modelRenderable -> addModel(anchor, modelRenderable))
                        .exceptionally(throwable -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Something is wrong" + throwable.getMessage()).show();
                            return null;
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TryOnActivity.this, ProductDetailsActivity.class);
        intent.putExtra("pid", productID);
        startActivity(intent);
    }

    private void addModel(Anchor anchor, ModelRenderable modelRenderable) {

        // Creating a AnchorNode with a specific anchor
        AnchorNode anchorNode = new AnchorNode(anchor);
        // attaching the anchorNode with the ArFragment
        anchorNode.setParent(arCam.getArSceneView().getScene());
        TransformableNode transform = new TransformableNode(arCam.getTransformationSystem());
        transform.getScaleController().setMinScale((float) 0.9);
        transform.getScaleController().setMaxScale((float) 1);
        // attaching the anchorNode with the TransformableNode
        transform.setParent(anchorNode);

        // attaching the 3d model with the TransformableNode that is already attached with the node
        transform.setRenderable(modelRenderable);
        transform.select();
    }

    public static boolean checkSystemSupport(Activity activity) {

        // checking whether the API version of the running Android >= 24
        // that means Android Nougat 7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();

            // checking whether the OpenGL version >= 3.0
            if (Double.parseDouble(openGlVersion) >= 3.0) {
                return true;
            } else {
                Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        } else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }
}
