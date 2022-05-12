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

    private final Protocol protocol;
    private String key;

    public PrivateKey getPrivateKey() { return privateKey; }
    public PublicKey getPublicKey() {
        return publicKey;
    }
    public String getKey() {return key;}
    public Protocol getProtocol() {
        return protocol;
    }
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


    public void Handshake( ArrayList<Object> setup_protocolos) throws IOException {//TODO: completar funcionamento do handshake na main do cliente ie: depois da escolha de protocolos executar função para mandar para o server
        System.out.println("CLIENT_HELLO");
        ArrayList<Object> message = new ArrayList<>( 3 );
        message.add(0,"handshake");
        message.add( 1,userName );
        message.add(2,setup_protocolos);
        out.writeObject( message );
    }

    public void sendMessages () throws IOException {
        while ( client.isConnected( ) ) {
            Scanner usrInput = new Scanner( System.in );
            String message = usrInput.nextLine( );

            try {
                for ( int i = 0; i < userNames.size( ); i++ ) {
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

    public void sendOneMessage( byte[] data, Client user) throws IOException {
        if ( client.isConnected( ) ) {
            try {
                for ( int i = 0; i < publicKeys.size( ); i++ ) {
                    if ( ! user.getUserName().equals( userNames.get( i ) ) ) {
                        String key = null;
                        byte[] messageEncrypted = protocol.encrypt( data ,privateKey, publicKeys.get( i ), key   );
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


  /*  public void readMessages () {
        new Thread(() -> {
            while (client.isConnected()) {
                try {
                        ArrayList<Object> messageWithReceiver = new ArrayList<>( 3 );
                        messageWithReceiver= client.getInputStream();
                    if (message instanceof String) {
                        if (message.equals("UPDATE_PUBLIC_KEYS")) {
                            int numberOfClients = (int) in.readObject();
                            publicKeys.clear();
                            userNames.clear();
                            for (int i = 0; i < numberOfClients; i++) {
                                PublicKey publicKey = (PublicKey) in.readObject();
                                publicKeys.add(publicKey);
                                String userName = (String) in.readObject();
                                userNames.add(userName);
                            }
                        }
                    } else {
                        ArrayList<Object> messageWithUserName = (ArrayList<Object>) message;
                        String key = null;
                        String userName = (String) messageWithUserName.get(0);
                        String messageDecrypted = new String(protocol.decrypt((byte[]) messageWithUserName.get(1), key, privateKey, publicKey));
                        System.out.println(userName + ": " + messageDecrypted);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    try {
                        closeConnection();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    */
    public void readMessage(byte[] message){

        //String msg = new String(message);

            new Thread( () -> {
                while ( client.isConnected( ) ) {
                    try {
                        {
                            String key = null ;
                            String userName = getUserName();
                            String messageDecrypted = new String( protocol.decrypt( message,key, privateKey,publicKey )   );
                            //System.out.println( userName + ": " + messageDecrypted );
                            ArrayList <Client> destinatarios= MessageAnalizer(message);
                            if(!destinatarios.isEmpty()){
                                //TODO:envia para x destinatarios
                                for (Client z:destinatarios) {
                                    z.sendOneMessage(message,z);
                                }
                            }
                            else{
                                // broadcast message
                            }
                            
                        }
                    } catch ( IOException  e ) {
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
        public ArrayList <Client> MessageAnalizer(byte[] message){
        ArrayList <Client> clientes = null;
        String messageS= new String(message);
            if( messageS.startsWith("@")){//Todo: Mario
                // percorrer mensagem e ler os varios destinatarios para quem vai ser direcionada a mensagem 
            }
            return clientes;
        }

    private void closeConnection () throws IOException {
        client.close( );
        out.close( );
        in.close( );
    }



}
