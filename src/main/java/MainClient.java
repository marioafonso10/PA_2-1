import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MainClient {

    public static void main ( String[] args ) throws IOException, NoSuchAlgorithmException {
        Scanner usrInput = new Scanner( System.in );
        System.out.println( "Introduza o seu nome de utilizador:");
        boolean escolha=false;
        String userName = usrInput.nextLine( );
        while(!escolha){
        System.out.println( "Escolho protocolo de encriptação a ser utilizado: \n" +
                "1-AES \n" +
                "2-DES \n" +
                "3-RSA" );
        String protocol = usrInput.nextLine( );
            switch(protocol){
                case "1":
                    escolha= true;
                    System.out.println("AES");
                    Protocol AES = new AES();
                    Client client1 = new Client( "127.0.0.1" , 8000 , userName ,AES);
                    ClientHandler Handler= new ClientHandler(client1,);
                    String dados=  client1.getUserName() + "XXXX" + AES ;

                   // client1.sendProtocol(dados);
                    client1.readMessages();
                    client1.sendMessages();
                    break;
                case "2":
                    escolha= true;

                    System.out.println("2DES");
                    Protocol Des = new Des();
                    Client client2 = new Client( "127.0.0.1" , 8000 , userName ,Des);

                    client2.setSecretkey(Des.getSecretKey());
                    client2.readMessages();
                    client2.sendMessages( );
                    break;

                case "3":
                    escolha= true;
                    System.out.println("RSA");
                    Protocol RSA = new RSA();
                    Client client4 = new Client( "127.0.0.1" , 8000 , userName ,RSA );
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
