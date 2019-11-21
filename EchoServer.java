import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;
import java.text.SimpleDateFormat;

public class EchoServer extends Thread {
    //An EchoServer thread uses a ServerSocketChannel to listen for any new connection request on the local port 5672 for 10 seconds once it gets started
    //It uses a non-blocking approach
    private SimpleDateFormat formatter;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public EchoServer() {
        formatter = new SimpleDateFormat("[hh:mm:ss]");
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(5672));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 10000) {
            try {
                selector.select();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey nextKey = iterator.next();

                if(!nextKey.isValid())
                    continue;

                if (nextKey.isAcceptable()) {
                    accept();
                    System.out.println(formatter.format(new Date()) + "[SERVER] New connection request accepted.");
                }
                if(nextKey.isReadable()) {
                    System.out.println(formatter.format(new Date()) + "[SERVER] New message received.");
                    readRequest(nextKey);
                }
                if(nextKey.isWritable()) {
                    writeRequest(nextKey);
                }
                iterator.remove();
            }
        }
        close();
    }

    private void close() {
        try{
        selector.close();
        serverSocketChannel.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    private void writeRequest(SelectionKey newWriteRequest){
        SocketChannel client = (SocketChannel) newWriteRequest.channel();
        EchoByteBuffer attachment = (EchoByteBuffer) newWriteRequest.attachment();
        try {
            client.write(attachment.getByteBuffer());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readRequest(SelectionKey newReadRequest){
        SocketChannel client = (SocketChannel) newReadRequest.channel();
        EchoByteBuffer attachment = (EchoByteBuffer) newReadRequest.attachment();
        try {
            attachment.getByteBuffer().clear();
            client.read(attachment.getByteBuffer());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(attachment.updateOnRead()){
            newReadRequest.interestOps(SelectionKey.OP_WRITE);
        }

    }

    private void accept(){
        try {
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ, new EchoByteBuffer());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
