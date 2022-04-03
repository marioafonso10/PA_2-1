import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket client;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public Client ( String host , int port , String userName ) throws IOException {
        client = new Socket( host , port );
        out = new ObjectOutputStream( client.getOutputStream( ) );
        in = new ObjectInputStream( client.getInputStream( ) );
        out.writeObject( userName );
    }

    public void sendMessages () throws IOException {
        while ( client.isConnected( ) ) {
            Scanner usrInput = new Scanner( System.in );
            String message = usrInput.nextLine( );
            try {
                out.writeObject( message );
            } catch ( IOException e ) {
                closeConnection( );
                break;
            }
        }

    }

    public void readMessages () {
        new Thread( () -> {
            while ( client.isConnected( ) ) {
                try {
                    String message = (String) in.readObject( );
                    System.out.println( message );
                } catch ( IOException | ClassNotFoundException e ) {
                    try {
                        closeConnection( );
                    } catch ( IOException ex ) {
                        ex.printStackTrace( );
                    }
                    break;
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
