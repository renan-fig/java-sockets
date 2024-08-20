package control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ServidorEcho {

	private ServerSocket sckServidor;

	public ServidorEcho() throws IOException {
		this.sckServidor = new ServerSocket(4000);

		System.out.println("Servidor iniciado na porta 4000");

		while (true) {
			Socket sckEcho = null;
			InputStream canalEntrada;
			OutputStream canalSaida;
			BufferedReader entrada;
			PrintWriter saida;

			try {
				sckEcho = this.sckServidor.accept();

				canalEntrada = sckEcho.getInputStream();
				canalSaida = sckEcho.getOutputStream();
				entrada = new BufferedReader(new InputStreamReader(canalEntrada));
				saida = new PrintWriter(canalSaida, true);

				String linhaPedido = entrada.readLine();

				if (linhaPedido != null && !linhaPedido.trim().isEmpty()) {
					String[] partes = linhaPedido.split(";");

					if (partes.length < 2 || partes[0].trim().isEmpty() || partes[1].trim().isEmpty()) {
						saida.println("Erro: Entrada inválida. Forneça o fuso-horário e a data/hora.");
					} else {
						String fusoHorario = partes[0].trim();
						String dataHoraCliente = partes[1].trim();

						try {
							// Converte a data/hora do cliente para o fuso-horário UTC
							LocalDateTime dataHora = LocalDateTime.parse(dataHoraCliente);
							ZonedDateTime dataHoraZoned = dataHora.atZone(ZoneId.of(fusoHorario));

							// Converte a data/hora para o fuso-horário desejado
							ZonedDateTime dataHoraConvertida = dataHoraZoned.withZoneSameInstant(ZoneId.of(fusoHorario));
							saida.println("Hora convertida: " + dataHoraConvertida);
						} catch (Exception e) {
							saida.println("Erro ao converter a data/hora: " + e.getMessage());
						}
					}
				} else {
					saida.println("Erro: Nenhuma entrada foi fornecida.");
				}
			} catch (IOException e) {
				System.out.println("Erro no Socket: " + e.getMessage());
			}
			sckEcho.close();
		}
	}
}
