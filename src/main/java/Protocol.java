import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

abstract class Protocol { //TODO: Terminar definição dos protocolos (cada Algoritmo)
    public abstract PrivateKey generateKeyPair () throws NoSuchAlgorithmException;

    public abstract byte[] encrypt ( byte[] message, PublicKey publicKey,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;


    public abstract byte[] decrypt (byte[] message,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;


    public abstract PublicKey getPublicKey();

    public abstract SecretKey getSecretKey();

    public abstract String getKey();


}
