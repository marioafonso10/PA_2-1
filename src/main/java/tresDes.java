import jdk.incubator.vector.VectorOperators;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class tresDes extends Protocol {

    public SecretKey getSecretKey() {
        return k1;
    }
    SecretKey k1 = generateDESkey();
    @Override
    public PrivateKey generateKeyPair() throws NoSuchAlgorithmException {
        PrivateKey privateKey=null;
        return privateKey;
    }

    public SecretKey generateDESkey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DESede");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(VectorOperators.Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        keyGen.init(168); // key length 112 for two keys, 168 for three keys
        SecretKey secretKey = keyGen.generateKey();
        return secretKey;
    }


    @Override
    public byte[] encrypt(byte[] message, PrivateKey privateKey, PublicKey publicKey, String key, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException, InvalidAlgorithmParameterException throws NoSuchPaddingException, NoSuchAlgorithmException throws IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance( "DESede/ECB/PKCS5Padding" );
        SecretKey k1 = generateDESkey();
        cipher.init( Cipher.ENCRYPT_MODE , k1 );
        return cipher.doFinal(message);
    }




    @Override
    public byte[] decrypt(byte[] message, String key,PrivateKey privateKey,PublicKey publicKey,SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance( "DESede/ECB/PKCS5Padding" );
        SecretKey k1 = generateDESkey();
        cipher.init( Cipher.DECRYPT_MODE , k1 );
        return cipher.doFinal(message);
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }

}
