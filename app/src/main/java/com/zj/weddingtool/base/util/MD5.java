package com.zj.weddingtool.base.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5 {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/*
	 * public static void main(String[] args) {
	 * System.out.println(md5sum("/init.rc")); }
	 */

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	static public String md5String(byte [] b) {
		if(b==null)  return null;
		
		MessageDigest md = null;
       
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			return b.toString();
		}

		md.reset();
		md.update(b);
		byte[] encodedPassword = md.digest();
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}

		return buf.toString();
	}
	
	/*
	 * 计算给定字符串的md5
	 */
	static public String md5String(String s) {
		byte[] unencodedPassword = s.getBytes();

		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			return s;
		}
		md.reset();
		md.update(unencodedPassword);
		byte[] encodedPassword = md.digest();
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}

		return buf.toString();
	}

	public static String md5sum(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return toHexString(md5.digest());
		} catch (Exception e) {
			System.out.println("error");
			return null;
		}
	}
}
