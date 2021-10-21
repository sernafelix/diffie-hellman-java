package main.java.com.codvio.examples.diffie_hellman_helloworld;

import java.io.FileWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Person {
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private PublicKey receivedPublicKey;
	private byte[] secretKey;
	private String secretMessage;
	
	private static final Logger LOG = Logger.getLogger(Person.class.getName());
	
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
		
		saveKey(publicKey, "publicKey_dh.key", "public key dh");
		LOG.info( String.format("Public Key Format: %s", publicKey.getFormat() ));
		//LOG.info("Llave pública: " + publicKey.getEncoded() );
		
		saveKey(privateKey, "privateKey_dh.key", "private key dh");
		LOG.info( String.format("Private Key Format: %s", privateKey.getFormat() ));
		//LOG.info("Llave privada: " + privateKey.getEncoded() );
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
	
	private static void saveKey(Key key, String fileName, String header) {
		Base64.Encoder encoder = Base64.getEncoder();
		final String FORMAT = "----%s %s----";
		
		try (FileWriter out = new FileWriter(fileName)) {
			out.write( String.format(FORMAT, "BEGIN", header.toUpperCase() ));
			out.write("\n");
			
			out.write( encoder.encodeToString(key.getEncoded()) );
			out.write("\n");
			
			out.write( String.format(FORMAT, "END", header.toUpperCase()) );
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		
		//System.out.println("Llave generada: " + encoder.encodeToString(key.getEncoded()) + "\n-------");
	}

}
