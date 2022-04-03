import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class RSA {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSA () throws NoSuchAlgorithmException {
        generateKeyPair( );
    }

    private void generateKeyPair () throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance( "RSA" );
        keyPairGenerator.initialize( 2048 );
        KeyPair keyPair = keyPairGenerator.generateKeyPair( );
        this.privateKey = keyPair.getPrivate( );
        this.publicKey = keyPair.getPublic( );
    }

    public byte[] encrypt ( byte[] message , PublicKey publicKey ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance( "RSA" );
        cipher.init( Cipher.ENCRYPT_MODE , publicKey );
        return cipher.doFinal( message );
    }

    public byte[] decrypt ( byte[] message ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance( "RSA" );
        cipher.init( Cipher.DECRYPT_MODE , this.privateKey );
        return cipher.doFinal( message );
    }

    public PublicKey getPublicKey () {
        return publicKey;
    }
}