package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.paciente.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pacientes")
@SecurityRequirement(name = "bearer-key")
public class PacienteController {

    @Autowired
    private PacienteRepository repositorio;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar um novo paciente", description = "Este endpoint permite cadastrar um novo paciente.")
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroPaciente dados, UriComponentsBuilder uriBuilder){
        var paciente = new Paciente(dados);
        repositorio.save(paciente);
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoPaciente(paciente));
    }

    @GetMapping
    @Operation(summary = "Listar pacientes ativos", description = "Este endpoint retorna uma lista paginada de pacientes ativos.")
    public ResponseEntity<Page<DadosListagemPaciente>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
        var page =  repositorio.findAllByAtivoTrue(paginacao).map(DadosListagemPaciente::new);

        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar informações do paciente", description = "Este endpoint atualiza as informações de um paciente com base nos dados fornecidos.")
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizadosPaciente dados){
        var paciente = repositorio.getReferenceById(dados.id());
        paciente.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir paciente", description = "Este endpoint exclui um paciente com base no ID fornecido.")
    public ResponseEntity excluir(@PathVariable Long id){
        var paciente = repositorio.getReferenceById(id);
        paciente.excluir();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Transactional
    @Operation(summary = "Detalhar paciente", description = "Este endpoint retorna detalhes de um paciente com base no ID fornecido.")
    public ResponseEntity detalhar(@PathVariable Long id){
        var paciente = repositorio.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
    }
}
