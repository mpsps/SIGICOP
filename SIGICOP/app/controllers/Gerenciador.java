package controllers;
import models.Administrador;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import models.Usuario;
import play.cache.Cache;
import play.mvc.Controller;
import util.CriptografiaUtils;

public class Gerenciador extends Controller {
	// MANDA PARA A TELA PRINCIPAL
	public static void principal() {
		session.clear();
		Cache.clear();
		render();
	}
	// TELA DE LOGAR O USUARIO E ADMIN
		public static void login() {
			session.clear();
			Cache.clear();
			render();
		}
		// AUTENTIFICAR O USUARIO E ADMIN
		public static void autenticar(String login, String senha) throws InterruptedException {
			
			String senhaCript = CriptografiaUtils.criptografarMD5(senha);
			Administrador admin = Administrador.find(" email = ?1 AND senha = ?2", login, senhaCript).first();
			Usuario user = Usuario.find(" matricula = ?1 AND senha = ?2", login, senhaCript).first();
			
			if (admin != null && user == null) {
				session.put("adminLogado", admin.nomeAdm);			
				flash.success("Bem-vindo "+admin.nomeAdm+" !");
				
				DadosSessaoAdmin dadosSessao = null;
				if (Cache.get(session.getId()) != null) {
					dadosSessao = Cache.get(session.getId(), DadosSessaoAdmin.class);	
				}
				if (dadosSessao == null) {
					dadosSessao = new DadosSessaoAdmin();
				}
				dadosSessao.admin = admin;
				System.out.println("Meu admin: "+dadosSessao.admin.nomeAdm );
				Cache.set(session.getId(), dadosSessao);
				Administradores.paginaAdmin();
			}else if(user != null && admin == null){
					session.put("usuarioLogado", user.nomeUsu);
					flash.success("Bem-vindo "+user.nomeUsu+" !");
					
					DadosSessao dadosSessao = null;
					if (Cache.get(session.getId()) != null) {
						dadosSessao = Cache.get(session.getId(), DadosSessao.class);
					}
					
					if (dadosSessao == null) {
						dadosSessao = new DadosSessao();
					}
					
					dadosSessao.usuario = user;
					
					Cache.set(session.getId(), dadosSessao);
					
					Usuarios.paginaUsuario();
				
				
			}else {
				flash.error("Falha na Autentificação!");
				login();
			}
		}
	
}



