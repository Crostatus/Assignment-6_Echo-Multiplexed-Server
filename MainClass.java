public class MainClass {

    public static void main(String[] args) {
        EchoServer myTestServer = new EchoServer();
        myTestServer.start();

        EchoClient testClient0 = new EchoClient("Ciao Mondo!");
        EchoClient testClient1 = new EchoClient("Pici cacio e pepe");
        EchoClient testClient2 = new EchoClient("Forza Portoscuso!");
        EchoClient testClient3 = new EchoClient("Crostata di pere");
        EchoClient testClient4 = new EchoClient("Carciofi alla romana");
        EchoClient testClient5 = new EchoClient("Pasta alla gricia");

        try{
            Thread.sleep(500);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }

        testClient0.start();
        testClient1.start();
        testClient2.start();
        testClient3.start();
        testClient4.start();
        testClient5.start();

    }




}
