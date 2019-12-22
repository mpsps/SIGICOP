package historico;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import models.Pedido;

public class GerarRelatorio {
	
	public void relatorio(Long id) throws IOException {
		
		Pedido ped = Pedido.findById(id);
		FileWriter arq = new FileWriter("C:\\projeto\\respostas.txt");
		PrintWriter gravarArq = new PrintWriter(arq);
		
		gravarArq.printf(ped.toString());
	}
	
	public void DownloadArquivo(Long id) {
//		Pedido ped = Pedido.findById(id);
//		
//		final int ARBITARY_SIZE = 1048;
//
//					try (InputStream in = new FileInputStream("C:\\projeto\\respostas.txt");
//					OutputStream out = resp.getOutputStream()) {
//
//				byte[] buffer = new byte[ARBITARY_SIZE];
//
//				int numBytesRead;
//				while ((numBytesRead = in.read(buffer)) > 0) {
//					out.write(buffer, 0, numBytesRead);
//				}
//			}

		}
}
