# CatalogoApp

## Descrição do Projeto
O CatalogoApp é uma aplicação robusta de gerenciamento de produtos e categorias, desenvolvida como material prático para as aulas na disciplina de "Desenvolvimento para Servidores II" no curso de Sistemas para Internet. O projeto foi construído com o objetivo principal de proporcionar aos alunos o aprendizado sobre o desenvolvimento de sistemas full-stack, focando em boas práticas de arquitetura, segurança e persistência de dados.

Através deste projeto, exploramos a transição de armazenamentos temporários em memória para soluções profissionais utilizando bancos de dados relacionais e controle de acesso baseado em perfis.

## Tecnologias Utilizadas
*   Java 21 e Spring Boot 3
*   Spring Data JPA para persistência de dados
*   PostgreSQL como banco de dados relacional
*   Spring Security para autenticação e autorização
*   Thymeleaf para renderização de telas dinâmicas
*   Bootstrap 5 para interface responsiva
*   BCrypt para criptografia de senhas

## Principais Funcionalidades
*   Gerenciamento completo (CRUD) de Produtos e Categorias.
*   Sistema de busca e filtros dinâmicos por nome e categoria.
*   Controle de acesso diferenciado:
    *   Administrador (ADMIN): Acesso total para gerenciar produtos, categorias e usuários.
    *   Usuário (USER): Acesso de consulta aos produtos cadastrados.
*   Persistência de usuários no banco de dados com senhas protegidas por hash.
*   Inicialização automática de dados (Data Seeder) para facilitar o primeiro acesso ao ambiente de desenvolvimento.

## Estrutura de Pastas e Padrões
O projeto segue o padrão de responsabilidades separadas para garantir a manutenibilidade:
*   Models: Representação das entidades do banco de dados.
*   Repositories: Interfaces para comunicação direta com o PostgreSQL.
*   Services: Camada de lógica de negócio e autenticação customizada.
*   Controllers: Gerenciamento de rotas e fluxo entre a View e a Model.
*   Config: Classes de configuração de segurança e inicialização do sistema.

## Fluxo de Dados: Do Formulário HTML ao Banco de Dados

Esta seção descreve o caminho percorrido pelos dados desde o momento em que o usuário preenche um formulário até a persistência no PostgreSQL, usando o cadastro de produto como exemplo.

### 1. Formulário HTML (View — Thymeleaf)

O formulário em `cadastro-produto.html` usa os atributos `th:object` e `th:field` para se vincular ao objeto `ProdutoModel` enviado pelo Controller:

```html
<form th:action="@{/produtos/salvar}" th:object="${produto}" method="post">
    <input type="text"   th:field="*{nome}">
    <input type="number" th:field="*{valor}">
    <input type="number" th:field="*{quantidade}">
    <select             th:field="*{categoria}">...</select>
</form>
```

Ao clicar em "Salvar", o browser serializa os campos em uma requisição HTTP POST com os dados no corpo: `nome=Mouse+Gamer&valor=150.00&quantidade=10&categoria=1`.

### 2. Controller (Recebe e valida o formulário)

O `ProdutoController` recebe a requisição no método `salvar()`. A anotação `@ModelAttribute` instrui o Spring a mapear automaticamente cada campo do POST para o atributo correspondente do `ProdutoModel`. A anotação `@Valid` dispara as validações declaradas no Model (`@NotBlank`, `@NotNull`, `@Min`, `@Size`). Se houver erros, o `BindingResult` os captura e o formulário é reexibido com as mensagens de erro — sem chegar ao banco.

```java
@PostMapping("/salvar")
public String salvar(@Valid @ModelAttribute("produto") ProdutoModel produto,
                     BindingResult result, RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) return "cadastro-produto"; // Reexibe com erros
    service.salvar(produto);
    // ...
    return "redirect:/produtos";
}
```

### 3. Service (Aplica as regras de negócio)

O `ProdutoService` recebe o objeto já populado e aplica as regras de negócio antes de persistir. Esta é a camada que protege a integridade dos dados independentemente de como a requisição chegou (formulário, API, etc.):

```java
public void salvar(ProdutoModel produto) {
    if (produto.getIdProduto() == 0 && repository.existsByNome(produto.getNome())) {
        throw new RuntimeException("Já existe um produto com este nome.");
    }
    if (produto.getQuantidade() != null && produto.getQuantidade() < 0) {
        throw new IllegalArgumentException("A quantidade não pode ser negativa.");
    }
    produto.setDataCadastro(LocalDateTime.now());
    repository.save(produto);
}
```

### 4. Repository e Hibernate (Gera e executa o SQL)

O `ProdutoRepository` recebe o objeto e o Hibernate gera automaticamente o SQL correspondente, mapeando cada atributo do `ProdutoModel` para a coluna definida em `TB_PRODUTO`:

```sql
INSERT INTO tb_produto (id_categoria_fk, data_atualizacao, nome, quantidade, valor)
VALUES (1, '2026-05-21 22:12:26', 'Mouse Gamer', 10, 150.00)
```

### 5. Resumo do fluxo completo

```
[HTML form] --> POST /produtos/salvar
    --> [Controller] @ModelAttribute monta o ProdutoModel
        --> [Controller] @Valid dispara validações do Bean Validation
            --> [Service] aplica regras de negócio
                --> [Repository] Hibernate gera e executa o SQL
                    --> [PostgreSQL] dado persistido em TB_PRODUTO
                        --> [Controller] redirect:/produtos com mensagem de sucesso
                            --> [HTML] alerta verde exibido ao usuário
```

## Como Executar
1. Certifique-se de ter o Java 21 e o PostgreSQL instalados.
2. Clone o repositório.
3. Ajuste as credenciais do banco de dados no arquivo application.properties.
4. Execute a aplicação via IDE (como IntelliJ) ou terminal usando `./mvnw spring-boot:run`.
5. Ao iniciar pela primeira vez, o sistema criará automaticamente um usuário administrador inicial (admin / admin123).

## Créditos
Projeto desenvolvido em contexto acadêmico na FATEC Jales, sob a orientação do Professor James Campos, visando a formação em desenvolvimento multiplataforma e engenharia de software.
