package com.ybi.ragequit;

import rajawali.RajawaliFragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends RajawaliFragmentActivity implements OnTouchListener {

	private MainRenderer mRenderer;
	private FeedService mFeed;
	//private int currentscreen;
	private float startY;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mRenderer = new MainRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		super.setRenderer(mRenderer);
		mSurfaceView.setOnTouchListener(this);
		mFeed = new FeedService(getBaseContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		float pos = (event.getRawX() - width / 2.0f) / width;
		//final int X = (int) event.getRawX();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			startY = (event.getRawY() - height / 2.0f) / width;
			break;
		case MotionEvent.ACTION_UP:
			float currentY = (event.getRawY() - height / 2.0f) / width;
			if (startY - currentY > 0.4) {
				if (mFeed != null) {
					mFeed.getFeed();
				}
				//mRenderer.refresh(mFeed.getNbMessages());
			} else if (currentY - startY > 0.4) {
				Intent intent = new Intent(this, MainPreferencesActivity.class);
				startActivity(intent);
			} else {

				if (pos > 0.2) {
					Log.d("RageQuit", "Flipping to right Position = " + pos);
					mRenderer.flip(+1);
					//currentscreen += 1;
					return true;
				} else if (pos < -0.2) {
					Log.d("RageQuit", "Flipping to left Position = " + pos);
					mRenderer.flip(-1);
					//currentscreen -= 1;
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mRenderer.rotate(pos);
			break;
		}

		return true;
	}

}
