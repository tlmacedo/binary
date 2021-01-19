import br.com.tlmacedo.binary.controller.Operacoes;

public class Testes {

    public static void main(String[] args) {
        Operacoes operacoes = new Operacoes();
        System.out.printf("ativa:%s\n", operacoes.minimaVolatilidadeAtiva());
    }
}
