package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    @Autowired
    private MedicoRepository repositorio;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar um novo médico", description = "Este endpoint cadastra um novo médico com base nos dados fornecidos.")
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder){
        var medico = new Medico(dados);
        repositorio.save(medico);
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping
    @Operation(summary = "Listar médicos ativos", description = "Este endpoint retorna uma lista paginada de médicos ativos.")
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
        var page =  repositorio.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);

        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar informações do médico", description = "Este endpoint atualiza as informações de um médico com base nos dados fornecidos.")
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualiazadosMedico dados){
        var medico = repositorio.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir médico", description = "Este endpoint exclui um médico com base no ID fornecido.")
    public ResponseEntity excluir(@PathVariable Long id){
        var medico = repositorio.getReferenceById(id);
        medico.excluir();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Transactional
    @Operation(summary = "Detalhar médico", description = "Este endpoint retorna as informações de um médico com base no ID fornecido.")
    public ResponseEntity detalhar(@PathVariable Long id){
        var medico = repositorio.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }
}