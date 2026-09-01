package br.com.oficina.infrastructure;
import br.com.oficina.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
interface ClienteRepository extends JpaRepository<Cliente,UUID>{Optional<Cliente> findByDocumento(String documento);} interface VeiculoRepository extends JpaRepository<Veiculo,UUID>{Optional<Veiculo> findByPlaca(String placa);} interface ServicoRepository extends JpaRepository<Servico,UUID>{} interface PecaRepository extends JpaRepository<Peca,UUID>{} interface OrdemServicoRepository extends JpaRepository<OrdemServico,UUID>{}
