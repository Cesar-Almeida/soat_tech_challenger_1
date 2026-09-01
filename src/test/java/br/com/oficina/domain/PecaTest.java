package br.com.oficina.domain;
import static org.junit.jupiter.api.Assertions.*; import java.math.BigDecimal; import org.junit.jupiter.api.Test;
class PecaTest { @Test void baixaEstoqueQuandoHaSaldo(){Peca p=new Peca("Filtro",new BigDecimal("25.00"),2);p.baixar(2);assertEquals(0,p.getEstoque());} @Test void impedeEstoqueNegativo(){Peca p=new Peca("Filtro",new BigDecimal("25.00"),1);assertThrows(IllegalStateException.class,()->p.baixar(2));} }
