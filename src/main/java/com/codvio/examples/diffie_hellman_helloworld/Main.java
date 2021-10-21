package main.java.com.codvio.examples.diffie_hellman_helloworld;

public class Main {

	public static void main(final String[] args) throws Exception {
		new Main().init();
	}
	
	private void init() {
		// 1. This is Alice and Bob
		final Person alice = new Person();
		final Person bob = new Person();
		
		// 2. Alice and Bob generate public and private keys.
		alice.generateKeys();
		bob.generateKeys();
		
		System.out.println( "Llave pública obtenida para Alice: ---> " + alice.getPublicKey() + "\n-------------" );
		System.out.println( "Llave pública obtenida para Bob: ---> " + bob.getPublicKey() + "\n-------------" );
		
		// 3. Alice and Bob exchange public keys with each other.
		alice.receivePublicKeyFrom(bob);
		bob.receivePublicKeyFrom(alice);
		
		// 4.
		// Alice generates common secret key via using her private key and Bob's public key.
		// Bob generates common secret key via using his private key and Alice's public key.
		// Both secret keys are equal without TRANSFERRING. This is the magic of Diffie-Helman algorithm.
		alice.generateCommonSecretKey();
		bob.generateCommonSecretKey();
		
		// 5. Alice encrypt message using the secret key and sends to Bob.
		alice.encryptAndSendMessage("Bob! Guess Who I am", bob);
		
		// 6. Bob receives the important message and decrypts with secret key.
		bob.whisperTheSecretMessage();
		
	}

}
