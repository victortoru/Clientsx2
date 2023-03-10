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

public class TcpSocketClient{
        Scanner sc = new Scanner(System.in);
    public void connect(String address, int port) {
        String serverData;
        String request;
        boolean continueConnected=true;
        Socket socket;
        BufferedReader in;
        PrintStream out;
        try {
            socket = new Socket(InetAddress.getByName(address), port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
//el client atén el port fins que decideix finalitzar
            while(continueConnected){
                serverData = in.readLine();
//processament de les dades rebudes i obtenció d'una nova petició
                request = getRequest(serverData);
//enviament de la petició
                out.println(request);//assegurem que acaba amb un final de línia
                out.flush(); //assegurem que s'envia
//comprovem si la petició és un petició de finalització i en cas
//que ho sigui ens preparem per sortir del bucle
                continueConnected = mustFinish(request);
            }

            close(socket);
        } catch (UnknownHostException ex) {
            System.out.println("Error de connexió. No existeix el host" + ex);
        } catch (IOException ex) {
            System.out.println("Error de connexió indefinit" + ex);
        }
    }

    private String getRequest(String serverData) {
        System.out.println(serverData);
        System.out.print("");
        String resposta = sc.nextLine();
        return resposta;
    }

    private boolean mustFinish(String request) {
        if (request.equals("bye")) {
            return false;
        }
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
            Logger.getLogger(TcpSocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        TcpSocketClient tcpSocketClient = new TcpSocketClient();
        tcpSocketClient.connect("localhost", 9090);
    }
}