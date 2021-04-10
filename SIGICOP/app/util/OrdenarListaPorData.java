package util;

import java.util.Comparator;

import models.Pedido;

public class OrdenarListaPorData implements Comparator<Pedido> {

	@Override
	public int compare(Pedido o1, Pedido o2) {
		
		// Verificando se um dos dois Pedidos não foram atendidos ainda (se está em AGUARDANDO)
		if (o1.dataAtendimento == null || o2.dataAtendimento == null) {
			// Este pedido vai ser ordenado para o final da lista
			return -1;
		} else {

			if (o1.dataAtendimento.before(o2.dataAtendimento)) {
				// Este Pedido vai ser ordenado 
				return +1;		

			} else if (o1.dataAtendimento.after(o2.dataAtendimento)) {
				return -1;
			}
			return 0;
		}
	}
}
