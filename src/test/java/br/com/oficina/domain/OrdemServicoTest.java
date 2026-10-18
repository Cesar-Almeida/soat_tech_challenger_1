package br.com.oficina.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrdemServicoTest {
    @Test
    void aceitaFluxoDeAprovacaoEAExecucao() {
        OrdemServico os = new OrdemServico(new Cliente("52998224725", "Ana", null, null), null);

        assertDoesNotThrow(() -> os.mudarStatus(StatusOs.EM_DIAGNOSTICO));
        assertDoesNotThrow(() -> os.mudarStatus(StatusOs.AGUARDANDO_APROVACAO));
        assertDoesNotThrow(() -> os.mudarStatus(StatusOs.EM_EXECUCAO));
    }

    @Test
    void impedePularEtapaDoFluxo() {
        OrdemServico os = new OrdemServico(new Cliente("52998224725", "Ana", null, null), null);

        assertThrows(IllegalStateException.class, () -> os.mudarStatus(StatusOs.FINALIZADA));
    }
}
