package br.com.zup.autores

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest(
    transactional = false,
    transactionMode = TransactionMode.SINGLE_TRANSACTION
)
internal class CadastraAutorControllerTest{

    @field:Inject
    lateinit var enderecoClient: EnderecoClient
    @field:Inject
    lateinit var autorRepository: AutorRepository
    @field:Inject
    @field:Client("/")
    lateinit var  client: HttpClient
    lateinit var autor: Autor

    @BeforeEach
    internal fun setUp() {
        val enderecoResponse = EnderecoResponse(
            "60832410",
            "Rua José Rodrigues",
            "",
            "Lagoa Redonda",
            "Fortaleza",
            "CE",
            "2304400",
            "",
            "85",
            "1389"
        )
        val endereco = Endereco(enderecoResponse, "271")
        autor = Autor(
            "Emanuel Lucas",
            "emanuelecomp@gmail.com",
            "Um desenvolvedor",
            "ABC1234",
            endereco
        )
        autorRepository.save(autor)
    }

    @AfterEach
    internal fun tearDown() {
        autorRepository.deleteAll()
    }

    @Test
    internal fun `deve retornar os detalhes de um autor`() {

        val response = client.toBlocking()
                            .exchange("/autores?email=${autor.email}", DetalhesDoAutorResponse::class.java)
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(autor.nome, response.body()!!.nome)
    }

    @Test
    internal fun `deve cadastrar novo autor`() {
        val novoAutorRequest = NovoAutorRequest(
            "Rafael Ponte",
            "rafael.ponte@zup.com.br",
            "Marajá dos legados",
            "60832410",
            "271",
            "ABC1234"
        )

        val viaCepResponse = EnderecoResponse(
            "60832410",
            "Rua José Rodrigues",
            "",
            "Lagoa Redonda",
            "Fortaleza",
            "CE",
            "2304400",
            "",
            "85",
            "1389"
        )
        Mockito.`when`(enderecoClient.consulta(novoAutorRequest.cep)).thenReturn(HttpResponse.ok(viaCepResponse))

        val request = HttpRequest.POST("/autores", novoAutorRequest)
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.matches("/autores/\\d".toRegex()))
    }

    @MockBean(EnderecoClient::class)
    fun enderecoMock(): EnderecoClient{
        return Mockito.mock(EnderecoClient::class.java)
    }
}