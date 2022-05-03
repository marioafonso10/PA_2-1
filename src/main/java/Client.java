import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static final ArrayList<PublicKey> publicKeys = new ArrayList<>( );
    private static final ArrayList<String> userNames = new ArrayList<>( );
    private final Socket client;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final String userName;
    private final Protocol protocol;

    public Client ( String host , int port , String userName, Protocol protocol) throws IOException, NoSuchAlgorithmException {
        client = new Socket( host , port );
        this.userName = userName;
        this.protocol=protocol;
        out = new ObjectOutputStream( client.getOutputStream( ) );
        in = new ObjectInputStream( client.getInputStream( ) );
        out.writeObject( userName );
        out.writeObject( protocol.getPublicKey( ) );
    }

    public void sendMessages (Protocol protocol) throws IOException {
        while ( client.isConnected( ) ) {
            Scanner usrInput = new Scanner( System.in );
            String message = usrInput.nextLine( );
            try {
                for ( int i = 0; i < publicKeys.size( ); i++ ) {
                    if ( ! userName.equals( userNames.get( i ) ) ) {
                        String key = null;
                        byte[] messageEncrypted = protocol.encrypt( message.getBytes( StandardCharsets.UTF_8 ) , publicKeys.get( i ),key   );
                        ArrayList<Object> messageWithReceiver = new ArrayList<>( 2 );
                        messageWithReceiver.add( userNames.get( i ) );
                        messageWithReceiver.add( messageEncrypted );
                        out.writeObject( messageWithReceiver );
                        out.writeObject(protocol);
                    }
                }
            } catch ( IOException e ) {
                closeConnection( );
                break;
            } catch ( NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e ) {
                e.printStackTrace( );
            }
        }
    }

    public void readMessages (Protocol protocol) {
        new Thread( () -> {
            while ( client.isConnected( ) ) {
                try {
                    Object message = in.readObject( );
                    if ( message instanceof String ) {
                        if ( message.equals( "UPDATE_PUBLIC_KEYS" ) ) {
                            int numberOfClients = (int) in.readObject( );
                            publicKeys.clear( );
                            userNames.clear( );
                            for ( int i = 0; i < numberOfClients; i++ ) {
                                PublicKey publicKey = (PublicKey) in.readObject( );
                                publicKeys.add( publicKey );
                                String userName = (String) in.readObject( );
                                userNames.add( userName );
                            }
                        }
                    } else {
                        ArrayList<Object> messageWithUserName = (ArrayList<Object>) message;
                        String key = null ;
                        String userName = (String) messageWithUserName.get( 0 );
                        String messageDecrypted = new String( protocol.decrypt( (byte[]) messageWithUserName.get( 1 ), key )   );
                        System.out.println( userName + ": " + messageDecrypted );
                    }
                } catch ( IOException | ClassNotFoundException e ) {
                    try {
                        closeConnection( );
                    } catch ( IOException ex ) {
                        ex.printStackTrace( );
                    }
                    break;
                } catch ( NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e ) {
                    e.printStackTrace( );
                }
            }
        } ).start( );
    }

    private void closeConnection () throws IOException {
        client.close( );
        out.close( );
        in.close( );
    }



}
