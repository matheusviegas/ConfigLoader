package br.com.mvsouza.configloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Matheus Souza 
 * https://github.com/matheusviegas 
 * https://mvsouza.com.br
 *
 * Biblioteca para carregar um arquivo de configurações e setar seus valores em
 * uma instância de uma classe de configuração através de Reflection.
 */
public class ConfigLoader {

    public enum Delimitador {
        IGUAL("="), PONTO_VIRGULA(";"), VIRGULA(","), DOIS_PONTOS(":");

        private final String simbolo;

        private Delimitador(String simb) {
            this.simbolo = simb;
        }

        public String getSimbolo() {
            return simbolo;
        }

    }

    private Delimitador delimitador;
    private String caminhoArquivo;
    private Object instanciaClasseConfiguracao;

    /**
     * Construtor padrão. Inicializa os atributos padrão da classe.
     */
    public ConfigLoader() {
        delimitador = Delimitador.IGUAL;
        caminhoArquivo = ".env";
        instanciaClasseConfiguracao = null;
    }

    /**
     *
     * @param instancia Instância de uma classe de configurações a ser
     * manipulada com reflection. (Classe SINGLETON).
     */
    public ConfigLoader(Object instancia) {
        this();
        this.instanciaClasseConfiguracao = instancia;
    }

    /**
     *
     * @param delimitador Delimitador do valor da propriedade no arquivo. Padrão
     * [=] Exemplo: (KEY=VALUE).
     * @return Retorna a própria instância modificada.
     */
    public ConfigLoader setDelimitador(Delimitador delimitador) {
        this.delimitador = delimitador;
        return this;
    }

    /**
     *
     * @param caminhoArquivo Define um caminho personalizado para onde se
     * encontrará o arquivo de configurações.
     * @return Retorna a própria instância modificada.
     */
    public ConfigLoader setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        return this;
    }

    /**
     *
     * @param instancia Instância de uma classe de configurações a ser
     * manipulada com reflection. (Classe SINGLETON).
     * @return Retorna a própria instância modificada.
     */
    public ConfigLoader setInstanciaClasseConfiguracao(Object instancia) {
        this.instanciaClasseConfiguracao = instancia;
        return this;
    }

    /**
     * Carrega as configurações do arquivo e coloca o valor da propriedade na
     * instância da classe Config usando Reflection.
     */
    public void carregarConfiguracoes() {
        if (this.instanciaClasseConfiguracao == null) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "É necessário informar a instância da classe de configurações.");
            return;
        }

        try {
            Map<String, String> configMap = new HashMap<>();

            try (Stream<String> linhasArquivo = Files.lines(Paths.get(caminhoArquivo))) {
                linhasArquivo.filter(linha -> linha.contains(delimitador.getSimbolo())).forEach(
                        linha -> {
                            String[] partes = linha.split(this.delimitador.getSimbolo());
                            if (partes.length >= 0) {
                                configMap.putIfAbsent(partes[0], partes.length > 1 ? partes[1] : null);
                            }
                        }
                );
            } catch (ArrayIndexOutOfBoundsException e) {
                Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "Arquivo de configuração mal formatado.\nGaranta que todas as chaves tenham um valor correspondente no seu arquivo e que estejam separados por (" + this.delimitador.getSimbolo() + ").", e);
            }

            configMap.entrySet().forEach((config) -> {
                setaValorConfigInstanciaReflection(instanciaClasseConfiguracao, config.getKey(), getInstanciaValor(config.getValue()));
            });

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "Não foi possível encontraro arquivo de configurações.", ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "Erro ao ler arquivo de configurações.", ex);
        }
    }

    /**
     * Seta o valor da configuração no atributo com a key correspondente na
     * instância da classe de configurações informada.
     *
     * @param objeto Instância do classe de configuração que será manipulado
     * (singleton).
     * @param nomeAtributo Nome do atributo a ser modificado.
     * @param valorAtributo Valor a ser atribuído ao atributo.
     * @return
     */
    private static boolean setaValorConfigInstanciaReflection(Object objeto, String nomeAtributo, Object valorAtributo) {
        Class<?> classe = objeto.getClass();
        while (classe != null) {
            try {
                Field atributo = classe.getDeclaredField(nomeAtributo);
                atributo.setAccessible(true);
                atributo.set(objeto, valorAtributo);
                return true;
            } catch (NoSuchFieldException e) {
                classe = classe.getSuperclass();
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                throw new IllegalStateException(e);
            }
        }

        return false;
    }

    /**
     *
     * @param valor valor da propriedade.
     * @return Instância correta do valor.
     */
    private static Object getInstanciaValor(String valor) {
        if (valor == null) {
            return valor;
        }

        valor = valor.trim();

        if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(valor);
        }

        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {

        }

        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException ex) {

        }

        return valor;
    }

}
