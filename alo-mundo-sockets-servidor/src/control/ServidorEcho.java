package control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.*;

public class ServidorEcho {

	private ServerSocket sckServidor;

	public ServidorEcho() throws IOException {
		this.sckServidor = new ServerSocket(4000);

		System.out.println("Servidor iniciado na porta 4000");

		for (;;) {
			Socket sckEcho;
			InputStream canalEntrada;
			OutputStream canalSaida;
			BufferedReader entrada;
			PrintWriter saida;

			sckEcho = this.sckServidor.accept();
			canalEntrada = sckEcho.getInputStream();
			canalSaida = sckEcho.getOutputStream();
			entrada = new BufferedReader(new InputStreamReader(canalEntrada));
			saida = new PrintWriter(canalSaida, true);


			while (true) {
				String linhaPedido = entrada.readLine();

				if (linhaPedido != null && !linhaPedido.trim().isEmpty()){
					String[] partes = linhaPedido.split(";");

					// Valida entrada
					if (partes.length < 2 || partes[0].trim().isEmpty() || partes[1].trim().isEmpty()) {
						saida.println("Erro: Entrada inválida. Forneceça o fuso-horário e a data/hora.");
						break;
					} else {
						String fusoHorario = partes[0].trim();
						String dataHoraCliente = partes[1].trim();
						System.out.println("Hora passada: " + dataHoraCliente);

						if(isValidDate(dataHoraCliente)) {
							try {
								// Converte a data/hora do cliente para o fuso-horário UTC
								LocalDateTime dataHora = LocalDateTime.parse(dataHoraCliente);
								ZonedDateTime dataHoraZoned = dataHora.atZone(ZoneId.of(fusoHorario));
								System.out.printf("Horario retornado: " + dataHoraZoned);

								// Converte a data/hora para o fuso-horário desejado
								ZonedDateTime dataHoraConvertida = dataHoraZoned.withZoneSameInstant(ZoneId.of(fusoHorario));

								String mensagem = String.valueOf(dataHoraConvertida);

								saida.println("Hora convertida: " + mensagem);
							} catch (Exception e) {
								saida.println("Erro ao converter a data/hora: " + e.getMessage());
							}
						}else{
							saida.println("Erro: data inválida");
						}

				}
			}
			sckEcho.close();
        }
	}

	public static boolean isValidDate(String dateString) {
		try {
			LocalDateTime.parse(dateString);
			return true;
		} catch (DateTimeException e) {
			return false;
		}
	}

}
