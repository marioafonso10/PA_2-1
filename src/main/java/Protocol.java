import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

abstract class Protocol { //TODO: Terminar definição dos protocolos (cada Algoritmo)
    public abstract PrivateKey generateKeyPair () throws NoSuchAlgorithmException;

    public abstract byte[] encrypt(byte[] message, PrivateKey privateKey,PublicKey publicKey, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException;

    public abstract byte[] decrypt(byte[] message, String key,PrivateKey privateKey,PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,IOException;

    public abstract PublicKey getPublicKey();

}
