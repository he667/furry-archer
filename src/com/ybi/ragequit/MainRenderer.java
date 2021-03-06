package com.ybi.ragequit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

/**
 * 
 *
 */

public class MainRenderer extends RajawaliRenderer {

	private static final String TAG = "RageQuit";
	private DirectionalLight mLight;
	private Plane mSphere;
	private Plane mBackground;
	private BaseObject3D empty;

	// positions
	private float pos;
	private float currentpos;

	// different action states
	private boolean isFlipping;
	private boolean isFlopping;
	private boolean isCentering;

	// a timer
	private long timerValue;

	// a screen manager
	private FlipScreen flipScreen;


	public MainRenderer(Context context) {
		super(context);
		setFrameRate(30);
	}

	@Override
	protected void initScene() {

		empty = new BaseObject3D();
		mSphere = new Plane(3, 3, 12, 12);
		mBackground = new Plane(15f, 11f, 12, 12);

		mLight = new DirectionalLight(0.0f, 0.0f, 0.0f); // set the direction
		mLight.setPower(8f);

		flipScreen = new FlipScreen(getContext());

		//mSphere.setRotZ(90);
		DiffuseMaterial material = new DiffuseMaterial();
		material.setAmbientColor(Color.argb(255, 255, 255, 255));
		material.setUseColor(true);
		//material.setColors(Color.argb(255, 255, 255, 255));

		mSphere.setMaterial(material);
		mSphere.addLight(mLight);
		mSphere.addTexture(mTextureManager.addTexture(flipScreen.getCurrent()));
		mSphere.setRotZ(-90);

		empty.addChild(mSphere);

		DiffuseMaterial material2 = new DiffuseMaterial();
		material2.setAmbientColor(Color.argb(255, 255, 255, 255));
		material2.setAmbientIntensity(5);
		//mBackground.setColor(Color.argb(255, 255, 255, 255));
		mBackground.setZ(10f);
		mBackground.setMaterial(material2);

		addChild(mBackground);
		addChild(empty);
		setBackgroundColor(Color.argb(255, 255, 255, 255));

		mCamera.setZ(-4.2f);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		int rpos;
		//Log.d(TAG, "POSITION = " + currentpos + "-" + Math.round(currentpos));
		if (isFlipping) {
			if (pos > 0) {
				Log.d("RageQuit", "Flipping to " + pos);
				pos = 1;
			} else {
				Log.d("RageQuit", "Flipping to " + pos);
				pos = -1;
			}
		}

		if (isCentering) {
			pos = 0;
		}

		if (isFlopping) {
			currentpos = -90 * pos;
			pos = 0;
			isFlopping = false;
			isCentering = false;
			timerValue = System.currentTimeMillis();
			Log.d("RageQuit", "Flopping to " + pos);
		}

		rpos = (int) (90 * pos);

		if (System.currentTimeMillis() - timerValue > 2000 && Math.round(Math.abs(Math.round(currentpos))) != 0) {
			timerValue = System.currentTimeMillis();
			isCentering = true;
		}

		if (currentpos > rpos) {
			currentpos -= Math.abs((currentpos - rpos) / 4f);
		} else if (currentpos < rpos) {
			currentpos += Math.abs((currentpos - rpos) / 4f);
		}

		if (isFlopping && Math.round(Math.abs(Math.round(currentpos))) == 0) {
			isFlopping = false;
			isCentering = false;
			timerValue = System.currentTimeMillis();
		}
		if (isCentering && Math.round(Math.abs(Math.round(currentpos))) == 0) {
			isCentering = false;
			timerValue = System.currentTimeMillis();
		}

		if (isFlipping && Math.round(currentpos) <= -90) {
			Log.d("RageQuit", "Flipping complete from " + currentpos);

			isFlipping = false;
			isFlopping = true;
			isCentering = false;
			timerValue = System.currentTimeMillis();
			mTextureManager.updateTexture(mTextureManager.getTextureInfoList().get(0), flipScreen.getNext());
			flipScreen.flip(1);
			Log.d(TAG, "isFlipping from neg POSITION = " + currentpos + " SCREEN " + flipScreen.getCurrentScreen());
		}
		if (isFlipping && Math.round(currentpos) >= 90) {
			Log.d("RageQuit", "Flipping complete from " + currentpos);
			isFlipping = false;
			isFlopping = true;
			isCentering = false;
			timerValue = System.currentTimeMillis();
			mTextureManager.updateTexture(mTextureManager.getTextureInfoList().get(0), flipScreen.getPrevious());
			flipScreen.flip(-1);
			Log.d(TAG, "isFlipping from pos POSITION = " + currentpos + " SCREEN " + flipScreen.getCurrentScreen());
		}
		empty.setRotY(currentpos);
	}


	public void rotate(float tpos) {
		timerValue = System.currentTimeMillis();
		if (!isFlipping && !isFlopping) {
			pos = -tpos;
		}
		//Log.d(TAG, "POSITION = " + currentpos);

	}

	public void flip(int i) {
		timerValue = System.currentTimeMillis();
		if (i == 1 && !flipScreen.isMaxScreen()) {
			Log.d("RageQuit", "Starting flipping " + i);
			isFlipping = true;
			isCentering = false;
		} else if (i == -1 && flipScreen.getCurrentScreen() > 0) {
			Log.d("RageQuit", "Starting flipping " + i);
			isFlipping = true;
			isCentering = false;
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//		if (getSceneInitialized()) {
		//			Log.d(TAG, "UPDATING TEXTURE!!!!!!");
		//			mSphere.addTexture(mTextureManager.addTexture(flipScreen.getCurrent()));
		//			//mTextureManager.updateTexture(mTextureManager.getTextureInfoList().get(0), flipScreen.getCurrent());
		//		}
		super.onSurfaceCreated(gl, config);
	}

	@Override
	public void onSurfaceDestroyed() {
		super.onSurfaceDestroyed();
		//mTextureManager.removeTexture(mTextureManager.getTextureInfoList().get(0));
		flipScreen.recycle();
	}
}
