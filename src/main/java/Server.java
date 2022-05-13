import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class Server implements Runnable {

    private final ServerSocket server;
    //private final ServerSocket port_server;
    private  Protocol protocol = null;
    private ArrayList <Client> clients;

    public Server ( int port ) throws IOException {
        server = new ServerSocket( port );
        //port_server = new ServerSocket(prot_port);
        this.protocol=protocol;
    }

    @Override
    public void run () {
        try {
            while ( ! server.isClosed( ) ) {

                Socket client = server.accept( ) ;

                Scanner entrada = new Scanner(client.getInputStream());


                System.out.println(entrada);
                Protocol protocol= new AES();
                Client default_client= new Client("127.0.0.1" , 8000 ,"default" ,protocol);
                ClientHandler handler = new ClientHandler( client );
                Thread thread = new Thread( handler );
                thread.start( );



            }
        } catch ( IOException | ClassNotFoundException e ) {
            try {
                closeConnection( );
            } catch ( IOException ex ) {
                ex.printStackTrace( );
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection () throws IOException {
        server.close( );
    }

}