import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

abstract class Protocol { //TODO: Terminar definição dos protocolos (cada Algoritmo)
    public abstract PrivateKey generateKeyPair () throws NoSuchAlgorithmException;

    public abstract byte[] encrypt(byte[] message, PrivateKey privateKey, PublicKey publicKey, String key, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException, InvalidAlgorithmParameterException;

    public abstract byte[] decrypt(byte[] message, String key,PrivateKey privateKey,PublicKey publicKey,SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,IOException;

    public abstract PublicKey getPublicKey();

    public SecretKey generateDESkey() {
        return null;
    }
    public SecretKey getSecretKey(){
        return null;
    }

}
