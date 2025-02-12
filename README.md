EASTER_EGG_URLS

# Java HTMLAnalyzer

Este programa em java é capaz de a partir de uma URL, obter o trecho de texto contido no nível mais profundo da estrutura HTML de seu conteúdo. A ideia é implementar uma solução seguindo um autômato de pilha, que retorne esse nível mais profundo.

Exemplo: 

```html
<html>
   <head>
       <title>
           Este é o título.
       </title>
   </head>
   <body>
       Este é o corpo.
   </body>
</html>
```
Na estrutura HTML acima, o trecho desejado como retorno é "`Este é o título.`" (sem as aspas), porque está em 3 níveis de profundidade (`html > head > title`), enquanto o trecho "`Este é o corpo.`" está em 2 níveis (`html > body`). Se dois ou mais trechos estiverem no nível máximo de profundidade do documento, o primeiro deles deve ser retornado.


### Requisitos

- [Java 23](https://www.oracle.com/java/technologies/downloads/)

### Compilando

Para compilar o programa basta dentro da pasta contendo os arquivos executar o comando:

``` shell
javac HtmlAnalyzer.java
```

### Utilização

Após compilar o programa, para utilizar basta executar o comando:

``` shell
java HtmlAnalyzer <url>
```

### Easter Egg

Todas as urls entre 1 e 6 foram testadas para validar o código. A url de número 7 também foi testada e foi tido apenas um erro de conexão
