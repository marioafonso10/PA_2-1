import jdk.incubator.vector.VectorOperators;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Des extends Protocol {
    private SecretKey secretKey;



public Des(){
    generateDESkey();
}

    public SecretKey getSecretKey() {

        return this.secretKey;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public PrivateKey generateKeyPair() throws NoSuchAlgorithmException {
        PrivateKey privateKey=null;
        return privateKey;
    }


    public SecretKey  generateDESkey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DESede");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(VectorOperators.Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        keyGen.init(56); // key length 112 for two keys, 168 for three keys
        this.secretKey = keyGen.generateKey();
        return secretKey;
    }


    @Override
    public byte[] encrypt(byte[] message, PublicKey publicKey, String key)throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance( "DESede/ECB/PKCS5Padding" );
        SecretKey k1 = generateDESkey();
        cipher.init( Cipher.ENCRYPT_MODE , this.secretKey );
        return cipher.doFinal(message);
    }




    @Override
    public byte[] decrypt(byte[] message, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance( "DESede/ECB/PKCS5Padding" );
        SecretKey k1 = generateDESkey();
        cipher.init( Cipher.DECRYPT_MODE , secretKey);
        return cipher.doFinal(message);
    }


    @Override
    public PublicKey getPublicKey() {
        return null;
    }
}
