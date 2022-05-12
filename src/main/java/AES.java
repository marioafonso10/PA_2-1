import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.PublicKey;



public class AES extends Protocol{

    private String protocolo="";// como aceder a tamanho da chave
    public String getKey(){
        return´´
    }
    @Override
    public PrivateKey generateKeyPair() {
        PrivateKey privateKey=null;
        return privateKey;
    }

    @Override
    public byte[] encrypt(byte[] message, PrivateKey privateKey, PublicKey publicKey, String key, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance( "AES" );
        SecretKeySpec secretKeySpec = new SecretKeySpec( key.getBytes( StandardCharsets.UTF_8 ) , "AES" );
        cipher.init( Cipher.ENCRYPT_MODE , secretKeySpec );

        return cipher.doFinal(message);
    }


    @Override
    public byte[] decrypt(byte[] message, String key,PrivateKey privateKey,PublicKey publicKey,SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance( "AES" );
        SecretKeySpec secretKeySpec = new SecretKeySpec( key.getBytes( StandardCharsets.UTF_8 ) , "AES" );
        cipher.init( Cipher.DECRYPT_MODE , secretKeySpec );
        return cipher.doFinal( message ) ;
    }




    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    @Override
    public String toString() {
        return protocolo;
    }
}
