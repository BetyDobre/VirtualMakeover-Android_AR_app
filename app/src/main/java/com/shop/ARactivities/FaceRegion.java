package com.shop.ARactivities;

import android.content.Context;

import com.shop.ARactivities.rendering.ObjectRenderer;

import java.io.IOException;


public class FaceRegion {

    public AugmentedFaceNode.FaceLandmark faceLandmark;

    public FaceRegion(AugmentedFaceNode.FaceLandmark faceLandmark) {
        this.faceLandmark = faceLandmark;
    }

    private ObjectRenderer objectRenderer = new ObjectRenderer();
    private float scaleFactor  = 1.0f;
    private static float[] DEFAULT_COLOR = {0f, 0f, 0f, 0f};

    public void setRenderable(Context context, String modelName, String modelTexture) throws IOException {
        objectRenderer.createOnGlThread(/*context=*/context, modelName, modelTexture);
        objectRenderer.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f);
        objectRenderer.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending);
    }

    public void draw(float[] objectMatrix, float[] viewMatrix, float[] projectionMatrix, float[] colorCorrectionRgba){
        objectRenderer.updateModelMatrix(objectMatrix, scaleFactor);
        objectRenderer.draw(
                viewMatrix,
                projectionMatrix,
                colorCorrectionRgba,
                DEFAULT_COLOR
        );
    }
}
