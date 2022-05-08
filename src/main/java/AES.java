import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

    private static final int ENCRYPT_MODE = 1;
    private static final int DECRYPT_MODE = 2;
    private String protocolo="";// como aceder a tamanho da chave

    @Override
    public PrivateKey generateKeyPair() {
        PrivateKey privateKey=null;
        return privateKey;
    }




    @Override
    public byte[] encrypt(byte[] message ,PrivateKey privateKey, PublicKey publicKey , String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance( "AES" );
        SecretKeySpec secretKeySpec = new SecretKeySpec( key.getBytes( StandardCharsets.UTF_8 ) , "AES" );

        cipher.init( Cipher.ENCRYPT_MODE , secretKeySpec );
        ArrayList<byte[]> textSplits = splitText( message , 15 , AES.ENCRYPT_MODE );
        ByteArrayOutputStream output = new ByteArrayOutputStream( );
        for ( byte[] textSplit : textSplits ) {
            byte[] textEncrypted = cipher.doFinal( textSplit );
            output.write( textEncrypted );
        }
        return output.toByteArray( );
    }


    @Override
    public byte[] decrypt(byte[] message, String key,PrivateKey privateKey, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance( "AES" );
        SecretKeySpec secretKeySpec = new SecretKeySpec( key.getBytes( StandardCharsets.UTF_8 ) , "AES" );
        cipher.init( Cipher.DECRYPT_MODE , secretKeySpec );
        ArrayList<byte[]> textSplits = splitText( message , 16 , DECRYPT_MODE );
        ByteArrayOutputStream output = new ByteArrayOutputStream( );
        for ( byte[] textSplit : textSplits ) {
            output.write( cipher.doFinal( textSplit ) );
        }
        byte[] outputByte = output.toByteArray( );
        int padding = outputByte[ outputByte.length - 1 ];
        return Arrays.copyOfRange( outputByte , 0 , outputByte.length - padding );
    }


    private static ArrayList<byte[]> splitText ( byte[] text , int blockSize , int mode ) throws IOException {
        ArrayList<byte[]> textSplits = new ArrayList<>( );
        for ( int startPos = 0; startPos < text.length; startPos += blockSize ) {
            int endPos = startPos + blockSize;
            if ( endPos > text.length ) {
                endPos = text.length;
            }
            textSplits.add( Arrays.copyOfRange( text , startPos , endPos ) );
        }
        if ( mode == ENCRYPT_MODE ) {
            byte[] lastBlock = textSplits.get( textSplits.size( ) - 1 );
            int padding = blockSize - lastBlock.length;
            ByteArrayOutputStream output = new ByteArrayOutputStream( );
            if ( padding == 0 ) {
                for ( int i = 0; i < blockSize; i++ ) {
                    output.write( (byte) blockSize );
                }
                textSplits.add( output.toByteArray( ) );
            } else {
                output.write( lastBlock );
                for ( int i = 0; i < padding; i++ ) {
                    output.write( (byte) padding );
                }
                textSplits.set( textSplits.size( ) - 1 , output.toByteArray( ) );
            }
        }
        return textSplits;
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
