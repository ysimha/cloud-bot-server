package ys.cloud.sbot.exchange;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExHelper {

	//TODO FIXME change encryption algorithm
//	private static StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//	encryptor.setPassword("some-random-password");
//	encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
	
	final private static BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	final private static AtomicBoolean initialize = new AtomicBoolean(false);

	public static String set(String str) {
		return textEncryptor.encrypt(str);
	}

	public static String get(String str) {
		return textEncryptor.decrypt(str);
	}
	
	public static void init(String str) {
		synchronized (ExHelper.class){
			if (initialize.get()) return;
			textEncryptor.setPassword(str);
			initialize.set(true);
		}
	}
}
