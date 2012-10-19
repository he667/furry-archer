package com.ybi.ragequit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 *
 */

public class Utils {

	protected Utils() {
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (byte element : messageDigest) {
				hexString.append(Integer.toHexString(0xFF & element));
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}
