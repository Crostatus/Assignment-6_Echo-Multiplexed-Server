import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.text.SimpleDateFormat;

public class EchoClient extends Thread {
    private static int i = 0;
    private static SimpleDateFormat formatter = new SimpleDateFormat("[hh:mm:ss]");

    private int id;
    private SocketChannel clientSocket;
    private ByteBuffer byteBuffer;
    private String stringToSend;

    public EchoClient(String newStringToSend){
        this.id = i;
        i++;
        this.stringToSend = format(newStringToSend);
        try {
            clientSocket = SocketChannel.open(new InetSocketAddress(5672));
            byteBuffer = ByteBuffer.allocate(512);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        sendMessage();
        System.out.println(formatter.format(new Date()) + "[CLIENT " + id + "] Just sent the message: " + stringToSend);
        String response = readResponse(); 
        System.out.println(formatter.format(new Date()) + "[CLIENT " + id + "] Server response: " + response);
        try{
            clientSocket.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(formatter.format(new Date()) + "[CLIENT " + id + "] Connection with the server closed.");

    }

    private void sendMessage(){
        byteBuffer.put(stringToSend.getBytes());
        byteBuffer.flip();
        while(byteBuffer.hasRemaining()){
            try{
                clientSocket.write(byteBuffer);                
            } 
            catch(Exception e){
                e.printStackTrace();
            }
        }
        byteBuffer.clear();
    }

    private String format(String newStringToSend){
        if(newStringToSend.equals(null))
            return "Default message._EOM";
        else if(!newStringToSend.endsWith("_EOM")){
            newStringToSend += "_EOM";
            return newStringToSend;
        }
    return newStringToSend;
    }

    private String readResponse(){
        String result = "";
        try {    
            int bytesRead = clientSocket.read(byteBuffer);
            byteBuffer.flip();
            byte[] data = new byte[bytesRead];
            byteBuffer.get(data);
            result = new String(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
