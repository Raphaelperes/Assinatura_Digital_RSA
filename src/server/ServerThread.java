package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author rapha
 */
public class ServerThread extends Thread{
    private Server server;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    public ServerThread(Socket socket,Server server)throws IOException{
        this.server = server;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(),true);
        
    }
    void forwardMessage(String message){
        printWriter.println(message);
    }
    public void run(){
        JsonObject jsonObject = null;
    try{  
        while(true){
            jsonObject = Json.createReader(bufferedReader).readObject();
            System.out.println("[System]:"+ jsonObject.toString());
            if(jsonObject.containsKey("e")){
                System.out.println("[Eve]"+jsonObject.getString("name")+" \r\n"
                		+ "chave pública de(n,e) = ("+ 
                        jsonObject.getString("n")+ "," + jsonObject.getString("e")+").");
                System.out.println("            Necessita da chave privada d");
                System.out.println("             onde d * e mod phi (n)");
                System.out.println("             mas não tem phi (n). ");
                System.out.println("            para obtê-lo, pode-se usar a fórmula: phi (n) = (p-1) (q-1)");
                System.out.println("             onde p * q = n e ambos os #s são primos");
                System.out.println("             ou seja, precisa fazer a fatoração principal de n em p & q");
                
            }
            server.forwardMessage(jsonObject.toString(),this);
        }
    }catch(Exception e){
        server.getServerThreads().remove(this);
    }

}
}
