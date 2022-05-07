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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.getInteger;

public class Client {

    private static final ArrayList<PublicKey> publicKeys = new ArrayList<>( );
    private static final ArrayList<String> userNames = new ArrayList<>( );
    private final Socket client;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final String userName;

    public Protocol getProtocol() {
        return protocol;
    }

    private final Protocol protocol;

    public String getUserName() {
        return userName;
    }

    public Client (String host , int port , String userName, Protocol protocol) throws IOException, NoSuchAlgorithmException {
        client = new Socket( host , port );
        this.privateKey = protocol.generateKeyPair();
        this.publicKey= protocol.getPublicKey();
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
                    if (  userName.equals( userNames.get( i ) ) ) {
                        String key = null;
                        byte[] messageEncrypted = protocol.encrypt( message.getBytes( StandardCharsets.UTF_8 ) ,privateKey, publicKey ,key );
                        ArrayList<Object> messageWithReceiver = new ArrayList<>( 3 );
                        messageWithReceiver.add(0,"message");
                        messageWithReceiver.add( 1,userNames.get( i ) );
                        messageWithReceiver.add(2, messageEncrypted );
                        out.writeObject( messageWithReceiver );
                        //out.writeObject(protocol);

                }
            }
            }catch ( IOException e ) {
                closeConnection( );
                break;
            } catch ( NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e ) {
                e.printStackTrace( );
            }
        }
    }

    public void sendOneMessage(Protocol protocol, String data, Client user) throws IOException {
        if ( client.isConnected( ) ) {
            try {
                for ( int i = 0; i < publicKeys.size( ); i++ ) {
                    if ( ! user.getUserName().equals( userNames.get( i ) ) ) {
                        String key = null;
                        byte[] messageEncrypted = protocol.encrypt( data.getBytes( StandardCharsets.UTF_8 ) ,privateKey, publicKeys.get( i ), key   );
                        ArrayList<Object> messageWithReceiver = new ArrayList<>( 2 );
                        messageWithReceiver.add( userNames.get( i ) );
                        messageWithReceiver.add( messageEncrypted );
                        out.writeObject( messageWithReceiver );
                        //out.writeObject(protocol);
                    }
                }
            } catch ( IOException e ) {
                closeConnection( );
            } catch ( NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e ) {
                e.printStackTrace( );
            }
        }
        else  {
            System.out.println("Client is not connected");
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
                        String messageDecrypted = new String( protocol.decrypt( (byte[]) messageWithUserName.get( 1 ),key, privateKey,publicKey )   );
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
