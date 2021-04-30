package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;


// classe para conectar ao servidor, tem que passar o nome e os numeros primos que iram ser utilizados para o calculo
public class Cliente {
	private String name = null;
	private String otherPartyUsername = null;
	private BigInteger n = null;
	private BigInteger encryptN = null;
	private BigInteger phi = null;
	private BigInteger e = null;
	private BigInteger encryptE = null;
	private BigInteger d = null;
	private PrintWriter printWriter;
	public static void main(String[] args) throws UnknownHostException, IOException {
		Cliente client = new Cliente();
		Socket socket =  new Socket("localhost",4444);
		new ClienteThread(socket, client).start();
		client.printWriter = new PrintWriter(socket.getOutputStream(),true);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		StringWriter stringWriter = new StringWriter();
		RSA.handleGenerateKeys(bufferedReader, stringWriter, client);
		while(true){
			RSA.sendMessage(bufferedReader, client);
		}
	}
	public BigInteger getE() {
			return e;
	}
	public void setE(BigInteger e) {
		this.e = e;
	}
	public PrintWriter getPrintWriter() {
		return printWriter;
	}
	public void setPrintWriter(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}
	public BigInteger getEncryptN() {
		return encryptN;
	}
	public BigInteger getEncryptE() {
		return encryptE;
	}
	public void setD(BigInteger d) {
		this.d = d;
	}
	public BigInteger getPhi() {
		return phi;
	}
	public void setPhi(BigInteger phi) {
		this.phi = phi;
	}
	public void setN(BigInteger n) {
		this.n = n;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public BigInteger getN() {
		return n;
	}
	public BigInteger getD() {
		return d;
	}
	public String getOtherPartyUsername() {
		return otherPartyUsername;
	}
	public void setEncryptE(BigInteger encryptE) {
		this.encryptE = encryptE; 
	}
	public void setEncryptN(BigInteger encryptN) {
		this.encryptN = encryptN;
	}
	public void SetOtherPartyUsername(String otherPartyUsername) {
		this.otherPartyUsername = otherPartyUsername;
	}
	
}
