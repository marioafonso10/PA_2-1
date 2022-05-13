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


    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>( );
    public static ArrayList<String> userNames = new ArrayList<>( );

    private Protocol protocol;
    private final ObjectInputStream in;

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    private final ObjectOutputStream out;
    private  String userName=null;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getKey() {
        return key;
    }

    public SecretKey getSecretkey() {
        return secretkey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }

    private final Socket server;
    private PublicKey publicKey=null;
    private PrivateKey privateKey= null;
    private String key=null;
    private SecretKey secretkey;

    public ClientHandler( Socket server) throws IOException, ClassNotFoundException {

        this.server = server;
        this.in = new ObjectInputStream( server.getInputStream( ) );
        this.out = new ObjectOutputStream( server.getOutputStream( ) );


    }



    @Override
    public void run () {
        while ( server.isConnected( ) ) {
            try {
                ArrayList<Object> messageWithReceiver = (ArrayList<Object>) in.readObject( );
                String userNameReceived = (String) messageWithReceiver.get( 1 );
                if(messageWithReceiver.get(0) =="handshake"){

                    handshakeConfirm( messageWithReceiver );


                }
                else{

                    broadcastMessage(decryptMessage(messageWithReceiver), (String) messageWithReceiver.get(1));
                }

            } catch ( IOException | ClassNotFoundException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e ) {
                e.printStackTrace();
            }
        }
    }


    public byte[] encryptMessage(ClientHandler client, byte[] message, SecretKey key) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        byte[] message_encrypted= client.getProtocol().encrypt(message,this.getProtocol().getPublicKey(),getProtocol().getKey());
        return message_encrypted;
    }

    public byte[] decryptMessage(ArrayList<Object> messageWithReceiver) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        byte[] message = (byte[]) messageWithReceiver.get( 2 );
        for (ClientHandler clientHandler: clientHandlers) {
            if(messageWithReceiver.get(1)== clientHandler.getUserName()){
                message =clientHandler.getProtocol().decrypt(message,clientHandler.getKey());
            }
        }
        return message;
    }


    public void broadcastMessage ( byte[] message,String user ) throws IOException {
        for ( ClientHandler client : clientHandlers ) {
                if(client.userName!= user) {
                    try {
                        ArrayList<Object> messageWithUserName = new ArrayList<>(2);
                        messageWithUserName.add(0, this.userName);
                        byte[] encrypted_message = encryptMessage(client, message,client.getSecretkey());
                        messageWithUserName.add(1, encrypted_message);
                        client.out.writeObject(messageWithUserName);
                        client.out.flush();


                    } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }

        }
    }
    public void groupMessage(){ //TODO:Criar metodo que recebe so recebe mensagem e envia para o grupo de pessoas nela indicado

    }
    public void handshakeConfirm(ArrayList<Object> messageWithReceiver ) throws IOException, ClassNotFoundException {

        ClientHandler handler= new ClientHandler(server);
        handler.setKey((String) messageWithReceiver.get(4));
        handler.setPrivateKey((PrivateKey) messageWithReceiver.get(2));
        handler.setPublicKey((PublicKey) messageWithReceiver.get(1));
        handler.setSecretkey((SecretKey) messageWithReceiver.get(6));
        handler.setProtocol((Protocol) messageWithReceiver.get(5));
        handler.setUserName((String) messageWithReceiver.get(3));
        clientHandlers.add(handler);
        System.out.println("");

        System.out.println("SERVER_OK");
        ArrayList<Object> messageConfirm = new ArrayList<Object>(2);
        messageConfirm.add(1,"handshake");
        messageConfirm.add(2,handler.getUserName());
        out.writeObject(messageConfirm);
    }

    public void sendOneMessage( byte[] data, ClientHandler user) throws IOException {
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
