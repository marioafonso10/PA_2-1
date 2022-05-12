import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer {
    public static ArrayList <Client> clients;
    public static void main ( String[] args ) throws IOException, ClassNotFoundException {
        Server server = new Server( 8000);
        Thread serverThread = new Thread( server );
        serverThread.start( );

    }

}
