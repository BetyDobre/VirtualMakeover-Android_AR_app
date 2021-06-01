package com.shop.ARactivities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.shop.ARactivities.helpers.DisplayRotationHelper;
import com.shop.ARactivities.helpers.SnackbarHelper;
import com.shop.ARactivities.helpers.TrackingStateHelper;
import com.shop.ARactivities.rendering.BackgroundRenderer;
import com.shop.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link //AugmentedFaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AugmentedFaceFragment extends Fragment implements GLSurfaceView.Renderer {

    private com.google.ar.core.Session session = null;
    private FrameLayout frameLayout = null;
    private GLSurfaceView surfaceView = null;
    private DisplayRotationHelper displayRotationHelper;
    private TrackingStateHelper trackingStateHelper;

    public HashMap<AugmentedFace, AugmentedFaceNode> faceNodeMap = new HashMap<>();

    private BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private Boolean installRequested = false;
    private Boolean canRequestDangerousPermissions = true;
    private SnackbarHelper messageSnackbarHelper= new SnackbarHelper();
    private int RC_PERMISSIONS = 1010;

    private AugmentedFaceListener augmentedFaceListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayRotationHelper = new DisplayRotationHelper(getContext());
        trackingStateHelper = new TrackingStateHelper(requireActivity());
        installRequested = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_augmented_face, container, false);
        surfaceView = (GLSurfaceView) frameLayout.findViewById(R.id.surface_view);
        if (surfaceView != null){
            surfaceView.setPreserveEGLContextOnPause(true);
            surfaceView.setEGLContextClientVersion(2);
            surfaceView.setEGLConfigChooser(8,8,8,8,16,0);

            surfaceView.setRenderer(this);
            surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            surfaceView.setWillNotDraw(false);
        }

        return frameLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(session == null){
            try {
                ArCoreApk.InstallStatus installStatus = ArCoreApk.getInstance().requestInstall(requireActivity(), !installRequested);
                switch (installStatus){
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                    default:
                        System.out.println("Undefined installed status");
                }
                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (checkSelfPermission(requireActivity(), "android.permission.CAMERA")
                        == PackageManager.PERMISSION_GRANTED) {
                    // Configure session to use front facing camera.
                    EnumSet<com.google.ar.core.Session.Feature> featureSet = EnumSet.of(com.google.ar.core.Session.Feature.FRONT_CAMERA);
                    // Create the session.
                    session = new Session(/*context=*/getContext(), featureSet);
                    configureSession();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, RC_PERMISSIONS);
                    //requestDangerousPermissions();
                }
            } catch (UnavailableArcoreNotInstalledException e) {
                String message = "Please install ARCore";
                messageSnackbarHelper.showError(requireActivity(), message);
                System.out.println("Exception creating session:$e");
                return;
            } catch (UnavailableUserDeclinedInstallationException e) {
                String message = "Please install ARCore";
                messageSnackbarHelper.showError(requireActivity(), message);
                System.out.println("Exception creating session:$e");
            } catch (UnavailableApkTooOldException e) {
                String message = "Please update ARCore";
                messageSnackbarHelper.showError(requireActivity(), message);
                System.out.println("Exception creating session:$e");
            } catch (UnavailableSdkTooOldException e) {
                String message = "Please update this app";
                messageSnackbarHelper.showError(requireActivity(), message);
                System.out.println("Exception creating session:$e");
            } catch (UnavailableDeviceNotCompatibleException e) {
                String message = "This device does not support AR";
                messageSnackbarHelper.showError(requireActivity(), message);
                System.out.println("Exception creating session:$e");
            } catch (Exception e) {
                String message = "Failed to create AR session";
                messageSnackbarHelper.showError(requireActivity(), message);
                System.out.println("Exception creating session:$e");
            }
        }

        try {
            if (session != null) {
                session.resume();
            }
        } catch (CameraNotAvailableException e) {
            messageSnackbarHelper.showError(
                    requireActivity(),
                    "Camera not available. Try restarting the app."
            );
            session = null;
            return;
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();
    }

    public void setAugmentedFaceListener(AugmentedFaceListener listener) {
        augmentedFaceListener = listener;
    }

    private Boolean getCanRequestDangerousPermissions() {
        return canRequestDangerousPermissions;
    }

    /**
     * If true, [.requestDangerousPermissions] returns without doing anything, if false
     * permissions will be requested
     */
    private void setCanRequestDangerousPermissions(Boolean canRequestDangerousPermissions) {
        this.canRequestDangerousPermissions = canRequestDangerousPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(
                requireActivity(),
                android.R.style.Theme_Material_Dialog_Alert
        );
        builder
                .setTitle("Camera permission required")
                .setMessage("Add camera permission via Settings?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts(
                                "package",
                                requireActivity().getPackageName(),
                                null)
                        );
                        requireActivity().startActivity(intent);
                        // When the user closes the Settings app, allow the app to resume.
                        // Allow the app to ask for permissions again now.
                        setCanRequestDangerousPermissions(true);
                    }
                })
            .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!getCanRequestDangerousPermissions()) {
                            requireActivity().finish();
                        }
                    }
                })
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (session != null) {
            // Explicitly close ARCore Session to release native resources.
            // Review the API reference for important considerations before calling close() in apps with
            // more complicated lifecycle requirements:
            // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
            session.close();
            session = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(getContext());
        } catch (IOException e) {
            System.out.println("Failed to read an asset file $e");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (session != null){
            displayRotationHelper.updateSessionIfNeeded(session);
            try {
                session.setCameraTextureName(backgroundRenderer.getTextureId());

                // Obtain the current frame from ARSession. When the configuration is set to
                // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the camera framerate.
                Frame frame = session.update();
                Camera camera= frame.getCamera();

                // Get projection matrix.
                float[] projectionMatrix = new float[16];
                camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100.0f);

                // Get camera matrix and draw.
                float[] viewMatrix = new float[16];
                camera.getViewMatrix(viewMatrix, 0);

                // Compute lighting from average intensity of the image.
                // The first three components are color scaling factors.
                // The last one is the average pixel intensity in gamma space.
                float[] colorCorrectionRgba = new float[4];
                frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

                // If frame is ready, render camera preview image to the GL surface.
                backgroundRenderer.draw(frame);

                // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
                trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());
                Collection<AugmentedFace> faces = session.getAllTrackables(AugmentedFace.class);
                for (AugmentedFace face : faces) {
                    if (!faceNodeMap.containsKey(face)) {
                        AugmentedFaceNode faceNode = new AugmentedFaceNode(face, requireContext());
                        augmentedFaceListener.onFaceAdded(faceNode);
                        faceNodeMap.put(face, faceNode);
                    } else {
                        if(faceNodeMap.get(face) != null) {
                            augmentedFaceListener.onFaceUpdate(faceNodeMap.get(face));
                        }
                    }

                    Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iter = faceNodeMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iter.next();
                        AugmentedFace faceNode = entry.getKey();
                        if (faceNode.getTrackingState() == TrackingState.STOPPED) {
                            iter.remove();
                        }
                    }
                    if (face.getTrackingState() != TrackingState.TRACKING) {
                        break;
                    }
                    // Face objects use transparency so they must be rendered back to front without depth write.
                    GLES20.glDepthMask(false);

                    // Each face's region poses, mesh vertices, and mesh normals are updated every frame.

                    // 1. Render the face mesh first, behind any 3D objects attached to the face regions.
                    faceNodeMap.get(face).onDraw(projectionMatrix, viewMatrix, colorCorrectionRgba);
                }
            } catch (Throwable t) {
                // Avoid crashing the application due to unhandled exceptions.
                System.out.println("Exception on the OpenGL thread " + t);
            } finally {
                GLES20.glDepthMask(true);
            }
        }
    }

    public void requestDangerousPermissions() {
        if(!canRequestDangerousPermissions){
            return;
        }
        canRequestDangerousPermissions = false;

        ArrayList<String> permissions = new ArrayList();
        ArrayList<String> additionalPermissions = new ArrayList();
        int permissionLength = additionalPermissions.size();
        for (int i = 0; i < permissionLength; i++) {
            if (checkSelfPermission(requireActivity(), additionalPermissions.get(i)) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(additionalPermissions.get(i));
            }
        }

        // Always check for camera permission
        if (checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (!permissions.isEmpty()) {
            // Request the permissions
            requestPermissions(
                    new String[]{""},
                    RC_PERMISSIONS
            );
        }
    }

    private void configureSession() {
        Config config = new Config(session);
        config.setAugmentedFaceMode(Config.AugmentedFaceMode.MESH3D);
        session.configure(config);
    }
}