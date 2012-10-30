package com.ybi.ragequit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class FeedService {

	private final Context context;
	private PicsDataSource datasource;
	private int nbMessages;

	public FeedService(Context baseContext) {
		context = baseContext;
		checkFeed();
	}

	private void checkFeed() {
		Log.d("RageQuit", "Checking feed ...");
		Thread feedThread = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					datasource = new PicsDataSource(context);
					datasource.open();
					if (datasource.isEmpty()) {
						Log.d("RageQuit", "Checking feed is emtpy processing");
						processFeed();
					}
					datasource.close();
				} catch (SQLException e) {
					Log.d("RageQuit", "error",e  );
				}
				if (datasource != null) {
					datasource.close();
				}
			}
		});
		feedThread.start();
	}

	private void processFeed() throws SQLException {
		String feedUrl = "http://feeds.feedburner.com/ffffound/everyone";
		AndroidSaxFeedParser asx = new AndroidSaxFeedParser(feedUrl);
		List<Message> list = asx.parse();
		for (Message message : list) {
			Log.d("RageQuit", "Image " + message.getMediaContent());
			Log.d("RageQuit", "Checking " + message.getLink().toString());
			// check preexistence
			if (!datasource.isMessage(message.getChecksum())) {
				// if does not exist download pic
				message.setLocation(downloadAndResizeFile(message.getMediaContent()));

				// insert into database all the message
				datasource.createMessage(message);
			}
		}

		nbMessages = datasource.getNbMessages();
	}

	public void getFeed() {
		Thread feedThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


				try {
					datasource = new PicsDataSource(context);
					datasource.open();

					if (mWifi.isConnected()) {
						processFeed();
					}
				} catch (SQLException e) {
					Log.d("RageQuit", "error",e  );
				}
				if (datasource != null) {
					datasource.close();
				}
			}


		});
		feedThread.start();
	}

	private String downloadAndResizeFile(String mediaContent) {

		String result = null;
		try {
			File root = android.os.Environment.getExternalStorageDirectory();

			File dir = new File(root.getAbsolutePath() + "/ragequit");
			if (dir.exists() == false) {
				dir.mkdirs();
			}

			String fileName = mediaContent.substring(mediaContent.lastIndexOf('/') + 1);

			URL url = new URL(mediaContent); //you can write here any link
			File file = new File(dir, fileName);

			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(5000);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();

			result = file.getAbsolutePath();

		} catch (IOException e) {
			Log.d("DownloadManager", "Error: " + e);
		}

		return result;
	}

	public int getNbMessages() {
		return nbMessages;
	}
}
