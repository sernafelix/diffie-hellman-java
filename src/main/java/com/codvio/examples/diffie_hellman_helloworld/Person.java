package main.java.com.codvio.examples.diffie_hellman_helloworld;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Person {
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private PublicKey receivedPublicKey;
	private byte[] secretKey;
	private String secretMessage;
	
	public void encryptAndSendMessage(final String message, final Person person) {
		try {
			final SecretKey keySpec = new SecretKeySpec(secretKey, "DES");
			final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			
			final byte[] encryptedMessage = cipher.doFinal( message.getBytes() );
			
			person.receiveAndDecryptMessage(encryptedMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generateCommonSecretKey() {
		try {
			final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
			keyAgreement.init(privateKey);
			keyAgreement.doPhase(receivedPublicKey, true);
			
			secretKey = shortenSecretKey( keyAgreement.generateSecret() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//System.out.println( "Llave privada en Método GenerateCommonSecretKey: " + secretKey );
	}
	
	public void generateKeys() {
		try {
			final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
			keyPairGenerator.initialize(1024);
			
			final KeyPair keyPair = keyPairGenerator.generateKeyPair();
			
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void receiveAndDecryptMessage(final byte[] message) {
		try {
			
			final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "DES");
			final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			
			secretMessage = new String( cipher.doFinal(message) );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receivePublicKeyFrom(final Person person) {
		receivedPublicKey = person.getPublicKey();
	}
	
	public void whisperTheSecretMessage() {
		System.out.println(secretMessage);
		System.out.println( "Llave pública: " + this.getPublicKey() );
		//System.out.println( "Llave privada en Método Whisper: " + secretKey );
	}
	
	private byte[] shortenSecretKey(final byte[] longKey) {
		try {
			final byte[] shortenedKey = new byte[8];
			System.arraycopy(longKey, 0, shortenedKey, 0, shortenedKey.length);
			
			return shortenedKey;
			
			/*
			
			final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			final DESKeySpec desSpec = new DESKeySpec(longKey);
			return keyFactory.generateSecret(desSpec).getEncoded();
			
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
