import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.text.DecimalFormat;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConversorMoedas {

    private static final String API_KEY = "2c84071575cee5fc9a76d147";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final Scanner scanner = new Scanner(System.in);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        exibirMenu();
    }

    private  static void exibirMenu(){
        System.out.println("""
    ****************************************************
    Seja bem-vindo ao Conversor de Moeda! :D
    
    1) Dólar =>> Peso Argentino
    2) Peso Argentino =>> Dólar
    3) Dólar =>> Real Brasileiro
    4) Real Brasileiro =>> Dólar
    5) Dólar =>> Peso Colombiano
    6) Peso Colombiano =>> Dólar
    7) Sair
    Escolha uma opção válida:
    ****************************************************
    """);

     int opcao = scanner.nextInt();

     if(opcao == 7){
         System.out.println("Saindo do programa :(...");
         return;
     }

     if(opcao < 1 || opcao > 6){
         System.out.println("Opção inválida. Tente novamente");
         exibirMenu();
         return;
     }

     System.out.println("Digite o valor que deseja converter:");
     double valor = scanner.nextDouble();

     try {
         switch (opcao) {
             case 1:
                 converterMoeda("USD", "ARS", valor);
                 break;
             case 2:
                 converterMoeda("ARS", "USD", valor);
                 break;
             case 3:
                 converterMoeda("USD", "BRL", valor);
                 break;
             case 4:
                 converterMoeda("BRL", "USD", valor);
                 break;
             case 5:
                 converterMoeda("USD", "COP", valor);
                 break;
             case 6:
                 converterMoeda("COP", "USD", valor);
                 break;
         }
     } catch (IOException | InterruptedException e){
         System.out.println("Erro ao aceesar API de câmbio: " + e.getMessage());
     }

     exibirMenu(); //após conversão
    }

    private static void converterMoeda(String moedaOriginal, String moedaConversao, double valor) throws IOException, InterruptedException
    {
        String url = BASE_URL + moedaOriginal;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200){
            throw new IOException("Erro na requisição: " + response.statusCode());
        }

        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        JsonObject rates = jsonResponse.getAsJsonObject("conversion_rates");
        double taxa = rates.get(moedaConversao).getAsDouble();
        double valorConvertido = valor * taxa;

        System.out.println("O valor " + df.format(valor) + " [" + moedaOriginal + "]" + " corresponde ao valor final =>>> " + df.format(valorConvertido) + " [" + moedaConversao + "]" );
    }
}