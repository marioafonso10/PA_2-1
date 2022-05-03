import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MainClient {

    public static void main ( String[] args ) throws IOException, NoSuchAlgorithmException {
        Scanner usrInput = new Scanner( System.in );
        System.out.println( "Write your username" );
        String userName = usrInput.nextLine( );
        System.out.println( "which protocol do you want to use?" +
                "1-AES" +
                "2-2DES" +
                "3-3DES" +
                "4-RSA" );
        String protocol = usrInput.nextLine( );
        switch(protocol){
            case "1":
                System.out.println("AES");
                Protocol AES = new AES();
                Client client1 = new Client( "127.0.0.1" , 8000 , userName ,AES);
                client1.readMessages(AES );
                client1.sendMessages(AES);
                break;
            case "2":
                System.out.println("2DES");
                Protocol doisDes = new doisDes();
                Client client2 = new Client( "127.0.0.1" , 8000 , userName ,doisDes);
                client2.readMessages(doisDes );
                client2.sendMessages( doisDes);
                break;
            case "3":
                System.out.println("3DES");
                Protocol tresDes = new tresDes();
                Client client3 = new Client( "127.0.0.1" , 8000 , userName ,tresDes);
                client3.readMessages( tresDes);
                client3.sendMessages(tresDes );
                break;
            case "4":
                System.out.println("RSA");
                Protocol RSA = new RSA();
                Client client4 = new Client( "127.0.0.1" , 8000 , userName ,RSA );
                client4.readMessages(RSA );
                client4.sendMessages(RSA );

                break;

        }



    }

}
