import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>( );
    public static final ArrayList<String> userNames = new ArrayList<>( );
    public static final ArrayList<PublicKey> publicKeys = new ArrayList<>( );

    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final String userName;
    private final Socket server;
    private final PublicKey publicKey;

    public ClientHandler ( Socket server ) throws IOException, ClassNotFoundException {
        this.server = server;
        this.in = new ObjectInputStream( server.getInputStream( ) );
        this.out = new ObjectOutputStream( server.getOutputStream( ) );
        this.userName = (String) in.readObject( );
        this.publicKey = (PublicKey) in.readObject( );
        clientHandlers.add( this );
        userNames.add( userName );
        publicKeys.add( publicKey );
        updateClientPublicKeys( );
    }

    private void updateClientPublicKeys () throws IOException {
        for ( ClientHandler client : clientHandlers ) {
            client.out.writeObject( "UPDATE_PUBLIC_KEYS" );
            client.out.writeObject( publicKeys.size( ) );
            for ( int i = 0; i < publicKeys.size( ); i++ ) {
                client.out.writeObject( publicKeys.get( i ) );
                client.out.writeObject( userNames.get( i ) );
            }
            client.out.flush( );
        }
    }

    @Override
    public void run () {
        while ( server.isConnected( ) ) {
            try {
                ArrayList<Object> messageWithReceiver = (ArrayList<Object>) in.readObject( );
                String userNameReceived = (String) messageWithReceiver.get( 0 );
                byte[] message = (byte[]) messageWithReceiver.get( 1 );
                broadcastMessage( userNameReceived , message );
            } catch ( IOException | ClassNotFoundException e ) {
                try {
                    removeClient( this );
                    break;
                } catch ( IOException ex ) {
                    ex.printStackTrace( );
                }
                e.printStackTrace( );
            }
        }
    }

    private void removeClient ( ClientHandler client ) throws IOException {
        clientHandlers.remove( client );
        publicKeys.remove( publicKey );
        updateClientPublicKeys( );
        server.close( );
        in.close( );
        out.close( );
    }

    public void broadcastMessage ( String userNameReceived , byte[] message ) throws IOException {
        for ( ClientHandler client : clientHandlers ) {
            if ( client.getUserName( ).equals( userNameReceived ) ) {
                try {
                    ArrayList<Object> messageWithUserName = new ArrayList<>( 2 );
                    messageWithUserName.add( this.userName );
                    messageWithUserName.add( message );
                    client.out.writeObject( messageWithUserName );
                    client.out.flush( );
                } catch ( IOException e ) {
                    removeClient( client );
                }
            }
        }
    }

    public String getUserName () {
        return userName;
    }
}
