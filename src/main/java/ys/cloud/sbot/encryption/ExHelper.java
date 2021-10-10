package ys.cloud.sbot.encryption;

import org.jasypt.util.text.BasicTextEncryptor;

public class ExHelper {

	//TODO FIXME change encryption algorithm
//	private static StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//	encryptor.setPassword("some-random-passwprd");
//	encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
	
	private static BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	
	public static String set(String str) {

		return textEncryptor.encrypt(str);
	}

	public static String get(String str) {
		return textEncryptor.decrypt(str);
	}
	
	public static void init(String str) {
		textEncryptor.setPassword(str);
	}
}
