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
                "2-2DES \n" +
                "3-3DES \n" +
                "4-RSA" );
        String protocol = usrInput.nextLine( );
            switch(protocol){
                case "1":
                    escolha= true;
                    System.out.println("AES");
                    Protocol AES = new AES();
                    System.out.println (AES) ;
                    Client client1 = new Client( "127.0.0.1" , 8000 , userName ,AES);

                    String dados=  client1.getUserName() + "XXXX" + AES ;

                    client1.sendProtocol(dados);
                    client1.readMessages(AES );
                    client1.sendMessages(AES);
                    break;
                case "2":
                    escolha= true;
                    System.out.println("2DES");
                    Protocol doisDes = new doisDes();
                    Client client2 = new Client( "127.0.0.1" , 8000 , userName ,doisDes);
                    client2.readMessages(doisDes );
                    client2.sendMessages( doisDes);
                    break;
                case "3":
                    escolha= true;
                    System.out.println("3DES");
                    Protocol tresDes = new tresDes();
                    Client client3 = new Client( "127.0.0.1" , 8000 , userName ,tresDes);
                    client3.readMessages( tresDes);
                    client3.sendMessages(tresDes );
                    break;
                case "4":
                    escolha= true;
                    System.out.println("RSA");
                    Protocol RSA = new RSA();
                    Client client4 = new Client( "127.0.0.1" , 8000 , userName ,RSA );
                    client4.readMessages(RSA );
                    client4.sendMessages(RSA );
                    break;
                default:
                    System.out.println("Escolha uma das opções apresentadas");
                    break;

            }

        }


        System.out.println("C");



    }

}
