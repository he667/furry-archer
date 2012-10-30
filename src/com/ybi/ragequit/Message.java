package com.ybi.ragequit;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * 
 *
 */

public class Message implements Comparable<Message> {
	final static SimpleDateFormat IN_FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	final static SimpleDateFormat OUT_FORMATTER = new SimpleDateFormat("yyMMddHHmmssZ", Locale.US);

	private long id;
	private String title;
	private URL link;
	private String description;
	private Date date;
	private String mediaThumbnail;
	private String mediaContent;
	private String checksum;
	private String location;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	// getters and setters omitted for brevity
	public void setLink(String link) {
		try {
			this.link = new URL(link);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDateForDatabase() {
		return OUT_FORMATTER.format(date);
	}

	public String getDateForDisplay() {
		return IN_FORMATTER.format(date);
	}

	public void setDate(String date) {
		// pad the date if necessary
		while (!date.endsWith("00")) {
			date += "0";
		}
		try {
			this.date = IN_FORMATTER.parse(date.trim());
		} catch (java.text.ParseException e) {
			Log.e("RageQuit", "Parse Excpetion", e);
		}
	}

	public void setMediaContent(String mediaContent) {
		this.mediaContent = mediaContent;
	}

	public void setMediaThumbnail(String mediaThumbnail) {
		this.mediaThumbnail = mediaThumbnail;
	}

	public String getMediaContent() {
		return mediaContent;
	}

	public String getMediaThumbnail() {
		return mediaThumbnail;
	}

	public String getChecksum() {
		return Utils.md5(link.toString());
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return title + " / " + description + " / " + link.toString();
	}

	// sort by date
	@Override
	public int compareTo(Message another) {
		if (another == null) {
			return 1;
		}
		// sort descending, most recent first
		return another.date.compareTo(date);
	}

	public Message copy() {
		Message message = new Message();
		message.setDate(date);
		message.setDescription(description);
		message.setLink(link);
		message.setTitle(title);
		message.setMediaThumbnail(mediaThumbnail);
		message.setMediaContent(mediaContent);
		message.setLocation(location);
		return message;
	}
}