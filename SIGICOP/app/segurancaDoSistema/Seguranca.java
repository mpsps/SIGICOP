package segurancaDoSistema;

import annotations.Admin;
import controllers.Administradores;
import controllers.Gerenciador;
import controllers.Usuarios;
import models.DadosSessao;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

public class Seguranca extends Controller {
	
	@Before(unless = {"Gerenciador.login","Usuarios.cadastroDeUsuario", "Usuarios.autenticarUsuario", "Usuarios.salvarUsuario"})
	static void verificarAutenticacao() {
		if (!session.contains("usuarioLogado")) {
			flash.error("Voce precisa logar no sistema");
			Gerenciador.login();
		}
		
		DadosSessao dadosSessao = (DadosSessao) Cache.get(session.getId());
		if (dadosSessao == null) {
			flash.error("Voce precisa logar no sistema, a sessão expirou-se");
			Gerenciador.login();
		}
	}

}
