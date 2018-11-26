import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

	public static void main(String[] args) throws IOException {

		// Cria um socket na porta 12345
		ServerSocket servidor = new ServerSocket(12345);
		System.out.println("Porta 12345 aberta!");

		System.out.print("Aguardando conexão do cliente...");
		Socket cliente = servidor.accept();

		System.out.println("Nova conexao com o cliente " + cliente.getInetAddress().getHostAddress());

		// Recebe a mensagem enviada pelo cliente
		Scanner s = new Scanner(cliente.getInputStream());
		
		// Exibe mensagem no console
		while (s.hasNextLine()) {
			String line = s.nextLine();
			String[] get = line.split(" ");
			
			//Filtra a linha do GET dentro do cabeçalho HTTP enviado na solicitação
			if (get.length > 0 && get[0].equals("GET")) {
				
				String fileName = get[1];
				
				//Procura arquivo solicitado no GET
				boolean arquivoExiste = procuraArquivo(fileName);
				System.out.println(fileName + " existe? = " + arquivoExiste);
				
				//Se arquivo encontrado entra
				if (arquivoExiste) {
					
					//Abre o arquivo solicitado
					File myFile = new File(fileName.substring(1));
					FileInputStream fis = new FileInputStream(myFile);
					
					//Cria o canal de retorno para envio de arquivo binário
					BufferedOutputStream bf = new BufferedOutputStream(cliente.getOutputStream());

					//Cria o canal de retorno para envio do cabeçalho HTTP
					PrintWriter out = new PrintWriter(cliente.getOutputStream());

					//Carrega o arquivo em um array de bytes para envio
					byte[] mybytearray = new byte[(int) myFile.length()];
					BufferedInputStream bis = new BufferedInputStream(fis);
					bis.read(mybytearray, 0, mybytearray.length);

					System.out.println("Sending " + fileName + "(" + mybytearray.length + " bytes)");
					
					//Enviando os Cabeçalhos de HTTP
					out.println("HTTP/1.1 501 Not Implemented");
					out.println("Server: Java HTTP Server from Luciano : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: text/html");
					out.println("Content-length: " + mybytearray.length);
					out.println(); // uma linha em branco no final do cabeçalho para indicar seu fim. 
					out.flush();
					
					//Envio do arquivo
					bf.write(mybytearray, 0, mybytearray.length);
					bf.flush();
					
					System.out.println("Done.");
					break;
				}
			}
		}

		// Finaliza objetos - fecha conexão.
		s.close();
		cliente.close();
		servidor.close();
		System.out.println("Fim do Servidor!");
	}

	/**
	 * Este método verifica se existe o arquivo solicitado no mesmo diretório da aplicação.
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean procuraArquivo(String fileName) {
		try {
			// Cria o caminho
			String path = new File(".").getCanonicalPath() + "\\" + fileName.substring(1);
			//Abre o arquivo
			File file = new File(path);
			return file.exists();
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
	
}
