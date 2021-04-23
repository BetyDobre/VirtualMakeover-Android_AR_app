package com.shop.ARactivities;

import android.content.Context;

import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;

import java.io.IOException;
import java.util.HashMap;

public class AugmentedFaceNode {
    private AugmentedFaceRenderer augmentedFaceRenderer = new AugmentedFaceRenderer();
    private HashMap<FaceLandmark, FaceRegion> faceLandmarks = new HashMap<>();
    private Boolean renderFaceMesh = false;

    private Context context;
    private AugmentedFace augmentedFace;

    enum FaceLandmark {
        FOREHEAD_RIGHT,
        FOREHEAD_LEFT,
        NOSE_TIP
    }

    public AugmentedFaceNode(AugmentedFace aug, Context con) {
        context = con;
        augmentedFace = aug;

        renderFaceMesh = false;
        augmentedFaceRenderer.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f);
    }

    public void setRegionModel(FaceLandmark faceLandmark, String modelName, String modelTexture) throws IOException {
        FaceRegion faceRegion = new FaceRegion(faceLandmark);
        faceRegion.setRenderable(context, modelName, modelTexture);
        faceLandmarks.put(faceLandmark, faceRegion);
    }

    public void setFaceMeshTexture(String assetName) throws IOException {
        augmentedFaceRenderer.createOnGlThread(context, assetName);
        renderFaceMesh = true;
    }

    public void onDraw(float[] projectionMatrix, float[] viewMatrix, float[] colorCorrectionRgba){
        if (augmentedFace != null){
            if (augmentedFace.getTrackingState() != TrackingState.TRACKING) {
                return;
            }

            if (renderFaceMesh){
                float[] modelMatrix = new float[16];
                augmentedFace.getCenterPose().toMatrix(modelMatrix, 0);
                augmentedFaceRenderer.draw(projectionMatrix, viewMatrix, modelMatrix, colorCorrectionRgba, augmentedFace);
            }

            for (FaceRegion region:faceLandmarks.values()) {
                float[] objectMatrix = new float[16];
                getRegionPose(region.faceLandmark).toMatrix(objectMatrix, 0);
                region.draw(objectMatrix, viewMatrix, projectionMatrix, colorCorrectionRgba);
            }
        }
    }

    private Pose getRegionPose(FaceLandmark faceLandmark) {
        switch (faceLandmark){
            case NOSE_TIP:
                return augmentedFace.getRegionPose(AugmentedFace.RegionType.NOSE_TIP);
            case FOREHEAD_LEFT:
                return augmentedFace.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT);
            case FOREHEAD_RIGHT:
                return augmentedFace.getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT);
            default:
                return augmentedFace.getRegionPose(AugmentedFace.RegionType.NOSE_TIP);
        }
    }
}
