package com.shop.ARactivities;

public interface AugmentedFaceListener {
    void afterFaceDetect(AugmentedFaceNode face);

    void onFaceUpdate(AugmentedFaceNode face);
}
