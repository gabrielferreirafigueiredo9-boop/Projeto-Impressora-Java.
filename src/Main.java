
import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Scanner;
import java.nio.file.*;

public class Main {

    // Interface que conecta o Java à DLL da impressora
    public interface ImpressoraDLL extends Library {

        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                "E1_Impressora01", ImpressoraDLL.class);

        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);

        int FechaConexaoImpressora();

        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);

        int Corte(int avanco);

        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);

        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);

        int AvancaPapel(int linhas);

        int StatusImpressora(int param);

        int AbreGavetaElgin(int pino, int ti, int tf);

        int AbreGaveta(int pino, int ti, int tf);

        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);

        int ModoPagina();

        int LimpaBufferModoPagina();

        int ImprimeModoPagina();

        int ModoPadrao();

        int PosicaoImpressaoHorizontal(int posicao);

        int PosicaoImpressaoVertical(int posicao);

        int ImprimeXMLSAT(String dados, int param);

        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    // Dados do login
    private static final String USUARIO = "admin";
    private static final String SENHA = "1234";

    // Assinatura do QR Code para o cancelamento SAT
    private static final String ASS_QRCODE =
            "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

    // Variáveis da conexão
    private static boolean conexaoAberta = false;
    private static int tipo;
    private static String modelo;
    private static String conexao;
    private static int parametro;

    private static final Scanner scanner = new Scanner(System.in);

    // Captura um texto digitado pelo usuário
    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    // Lê um número inteiro sem deixar o programa parar se forem digitadas letras
    private static int lerInteiro(String mensagem) {

        while (true) {

            String texto = capturarEntrada(mensagem).trim();

            boolean valido = texto.length() > 0 && texto.length() <= 9;

            for (int i = 0; i < texto.length(); i++) {

                char caractere = texto.charAt(i);

                if (caractere < '0' || caractere > '9') {
                    valido = false;
                }
            }

            if (valido) {

                int numero = 0;

                for (int i = 0; i < texto.length(); i++) {

                    int digito = texto.charAt(i) - '0';

                    numero = numero * 10 + digito;
                }

                return numero;

            } else {
                System.out.println("Valor inválido. Digite apenas números.");
            }
        }
    }

    // Lê o arquivo e devolve seu conteúdo.
    // Se o arquivo não existir, devolve null.
    private static String lerArquivo(String nome) throws java.io.IOException {

        Path caminho = Paths.get("arquivos", nome);

        if (!Files.exists(caminho)) {
            return null;
        }

        return new String(Files.readAllBytes(caminho), "UTF-8");
    }

    // Confere se a conexão está aberta
    private static boolean conexaoOk() {

        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return false;
        }

        return true;
    }

    // Confere o retorno do comando de corte
    private static void cortarPapel() {

        int retorno = ImpressoraDLL.INSTANCE.Corte(5);

        if (retorno == 0) {
            System.out.println("Papel cortado.");
        } else {
            System.out.println("Erro ao cortar papel. Código: " + retorno);
        }
    }

    // LOGIN
    public static boolean fazerLogin() {

        for (int tentativa = 1; tentativa <= 3; tentativa++) {

            String usuario = capturarEntrada("Usuário: ");
            String senha = capturarEntrada("Senha: ");

            if (usuario.equals(USUARIO) && senha.equals(SENHA)) {

                System.out.println("Login realizado com sucesso!");
                return true;

            } else {

                int restantes = 3 - tentativa;

                if (restantes > 0) {
                    System.out.println("Usuário ou senha incorretos.");
                    System.out.println("Tentativas restantes: " + restantes);
                }
            }
        }

        System.out.println("Número máximo de tentativas excedido.");
        return false;
    }

    // CONFIGURAÇÃO DA CONEXÃO
    public static void configurarConexao() {

        tipo = lerInteiro("Tipo de conexão (1=USB, 2=Serial, 3=Rede, 4=Bluetooth): ");

        while (tipo < 1 || tipo > 4) {
            System.out.println("Tipo de conexão inválido.");
            tipo = lerInteiro("Digite 1, 2, 3 ou 4: ");
        }

        modelo = capturarEntrada("Modelo da impressora (i8 ou i9): ");

        while (!modelo.equals("i8") && !modelo.equals("i9")) {
            System.out.println("Modelo inválido. Digite i8 ou i9.");
            modelo = capturarEntrada("Modelo da impressora: ");
        }

        if (tipo == 1) {

            conexao = "USB";
            parametro = 0;

        } else {

            conexao = capturarEntrada("Digite a porta ou endereço da conexão: ");
            parametro = lerInteiro("Digite o parâmetro da conexão: ");
        }

        System.out.println("Configuração salva com sucesso.");
    }

    // ABRIR CONEXÃO
    public static void abrirConexao() {

        if (!conexaoAberta) {

            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(
                    tipo, modelo, conexao, parametro);

            if (retorno == 0) {

                conexaoAberta = true;
                System.out.println("Conexão aberta com sucesso.");

            } else {

                System.out.println("Erro ao abrir conexão. Código: " + retorno);
            }

        } else {

            System.out.println("Conexão já está aberta.");
        }
    }

    // FECHAR CONEXÃO
    public static void fecharConexao() {

        if (!conexaoAberta) {
            System.out.println("Conexão já está fechada.");
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();

        if (retorno == 0) {

            conexaoAberta = false;
            System.out.println("Conexão fechada com sucesso.");

        } else {

            System.out.println("Erro ao fechar conexão. Código: " + retorno);
        }
    }

    // MENU
    public static void exibirMenu() {

        System.out.println("\n*************************************************");
        System.out.println("*************** MENU IMPRESSORA ****************");
        System.out.println("*************************************************\n");

        System.out.println("1  - Configurar Conexao");
        System.out.println("2  - Abrir Conexao");
        System.out.println("3  - Fechar Conexao");
        System.out.println("4  - Imprimir Texto");
        System.out.println("5  - Imprimir QR Code");
        System.out.println("6  - Imprimir Codigo de Barras");
        System.out.println("7  - Imprimir XML SAT");
        System.out.println("8  - Imprimir XML Cancelamento SAT");
        System.out.println("9  - Abrir Gaveta");
        System.out.println("10 - Sinal Sonoro");
        System.out.println("11 - Avancar Papel");
        System.out.println("0  - Sair");
    }

    // IMPRIMIR TEXTO
    public static void imprimirTexto() {

        if (!conexaoOk()) {
            return;
        }

        String texto = capturarEntrada("Texto a imprimir: ");

        int copias = lerInteiro("Quantas cópias? ");

        while (copias < 1) {
            System.out.println("Digite pelo menos uma cópia.");
            copias = lerInteiro("Quantas cópias? ");
        }

        for (int i = 1; i <= copias; i++) {

            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto, 1, 4, 0);

            if (retorno >= 0) {
                System.out.println("Cópia " + i + " impressa!");
            } else {
                System.out.println("Erro ao imprimir. Código: " + retorno);
            }
        }

        cortarPapel();
    }

    // IMPRIMIR QR CODE
    public static void imprimirQRCode() {

        if (!conexaoOk()) {
            return;
        }

        String dados = capturarEntrada("Conteúdo do QR Code: ");

        int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 6, 4);

        if (retorno == 0) {

            System.out.println("QR Code impresso!");
            cortarPapel();

        } else {

            System.out.println("Erro ao imprimir QR Code. Código: " + retorno);
        }
    }

    // IMPRIMIR CÓDIGO DE BARRAS
    public static void imprimirCodigoBarras() {

        if (!conexaoOk()) {
            return;
        }

        String dados = capturarEntrada("Digite os números do código de barras: ");

        int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(
                8, "{A" + dados, 100, 2, 3);

        if (retorno == 0) {

            System.out.println("Código de barras impresso!");

        } else {

            System.out.println("Erro ao imprimir código de barras. Código: " + retorno);
        }
    }

    // IMPRIMIR XML SAT
    public static void imprimirXMLSAT() throws java.io.IOException {

        if (!conexaoOk()) {
            return;
        }

        String nome = capturarEntrada("Nome do arquivo XML (ex.: XMLSAT.xml): ");

        String xml = lerArquivo(nome);

        if (xml == null) {
            System.out.println("Não foi possível encontrar o arquivo: " + nome);
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0);

        if (retorno == 0) {

            System.out.println("XML SAT impresso com sucesso!");

        } else {

            System.out.println("Erro ao imprimir XML SAT. Código: " + retorno);
        }
    }

    // IMPRIMIR XML DE CANCELAMENTO SAT
    public static void imprimirXMLCancelamentoSAT() throws java.io.IOException {

        if (!conexaoOk()) {
            return;
        }

        String nome = capturarEntrada("Nome do arquivo XML (ex.: CANC_SAT.xml): ");

        String xml = lerArquivo(nome);

        if (xml == null) {
            System.out.println("Não foi possível encontrar o arquivo: " + nome);
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(
                xml, ASS_QRCODE, 0);

        if (retorno == 0) {

            System.out.println("Cancelamento SAT impresso com sucesso!");

        } else {

            System.out.println("Erro ao imprimir cancelamento SAT. Código: " + retorno);
        }
    }

    // ABRIR GAVETA
    public static void abrirGaveta() {

        if (!conexaoOk()) {
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);

        if (retorno == 0) {

            System.out.println("Gaveta aberta com sucesso!");

        } else {

            System.out.println("Erro ao abrir gaveta. Código: " + retorno);
        }
    }

    // SINAL SONORO
    public static void sinalSonoro() {

        if (!conexaoOk()) {
            return;
        }

        int quantidade = lerInteiro("Quantos bips? ");

        while (quantidade < 1 || quantidade > 63) {
            System.out.println("Digite uma quantidade entre 1 e 63.");
            quantidade = lerInteiro("Quantos bips? ");
        }

        int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(quantidade, 50, 5);

        if (retorno == 0) {

            System.out.println("Sinal sonoro executado!");

        } else {

            System.out.println("Erro no sinal sonoro. Código: " + retorno);
        }
    }

    // AVANÇAR PAPEL
    public static void avancarPapel() {

        if (!conexaoOk()) {
            return;
        }

        int linhas = lerInteiro("Quantas linhas avançar? ");

        while (linhas < 1) {
            System.out.println("Digite um número maior que zero.");
            linhas = lerInteiro("Quantas linhas avançar? ");
        }

        int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);

        if (retorno == 0) {

            System.out.println("Papel avançado com sucesso!");

        } else {

            System.out.println("Erro ao avançar papel. Código: " + retorno);
        }
    }

    // PROGRAMA PRINCIPAL
    public static void main(String[] args) throws java.io.IOException {

        // A DLL está na pasta main-impressora, ao lado da pasta untitled.
        // O Working directory do IntelliJ deve ser a pasta untitled.
        System.setProperty(
                "jna.library.path",
                Paths.get("..", "main-impressora").toAbsolutePath().toString()
        );

        if (!fazerLogin()) {
            scanner.close();
            return;
        }

        int opcao = -1;

        while (opcao != 0) {

            exibirMenu();

            opcao = lerInteiro("\nDigite a opção desejada: ");

            if (opcao == 1) {

                configurarConexao();

            } else if (opcao == 2) {

                abrirConexao();

            } else if (opcao == 3) {

                fecharConexao();

            } else if (opcao == 4) {

                imprimirTexto();

            } else if (opcao == 5) {

                imprimirQRCode();

            } else if (opcao == 6) {

                imprimirCodigoBarras();

            } else if (opcao == 7) {

                imprimirXMLSAT();

            } else if (opcao == 8) {

                imprimirXMLCancelamentoSAT();

            } else if (opcao == 9) {

                abrirGaveta();

            } else if (opcao == 10) {

                sinalSonoro();

            } else if (opcao == 11) {

                avancarPapel();

            } else if (opcao == 0) {

                fecharConexao();
                System.out.println("Programa encerrado.");

            } else {

                System.out.println("Opção inválida.");
            }
        }

    }
}
