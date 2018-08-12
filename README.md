# Config Loader
Biblioteca para carregar um arquivo de configurações e setar seus valores em  uma instância de uma classe de configuração através de Reflection.

## Como Utilizar

Primeiro crie seu arquivo de configuração. Segue abaixo um exemplo do arquivo que criei como exemplo e deixei o nome **.env** que é padrão desta biblioteca:

```java
ATRIBUTO_BOOLEAN=true
ATRIBUTO_STRING=Sou uma string
ATRIBUTO_INTEGER=12345
ATRIBUTO_DOUBLE=123.45
```

Note que usei o delimitador (=) para separar a chave do valor da propriedade. Este delimiter é o padrão da biblioteca.

Após criar o arquivo de configurações, crie uma classe que será responsável por armazenar suas configurações. 

**Essa classe precisa implementar singleton** e todas as variáveis de configuração que você colocou no arquivo devem estar presentes como atributos desta classe.

Segue abaixo um exemplo:

```java

public class Configuracoes {

    private static Configuracoes instancia;

    public static boolean ATRIBUTO_BOOLEAN;
    public static String ATRIBUTO_STRING;
    public static int ATRIBUTO_INTEGER;
    public static Double ATRIBUTO_DOUBLE;

    private Configuracoes() {

    }

    public static synchronized Configuracoes getInstancia() {
        if (instancia == null) {
            instancia = new Configuracoes();
        }

        return instancia;
    }

}

```

No exemplo acima, criei a classe config com os atributos estáticos e públicos, assim podendo serem acessados facilmente em todo o projeto apenas chamando **Config.ATRIBUTO_STRING** por exemplo.

Para utilizar é simples, basta instanciar a classe **ConfigLoader** e passar no construtor dela a instância da sua classe e chamar o método **carregarConfiguracoes()** que é responsável por iniciar a leitura do arquivo e atribuição dos valores do arquivo aos atributos da classe **Configuracoes**.

Exemplo:

```java
    new ConfigLoader(Configuracoes.getInstancia()).setCaminhoArquivo("/home/msouza/projeto/.env").carregarConfiguracoes();
```

Após isso, já será possível acessar as propriedades diretamente pela classe **Config** chamando **Config.NOME_ATRIBUTO**.

Sugestões e PR são aceitas.
