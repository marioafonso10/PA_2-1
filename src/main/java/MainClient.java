import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MainClient {

    public static void main ( String[] args ) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Scanner usrInput = new Scanner( System.in );
        System.out.println( "Introduza o seu nome de utilizador:");
        boolean escolha=false;
        String userName = usrInput.nextLine( );
        while(!escolha){
        System.out.println( "Escolha o protocolo de encriptação a ser utilizado: \n" +
                "1-AES \n" +
                "2-DES \n" +
                "3-RSA" );
        String protocol = usrInput.nextLine( );
        Socket server= new Socket("127.0.0.1",8000);
            switch(protocol){
                case "1":
                    escolha= true;
                    System.out.println("AES");
                    Protocol AES = new AES();
                    Client client1 = new Client( "127.0.0.1" , 8000 , userName ,AES);
                    ClientHandler handler= new ClientHandler(server);
                    client1.Handshake();
                    //handler.handshakeConfirm();
                    client1.HandshakeConfirm();
                    client1.readMessages();
                    client1.sendMessages();
                    break;
                case "2":
                    escolha= true;

                    System.out.println("2DES");
                    Protocol Des = new Des();
                    Client client2 = new Client( "127.0.0.1" , 8000 , userName ,Des);
                    client2.setSecretkey(Des.getSecretKey());
                    client2.Handshake();
                    client2.HandshakeConfirm();
                    client2.readMessages();
                    client2.sendMessages( );
                    break;

                case "3":
                    escolha= true;
                    System.out.println("RSA");
                    Protocol RSA = new RSA();
                    Client client4 = new Client( "127.0.0.1" , 8000 , userName ,RSA );
                    client4.Handshake();
                    client4.HandshakeConfirm();
                    client4.readMessages( );
                    client4.sendMessages( );
                    break;
                default:
                    System.out.println("Escolha uma das opções apresentadas");
                    break;

            }

        }


        System.out.println("C");



    }

}
