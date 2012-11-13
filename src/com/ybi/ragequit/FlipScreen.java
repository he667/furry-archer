package com.ybi.ragequit;

import java.sql.SQLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 
 *
 */

public class FlipScreen {

	private final Context ctx;

	private static final int MAX_WIDTH = 480;
	private static final int MAX_HEIGHT = 480;
	private static final int LINE_HEIGHT = 20;

	private int currentScreen;
	private int maxScreen;
	// some bitmaps
	private Bitmap current;
	private Bitmap previous;
	private Bitmap next;
	private final PicsDataSource datasource;

	public FlipScreen(Context context) {
		ctx = context;
		datasource = new PicsDataSource(ctx);

		// prepare the current and the next
		current = drawWidget(0);
		next = drawWidget(1);
	}

	public Bitmap getCurrent() {
		if (!current.isRecycled()) {
			return current;
		} else {
			Log.d("RageQuit", "current regen ");
			current = drawWidget(0);
			return current;
		}

	}

	public int getCurrentScreen() {
		return currentScreen;
	}

	private Bitmap drawWidget(int tScreen) {

		Paint p = new Paint();
		p.setDither(false);
		p.setFilterBitmap(false);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(8);
		p.setColor(0xFFFF0000);

		Bitmap bitmap = Bitmap.createBitmap(MAX_WIDTH, MAX_HEIGHT, Config.ARGB_8888);
		bitmap.setHasAlpha(true);
		bitmap.setDensity(DisplayMetrics.DENSITY_HIGH);
		bitmap.eraseColor(Color.argb(255, 255, 255, 255));
		Canvas canvas = new Canvas(bitmap);

		Typeface bold = Typeface.createFromAsset(ctx.getAssets(), "ubuntub.ttf");
		Typeface light = Typeface.createFromAsset(ctx.getAssets(), "ubuntul.ttf");
		p.setAntiAlias(true);
		p.setSubpixelText(true);
		p.setStyle(Style.FILL);
		p.setColor(Color.BLACK);
		p.setFilterBitmap(true);
		Bitmap bg = null;

		// get the current screen message
		Message message = getMessage(tScreen);

		if (message != null && message.getLocation() != null) {
			bg = BitmapFactory.decodeFile(message.getLocation());

			//		switch (tScreen) {
			//		case 0:
			//			bg = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.accm);
			//			break;
			//		case 1:
			//			bg = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.acca);
			//			break;
			//		case 2:
			//			bg = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.accf);
			//			break;
			//		case 3:
			//			bg = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.accd);
			//			break;
			//		case 4:
			//			bg = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.acco);
			//			break;
			//		default:
			//			bg = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.aic_launcher);
			//			break;
			//		}

			//MAX_WIDTH / bg.getWidth() * bg.getHeight()
			//Rect sourceRect = new Rect();

			canvas.drawBitmap(bg, null, new Rect(50, 0, MAX_WIDTH - 50, 350), p);
			int ih = 370;

			//canvas.drawARGB(0, 0, 0, 0);
			p.setTypeface(bold);
			p.setTextSize(20);
			canvas.drawText(message.getTitle(), 60, ih, p);
			ih += LINE_HEIGHT;
			p.setTypeface(light);
			p.setTextSize(12);
			canvas.drawText(message.getDateForDisplay(), 60, ih, p);
			ih += LINE_HEIGHT;
			p.setTypeface(light);
			p.setTextSize(15);
			canvas.drawText(message.getDescription(), 60, ih, p);
			ih += LINE_HEIGHT;
			canvas.drawText(message.getLink().toString(), 60, ih, p);
			ih += LINE_HEIGHT;
			p.setTextSize(12);
			canvas.drawText("[" + (tScreen + 1) + " / " + maxScreen + "]", 60, ih, p);
			ih += LINE_HEIGHT;
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(1);
			canvas.drawRect(new Rect(50, 0, MAX_WIDTH - 50, MAX_HEIGHT - 10), p);

			return bitmap;
		} else {
			return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.aic_launcher);
		}
	}

	private Message getMessage(int tScreen) {
		Message message = null;
		try {
			datasource.open();
			maxScreen = datasource.getNbMessages();
			message = datasource.getNthMessage(tScreen);
			Log.d("RageQuit", "getting " + currentScreen + " message + " + message.getId() + " --- " + message.getLocation());
		} catch (SQLException e) {
			Log.d("RageQuit", "error", e);
		}
		if (datasource != null) {
			datasource.close();
		}
		return message;
	}

	public void flip(final int i) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (i < 0) {
					currentScreen--;
					// going back
					next = current;
					current = previous;
					if (currentScreen > 0) {
						previous = drawWidget(currentScreen - 1);
					}
				} else {
					currentScreen++;
					previous = current;
					current = next;
					next = drawWidget(currentScreen + 1);
				}
			}
		});
		thread.start();
	}

	public Bitmap getNext() {
		if (!next.isRecycled()) {
			return next;
		} else {
			Log.d("RageQuit", "next regen ");
			next = drawWidget(currentScreen + 1);
			return next;
		}
	}

	public Bitmap getPrevious() {
		if (!previous.isRecycled()) {
			return previous;
		} else {
			Log.d("RageQuit", "previous regen ");
			previous = drawWidget(currentScreen - 1);
			return previous;
		}
	}

	public boolean isMaxScreen() {
		if (currentScreen < maxScreen) {
			return false;
		}
		return true;
	}

	public void recycle() {
		if (previous != null && !previous.isRecycled()) {
			Log.d("RageQuit", "previous recycled");
			previous.recycle();
			previous = null;
		}
		if (current != null && !current.isRecycled()) {
			Log.d("RageQuit", "current recycled");
			current.recycle();
			current = null;
		}
		if (next != null && !next.isRecycled()) {
			Log.d("RageQuit", "current recycled");
			next.recycle();
			next = null;
		}
	}

	//	private void flipBitmap(final int i) {
	//		Thread thread = new Thread(new Runnable() {
	//			@Override
	//			public void run() {
	//				if (i < 0) {
	//					second = first;
	//					third = second;
	//					if (currentScreen > 0) {
	//						first = drawWidget(currentScreen - 1, getContext());
	//					}
	//				} else {
	//					first = second;
	//					second = third;
	//					third = drawWidget(currentScreen + 1, getContext());
	//				}
	//			}
	//		});
	//		thread.start();
	//
	//	}
}
