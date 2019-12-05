package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DadosSessao implements Serializable {
	public List<Pedido> listaDePedidos;
	public Usuario usuario;
//	public List<String> preferencias;

	public DadosSessao() {

	}
}
