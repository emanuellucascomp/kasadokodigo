package br.com.zup.autores

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/autores")
class CadastraAutorController(val autorRepository: AutorRepository) {

    @Post
    @Transactional
    fun cadastra(@Body @Valid request: NovoAutorRequest): HttpResponse<Any> {
        val autor = request.paraAutor()
        autorRepository.save(autor)

        val uri = UriBuilder.of("/autores/{id}")
            .expand(mutableMapOf(Pair("id", autor.id)))

        return HttpResponse.created(uri)
    }

    @Get
    @Transactional
    fun lista(@QueryValue(defaultValue = "") email: String): HttpResponse<Any> {
        if(email.isBlank()){
            val autores = autorRepository.findAll()
            val resposta = autores.map { autor -> DetalhesDoAutorResponse(autor) }
            return HttpResponse.ok(resposta)
        }
        val possivelAutor = autorRepository.findByEmail(email)
        if(possivelAutor.isEmpty){
            return HttpResponse.notFound()
        }
        val autor = possivelAutor.get()
        return HttpResponse.ok(DetalhesDoAutorResponse(autor))
    }

    @Put("/{id}")
    @Transactional
    fun atualiza(@PathVariable id: Long, descricao: String): HttpResponse<Any>{
        val possivelAutor = autorRepository.findById(id)
        if(possivelAutor.isEmpty){
            return HttpResponse.notFound()
        }
        val autor = possivelAutor.get()
        autor.descricao = descricao
        return HttpResponse.ok(DetalhesDoAutorResponse(autor))
    }

    @Delete("/{id}")
    @Transactional
    fun deleta(@PathVariable id: Long): HttpResponse<Any>{
        val possivelAutor = autorRepository.findById(id)
        if(possivelAutor.isEmpty){
            return HttpResponse.notFound()
        }
        val autor = possivelAutor.get()
        autorRepository.delete(autor)

        return HttpResponse.ok()
    }
}