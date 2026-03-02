# help-pet-back

## Como rodar o projeto
1. Clone este repositório.
2. Entre na pasta utilizando Intellij para que a dependência `` lombok `` funcione corretamente.
3. Verifique que o banco de dados está configurando corretamente em **application properties**.
4. Rode o arquivo `` HelpPetApplication.java ``.

## Como começar a contribuir
> Preencha as tópicos vazios desse readme de acordo com a função do seu grupo e faça alterações na seção estrutura de pastas conforme sejam feitas alterações.

## Estrutura das pastas
  ```
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── helppet.com/
│   │   │       ├── controllers/
│   │   │       │   └── .gitkeep
│   │   │       ├── dtos/
│   │   │       │   └── .gitkeep
│   │   │       ├── entities/
│   │   │       │   └── .gitkeep
│   │   │       ├── repositories/
│   │   │       │   └── .gitkeep
│   │   │       └── services/
│   │   │           ├── .gitkeep
│   │   │           └── HelpPetApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── helppet.com/
│               └── HelpPetApplicationTests.java
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
  ```

## Convenção de nomenclatura
> Padrão de nomeação que deve ser seguido em todas as etapas de desenvolvimento.


### Linguagem
- A ser decidido

### Arquivos e pastas
- kebab-case.
- Com exceção dos arquivos na pasta .github que devem ser todos em maiúsculo todas as pastas e arquivos devem permanecer neste padrão.
- Exemplo:

  ```
    ├── .github/
    │   ├── CODEOWNERS 
    │   └── PULL_REQUEST_TEMPLATE.md 
    └──── src/ 
           ├──.mvn
           └──src
                └── main 
  ```
  
### Variáveis e funções
- camelCase.
- Nomes de variáveis e funções no front e back end devem seguir este padrão.
- Exemplos:

  ```java
  private String userName;

  public String getName() {
    return userName;
  }
  ```

### Endpoints
- kebab-case.
- Endpoints não devem iniciar nem finalizar com " / " e nomes compostos devem ser separados seguindo este padrão.
- Exemplo:

  ```java
    @RestController
    @RequestMapping("user")
    public class UserController {

        @PostMapping("login-admin")
	    public ResponseEntity<?> login(@RequestBody UserRequestDTO user) {
		
		    return ResponseEntity.ok(userService.login(user));
    	} 
    }
  ```


## Modelagem do banco de dados

### (Diagrama Entidade-Relacionamento)
<!-- Coloque o relacionamento aqui do jeito que preferir imagem, texto, etc... -->

### Relacionamento MER

### Relacionamento DER
