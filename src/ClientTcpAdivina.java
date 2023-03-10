import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientTcpAdivina extends Thread {
    /* CLient TCP que ha endevinar un número pensat per SrvTcpAdivina.java */

    String hostname;
    int port;
    boolean continueConnected;
    int intents;

    public ClientTcpAdivina(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        continueConnected = true;
        intents=0;
    }

    public void run() {
        String serverData;
        String request;

        Socket socket;
        BufferedReader in;
        PrintStream out;

        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            //el client atén el port fins que decideix finalitzar
            while(continueConnected){
                serverData = in.readLine();
                //processament de les dades rebudes i obtenció d'una nova petició
                request = getRequest(serverData);
                //enviament el número i els intents
                out.println(request);
                out.println(intents);
                out.flush();

            }
            close(socket);
        } catch (UnknownHostException ex) {
            System.out.println("Error de connexió. No existeix el host: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error de connexió indefinit: " + ex.getMessage());
        }

    }

    public String getRequest(String serverData) {
        String ret;
        System.out.println(serverData);
        if( serverData.equals("Correcte") ) {
            continueConnected = false;
            ret = "Campió!";
        } else {
            Scanner in = new Scanner(System.in);
            System.out.print("Digues un número: ");
            ret = new String(in.next());
            intents++;
        }

        return ret;

    }

    public boolean mustFinish(String dades) {
        if (dades.equals("exit")) return false;
        return true;

    }

    private void close(Socket socket){
        //si falla el tancament no podem fer gaire cosa, només enregistrar
        //el problema
        try {
            //tancament de tots els recursos
            if(socket!=null && !socket.isClosed()){
                if(!socket.isInputShutdown()){
                    socket.shutdownInput();
                }
                if(!socket.isOutputShutdown()){
                    socket.shutdownOutput();
                }
                socket.close();
            }
        } catch (IOException ex) {
            //enregistrem l'error amb un objecte Logger
            Logger.getLogger(ClientTcpAdivina.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
		/*if (args.length != 2) {
            System.err.println(
                "Usage: java ClientTcpAdivina <host name> <port number>");
            System.exit(1);
        }*/

        // String hostName = args[0];
        // int portNumber = Integer.parseInt(args[1]);
        ClientTcpAdivina clientTcp = new ClientTcpAdivina("localhost",5558);
        clientTcp.start();
    }
}