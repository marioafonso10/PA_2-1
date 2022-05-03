import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class doisDes extends Protocol {


    @Override
    public byte[] encrypt(byte[] message, PublicKey publicKey, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] message, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        return new byte[0];
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }
}
