package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.IntStream;
import javax.json.Json;
import javax.json.JsonObject;



public class RSA {
	
	// calculo o inverso de E
	
	static BigInteger calculateD(BigInteger phi,BigInteger n, BigInteger e) {
		return e.modInverse(phi);
	}
	
	// faz o calculo da função totiente phi(n) = (p -1) (q -1)
	
	static BigInteger calculatePhi(BigInteger p,BigInteger q) {
		return ((p.subtract(BigInteger.valueOf(1))).multiply(q.subtract(BigInteger.valueOf(1))));
	}
	
	// Função que gera a chave Publica e Privada e no final retorna seus respectivos valores para serem utilizados
	// quando forem enviar uma mensagem criptografada
	
	static void handleGenerateKeys(BufferedReader bR, StringWriter sW, Cliente client) throws IOException{
		handleInput(bR, sW, client);
		client.setD(RSA.calculateD(client.getPhi(), client.getN(), client.getE()));
		System.out.println("["+client.getName()+"]:d*"+client.getE()+" 1 mod	"+client.getPhi()+" ===> d ="+client.getD()+".");
		Json.createWriter(sW).writeObject(Json.createObjectBuilder().add("name",client.getName())
																	.add("e",client.getE().toString())
																	.add("n",client.getN().toString()).build());
		client.getPrintWriter().println(sW);
		System.out.println("Minha chave pública (n, e) = ("+client.getN()+","+client.getE()+") | minha chave privada d = "+client.getD());
		if(client.getN() != null && client.getE() != null && client.getEncryptN() != null && client.getEncryptE() != null) {
			System.out.println("[System]: pronto para enviar e receber mensagens assinadas");
		}
	}
	
	// Função para Lidar com a Entrada de Numeros primos Distintos
	
	private static void handleInput(BufferedReader bR,StringWriter sW,Cliente client)throws IOException{
		boolean flag = true;
		BigInteger p = new BigInteger("0"), q = new BigInteger("0");
		while(flag) {
			try {
				System.out.println("[System]: insira o nome de usuário e 2 números primos p! = q (separe com espaço)");
				String[] values = bR.readLine().split(" ");
				client.setName(values[0]);
				p = new BigInteger(values[1]);
				q = new BigInteger(values[2]);
				if(!p.equals(q) && isPrime(p) && isPrime(q)) { // Verifica se P e Q são Numeros primos diferentes
					flag = false;
				}else {
					System.out.println("[System]:p & q devem ser números primos distintos");
				}
			}catch(Exception e) {
				System.out.println("entrada inválida");
			}
		}
		client.setN(p.multiply(q));
		System.out.println("["+client.getName()+"]: n = p*q= "+client.getN());
		client.setPhi(RSA.calculatePhi(p, q));
		System.out.println("| phi(n) = (p-1)*(q-1)="+client.getPhi());
		while(!flag) { // Verifica se o usuario escolhe um exponte maior 1 e menor que o resultado de phi(n-1)
			try {
				System.out.println("[System]: escolha o expoente público e de ser {1,2, ..., phi (n-1)}");
				System.out.println("Onde existe o inverso de e, ou seja, mdc (e, phi (n)) = 1");
				BigInteger input = new BigInteger(bR.readLine());
				if(isRelativelyPrime(input,client.getPhi()) && input.compareTo(new BigInteger("1")) >=0 
						&& input.compareTo(client.getPhi().subtract(new BigInteger("1"))) <= 0) {
					client.setE(input);
					flag = true;
				}
			}catch(Exception e) {
				System.out.println("entrada inválida");
			}
		}
	}
	
	// Função que lida com a Chave Publica Recebida
	
	static void handleReceivePublicKey(JsonObject jsonObject, Cliente client) {
		client.setEncryptE(new BigInteger(jsonObject.getString("e")));
		client.setEncryptN(new BigInteger(jsonObject.getString("n")));
		client.SetOtherPartyUsername(jsonObject.getString("name"));
		System.out.println("[System]:chave pública de"+client.getOtherPartyUsername() +" (n, e) = ("+jsonObject.getString("n")+","+jsonObject.getString("e")+")");
		if(client.getN() != null && client.getE() != null && jsonObject.getString("n")!= null) {
			System.out.println("[System]:pronto para enviar e receber mensagens assinadas");
		}else if(client.getN() != null && client.getE() == null) {
			System.out.println("[System]: escolha público # e");
		}else if(client.getN() == null) {
			System.out.println("[System]: insira o nome de usuário e 2 números primos p & q (separar com espaço");
		}
			
	}
	
	// transforma a mensagem em numerps ASCII
	static BigInteger[] signMessage(String name, String x, BigInteger d,BigInteger n) {
		String[] xSplit = x.split(" ");
		BigInteger[] xPrime = new BigInteger[xSplit.length];
		IntStream.range(0, xSplit.length).forEach(i-> xPrime[i] = new BigInteger(xSplit[i]).modPow(d, n));
		System.out.println("["+name+"]: cantar mensagem c / tecla '"+ d+"'==> s ="+ Arrays.toString(xPrime));
		return xPrime;
	}
	
	// Função que Criptografa a mensagem
	static void sendMessage(BufferedReader bR,Cliente client) throws IOException{
		String m = bR.readLine();
		StringBuffer x = new StringBuffer();
		IntStream.range(0, m.length()).forEach(i -> x.append(RSA.characterToAscii(m.charAt(i))+" "));
		System.out.println("["+client.getName()+"]:mapear char para mensagem ascii ==> x ="+x);
		boolean flag = true;
		while(flag) {
			try {
				System.out.println("[System]: por favor insira a mensagem do sinal com a chave");
				BigInteger signingKey = new BigInteger(bR.readLine());
				BigInteger[] xPrime = signMessage(client.getName(),x.toString(), signingKey, client.getN());
				StringBuffer xPrimeSB =  new StringBuffer();
				for(int i = 0; i < xPrime.length; i++) {
					xPrimeSB.append(xPrime[i].toString()+" ");
				}
				if(client.getOtherPartyUsername()!= null) {
					System.out.println("["+client.getName()+"]:e' = (enviar mensagem assinada, assinatura) ==> (x,s) = ('"+x.toString()+"','");
					System.out.println(xPrimeSB.toString().trim()+"')'");
					StringWriter sW = new StringWriter();
					Json.createWriter(sW).writeObject(Json.createObjectBuilder().add("name", client.getName())
																				.add("x", x.toString())
																				.add("s", xPrimeSB.toString()).build());
					client.getPrintWriter().println(sW);
				}
				flag = false;
			}catch(Exception e) {
				System.out.println("entrada inválida");
			}
		}
	}
	
	// Função que verifica se a chave privada informada está correta 
	
	static boolean validateSignature(String otherPartyName, String x, BigInteger[] s, BigInteger e, BigInteger n, String name) {
		System.out.println("["+name+"]:usar chave pública de "+otherPartyName+ "  para determinar se a mensagem calculada x 'é igual à mensagem x:");
		BigInteger[] xPrime = new BigInteger[s.length];
		IntStream.range(0, s.length).forEach(i-> xPrime[i] = s[i].modPow(e, n));
		System.out.println("["+name+"]: (1) calcular mensagem ==> x 's ^ e mod n ="+ Arrays.toString(xPrime)+"");
		StringBuffer xPrimeSB = new StringBuffer();
		for(int i = 0; i < xPrime.length; i++)xPrimeSB.append(xPrime[i].intValue()+" ");	
		return (xPrimeSB.toString().trim().equals(x.trim()));
	}
	
	
	// Função para lidar com a descriptografia da mensagem vizualizando se o usuario colocou a chave privada corretamente
	// se não estiver com a chave correta retorna um erro falando que não foi informado a chave privada correta
	// assim a mensagem continua criptografada
	
	static void handleReceiveMessage(JsonObject jsonObject,Cliente client) {
		String otherPartyName = jsonObject.getString("name");
		System.out.println("["+client.getName()+"]: receber 'mensagem assinada' = (mensagem, assinatura) ==> (x, s) = ('"+ jsonObject.getString("x")
											+","+jsonObject.getString("s")+"')");
		String[] sValues = jsonObject.getString("s").split(" ");
		BigInteger[] s = new BigInteger[sValues.length];
		IntStream.range(0, sValues.length).forEach(i->s[i] = new BigInteger(sValues[i]));
		if(RSA.validateSignature(otherPartyName, jsonObject.getString("x"), s,client.getEncryptE(),client.getEncryptN(),client.getName())) {
			System.out.println("["+client.getName()+"]:(2) comparar a mensagem com a mensagem calculada: x == x '==> a mensagem foi assinada com a chave privada de "+otherPartyName);
			String[] xValues = jsonObject.getString("x").split(" ");
			BigInteger[] x = new BigInteger[xValues.length];
			IntStream.range(0, xValues.length).forEach(i-> x[i] = new BigInteger(xValues[i]));
			StringBuffer m = new StringBuffer();
			for(int i = 0; i< x.length;i++) {
				m.append(RSA.asciiToCharacter(x[i].intValue()));
				}
			System.out.println("["+client.getName()+"]:"+"map ascii para char ==>"+m);
			System.out.println("["+otherPartyName+"]:"+m);
			}else{
				System.out.println("["+client.getName()+"]:(2) comparar a mensagem com a mensagem calculada: x! = X '==> erro!!! \n A mensagem não foi assinada com a chave privada de:"+otherPartyName);
		}
	}
	static boolean isPrime(BigInteger number) {
		return number.isProbablePrime(1000);
	}
	static boolean isRelativelyPrime(BigInteger e, BigInteger phi) {
		return e.gcd(phi).intValue() == 1;
	}
	static int characterToAscii(char character) {
		return (int)character;
	}
	static char asciiToCharacter(int ascii) {
		return (char)ascii;
	}
}
