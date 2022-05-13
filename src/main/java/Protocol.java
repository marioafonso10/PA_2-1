import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

abstract class Protocol { //TODO: Terminar definição dos protocolos (cada Algoritmo)
    public abstract PrivateKey generateKeyPair () throws NoSuchAlgorithmException;

    public byte[] encrypt ( byte[] message, PublicKey publicKey,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {

    public byte[] decrypt (byte[] message,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,IOException {

    public abstract PublicKey getPublicKey();

    public SecretKey generateDESkey() {
        return null;
    }
    public SecretKey getSecretKey(){
        return null;
    }

}
