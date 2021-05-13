package br.com.zup.autores

import javax.persistence.Embeddable

@Embeddable
class Endereco(
    enderecoResponse: EnderecoResponse,
    val numero: String
) {
    val cep: String = enderecoResponse.cep
    val logradouro: String = enderecoResponse.logradouro
    val complemento: String = enderecoResponse.complemento
    val bairro: String = enderecoResponse.bairro
    val localidade: String = enderecoResponse.localidade
    val uf: String = enderecoResponse.uf
    val ibge: String = enderecoResponse.ibge
    val gia: String = enderecoResponse.gia
    val ddd: String = enderecoResponse.ddd
    val siafi: String = enderecoResponse.siafi

}
