package modulos.produto;

import dataFactory.ProdutoDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pojo.UsuarioPojo;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API Rest do módulo de Produto")
public class ProdutoTest {

    private String token;

    @BeforeEach
    public void baseUriEToken() {
        // Configurando os dados da API Rest da Lojinha
        baseURI = "http://165.227.93.41";
        // port = 8080;
        basePath = "/lojinha";

        UsuarioPojo usuario = new UsuarioPojo();
        usuario.setUsuarioLogin("admin");
        usuario.setUsuarioSenha("admin");

        // Obter o token do usuário admin
        this.token = given()
                .contentType(ContentType.JSON)
                .body(usuario)
                .when()
                .post("/v2/login")
                .then()
                .extract()
                .path("data.token");
    }

    @Test
    @DisplayName("Validar o limite zerado do valor do Produto")
    public void testValidarLimiteZeradoValorProduto() {

        // Tentar inserir um produto com valor 0,00 e validar que a msg de erro fora apresentada e o
        // status code retornado foi 422
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorIgualA(0.00))
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }

    @Test
    @DisplayName("Validar o limite maior que sete mil do valor do Produto")
    public void testValidarLimiteMaiorSeteMilValorProduto() {

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorIgualA(7000.01))
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }

}
