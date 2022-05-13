import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>( );
    public static ArrayList<String> userNames = new ArrayList<>( );

    public static final ArrayList<PublicKey> publicKeys = new ArrayList<>( );

    public Client getClient() {
        return client;
    }

    private Client client;
    private Protocol protocol;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final String userName;

    private final Socket server;
    private final PublicKey publicKey;

    public ClientHandler(Client client, Socket server) throws IOException, ClassNotFoundException {
        this.client=client;
        this.server = server;
        this.in = new ObjectInputStream( server.getInputStream( ) );
        this.out = new ObjectOutputStream( server.getOutputStream( ) );
        this.userName = client.getUserName();
        this.publicKey = client.getPublicKey();
        this.protocol= client.getProtocol();
        clientHandlers.add( this );
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
                String userNameReceived = (String) messageWithReceiver.get( 1 );
                if(messageWithReceiver.get(0) =="handshake"){
                    Protocol AES= new AES(); //TODO retirar após ler do handshake!!!!!
                    //TODO para completar handshake temos de completar primeiro o handshake do lado do cliente

                    Client new_client= new Client("127.0.0.1" , 8000 , userName ,AES);
                }
                else{

                    broadcastMessage(decryptMessage(messageWithReceiver), (String) messageWithReceiver.get(1));// Todo verificar logica e funcionamento !!
                }

            } catch ( IOException | ClassNotFoundException e ) {
                try {
                    removeClient( this );
                    break;
                } catch ( IOException ex ) {
                    ex.printStackTrace( );
                }
                e.printStackTrace( );
            } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }


    public byte[] encryptMessage(Client client, byte[] message, SecretKey key) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        byte[] message_encrypted= client.getProtocol().encrypt(message,getClient().getProtocol().getPublicKey(),getClient().getProtocol().getKey());//Todo: para fazer falta acrescentar ciração de keys no cliente e verificar restantes algoritmos de encypt
        return message_encrypted;
    }

    public byte[] decryptMessage(ArrayList<Object> messageWithReceiver) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        byte[] message = (byte[]) messageWithReceiver.get( 2 );
        for (ClientHandler clientHandler: clientHandlers) {
            if(messageWithReceiver.get(1)== clientHandler.getUserName()){
                message =clientHandler.getClient().getProtocol().decrypt(message,clientHandler.getClient().getKey());//TODO : melhorar maneira de aceder aos  ecnrypt de cada protocolo
            }
        }
        return message;
    }

    private void removeClient ( ClientHandler client ) throws IOException {
        clientHandlers.remove( client );
        publicKeys.remove( publicKey );
        updateClientPublicKeys( );
        server.close( );
        in.close( );
        out.close( );
    }

    public void broadcastMessage ( byte[] message,String user ) throws IOException {// TODO: modificar para receber array e mandar mensagem para todos encriptada segundo o seu Protocolo
        for ( ClientHandler client : clientHandlers ) {// TODO: DUVIDA como aceder aqui a cliente/ se é possivel trabalhar com os clientHandlers?
                if(client.userName!= user) {
                    try {
                        ArrayList<Object> messageWithUserName = new ArrayList<>(2);
                        messageWithUserName.add(0, this.userName);
                        byte[] encrypted_message = encryptMessage(client.getClient(), message);
                        messageWithUserName.add(1, encrypted_message);
                        client.out.writeObject(messageWithUserName);
                        client.out.flush();


                    } catch (IOException e) {
                        removeClient(client);
                    } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }

        }
    }
    public void groupMessage(){ //TODO:Criar metodo que recebe so recebe mensagem e envia para o grupo de pessoas nela indicado

    }
    public void handshakeConfirm(ArrayList<Object> dados ) throws IOException, ClassNotFoundException {

        ClientHandler handler= new ClientHandler(,server);
        dados = new ArrayList<>( 3 );
    }

    public void sendOneMessage( byte[] data, Client user) throws IOException {
        if (server.isConnected()) {
            try {
                System.out.println("Client is connected");

                byte[] messageEncrypted = protocol.encrypt(data, user.getPublicKey(), user.getKey());
                ArrayList<Object> messageWithReceiver = new ArrayList<>(2);
                messageWithReceiver.add(user.getUserName());
                messageWithReceiver.add(messageEncrypted);
                out.writeObject(messageWithReceiver);
                //out.writeObject(protocol);


            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

        }
        else {
            System.out.println("Client is not connected");
        }
    }

    public String getUserName () {
        return userName;
    }


}
