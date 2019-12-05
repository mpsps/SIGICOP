package segurancaDoSistema;

import annotations.Admin;
import controllers.Administradores;
import controllers.Gerenciador;
import controllers.Usuarios;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

public class SegurancaAdmin extends Controller {

	@Before(unless = {"Administradores.logarAdm", "Administradores.autentificarAdmin"})
	static void verificarAdministrador() {
		if (!session.contains("adminLogado")) {
			flash.error("Voce precisa logar no sistema");
			Gerenciador.login();
		}
		DadosSessaoAdmin dadosSessaoAdmin = (DadosSessaoAdmin) Cache.get(session.getId());
		
		if (dadosSessaoAdmin == null) {
			flash.error("Voce precisa logar no sistema, a sessão expirou-se");
			Gerenciador.login();
		}
	}

}
