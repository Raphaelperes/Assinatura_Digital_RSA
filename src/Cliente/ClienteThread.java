package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.json.Json;
import javax.json.JsonObject;

public class ClienteThread extends Thread{
	private BufferedReader reader;
	private Cliente client;
	public ClienteThread(Socket socket, Cliente client) throws IOException{
		 this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		 this.client = client;
	}
	public void run() {
		while(true) {
			JsonObject jsonObject = Json.createReader(reader).readObject();
			if(jsonObject.containsKey("e")) {
				RSA.handleReceivePublicKey(jsonObject, client);
			}else if(jsonObject.containsKey("x")) {
				RSA.handleReceiveMessage(jsonObject, client);
			}
			
		}
	}
}
