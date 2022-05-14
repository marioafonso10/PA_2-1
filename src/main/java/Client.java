import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.getInteger;

public class Client {

    private final Socket client;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final String userName;


    private String Key = null;

    private final Protocol protocol;
    private SecretKey secretkey;

    public String getKey() {
        return Key;
    }


    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }

    public SecretKey getSecretKey() {

        return this.secretkey;
    }

    //private SecretKey key;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getUserName() {
        return userName;
    }

    public Client(String host, int port, String userName, Protocol protocol) throws IOException, NoSuchAlgorithmException {
        client = new Socket(host, port);
        this.privateKey = protocol.generateKeyPair();
        this.publicKey = protocol.getPublicKey();
        this.userName = userName;
        this.protocol = protocol;
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
        out.writeObject(userName);
        out.writeObject(protocol.getPublicKey());
    }


    public void Handshake() throws IOException {
        System.out.println("CLIENT_HELLO");
        ArrayList<Object> message = new ArrayList<>(7);
        message.add(0, "handshake");

        message.add(1, this.getPublicKey());
        message.add(2, this.getPrivateKey());
        message.add(3, this.getUserName());
        message.add(4, this.getKey());
        message.add(5, this.getProtocol());
        message.add(6, this.getSecretKey());
        out.writeObject(message);
    }

    public void HandshakeConfirm() throws IOException, ClassNotFoundException {
        ArrayList<Object> messageConfirm = (ArrayList<Object>) in.readObject();
        if (messageConfirm.get(0) == "handshake" && messageConfirm.get(1) == this.userName) {
            System.out.println(" CLIENT_OK");
        }
    }

    public void sendOneMessage(byte[] data, Client user) throws IOException {
        if (client.isConnected()) {
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

        } else {
            System.out.println("Client is not connected");
        }
    }

    public void readMessages() {
        new Thread(() -> {
            while (client.isConnected()) {
                try {
                    ArrayList<Object> messageWithUserName = (ArrayList<Object>) in.readObject();
                    String userName = (String) messageWithUserName.get(0);
                    if (userName == this.getUserName()) {

                    byte[] messageEncrpted = (byte[]) messageWithUserName.get(1);
                    getProtocol().decrypt(messageEncrpted, this.getKey());
                    System.out.println(userName + ": " + messageEncrpted);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    try {
                        closeConnection();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
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
        }).start();
    }

    public void sendMessages () throws IOException {//todo isto
        new Thread(()->{
            while ( client.isConnected( ) ) {
                Scanner usrInput = new Scanner(System.in);
                String message = usrInput.nextLine();
                ;
                ArrayList<Object> messageWithUserName = new ArrayList<>(3);
                messageWithUserName.add(0, "messsage");
                try {
                    byte[] encryptedMessage = this.getProtocol().encrypt(message.getBytes(Charset.forName("UTF-8")), this.getPublicKey(), this.getKey());
                    messageWithUserName.add(1, encryptedMessage);
                    messageWithUserName.add(2,encryptedMessage);
                    out.writeObject(message);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

        public ArrayList <Client> MessageAnalizer(byte[] message){
        ArrayList <Client> clientes = null;
        String messageS= new String(message);
            if( messageS.startsWith("@")){//Todo: isto



            }
            return clientes;
        }

    private void closeConnection () throws IOException {
        client.close( );
        out.close( );
        in.close( );
    }



}
