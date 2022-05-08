import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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


                //Socket protocolos = server.accept();


                Socket client = server.accept( ) ;


                //Scanner entrada2 = new Scanner(protocolos.getInputStream()); //socket para protocolos

                //tratar dados de protocolos (ie: adicionar a um array de clientes ?)

                Scanner entrada = new Scanner(client.getInputStream());


                System.out.println(entrada);

                ClientHandler clientHandler = new ClientHandler(clients, client );
                Thread thread = new Thread( clientHandler );
                thread.start( );
            }
        } catch ( IOException | ClassNotFoundException e ) {
            try {
                closeConnection( );
            } catch ( IOException ex ) {
                ex.printStackTrace( );
            }
        }
    }

    private void closeConnection () throws IOException {
        server.close( );
    }

}