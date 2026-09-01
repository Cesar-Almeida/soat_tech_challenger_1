package br.com.oficina.domain;
import jakarta.persistence.*; import java.math.BigDecimal; import java.util.UUID;
@Entity public class Servico { @Id @GeneratedValue private UUID id; @Column(nullable=false) private String descricao; @Column(nullable=false,precision=12,scale=2) private BigDecimal preco; private int minutosEstimados;
 protected Servico(){} public Servico(String d,BigDecimal p,int m){descricao=d;preco=p;minutosEstimados=m;} public UUID getId(){return id;} public String getDescricao(){return descricao;} public BigDecimal getPreco(){return preco;} public int getMinutosEstimados(){return minutosEstimados;} public void atualizar(String d,BigDecimal p,int m){descricao=d;preco=p;minutosEstimados=m;} }
