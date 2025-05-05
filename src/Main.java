import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String cep;

            do {
                System.out.print("Digite o CEP: ");
                String inputCep = scanner.nextLine();
                cep = inputCep.replaceAll("\\D", "");

                if (cep.length() != 8) {
                    System.out.println("CEP inválido. Deve conter 8 dígitos.");
                }
            } while (cep.length() != 8);

            System.out.println("Buscando...");

            String url = "https://viacep.com.br/ws/" + cep + "/json/";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Endereco endereco = mapper.readValue(response.body(), Endereco.class);

                if (endereco.cep() == null) {
                    System.out.println("CEP não encontrado.");
                } else {
                    System.out.println("Endereço encontrado:");
                    System.out.println("CEP: " + endereco.cep());
                    System.out.println("Logradouro: " + endereco.logradouro());
                    System.out.println("Bairro: " + endereco.bairro());
                    System.out.println("Cidade: " + endereco.localidade());
                    System.out.println("UF: " + endereco.uf());
                }

            } else {
                System.out.println("Erro ao consultar o CEP. HTTP: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Endereco(
            String cep,
            String logradouro,
            String complemento,
            String bairro,
            String localidade,
            String uf,
            String ibge,
            String gia,
            String ddd,
            String siafi
    ) {}
}
