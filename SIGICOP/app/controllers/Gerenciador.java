package controllers;
import java.util.Date;

import models.Administrador;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import models.Usuario;
import play.cache.Cache;
import play.mvc.Controller;
import seguranca.CriptografiaUtils;

public class Gerenciador extends Controller {
//// MANDA PARA A PAGINA PRINCIPAL /////
	public static void principal() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Gerenciador.principal() ... ["+ new Date()+"]");
	
		String usuario = session.get("usuarioLogado");
		if(usuario != null) {
			DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);
			if(dadosSessao != null) {
				Usuario usu = Usuario.findById(dadosSessao.usuario.id);
				usu.ultimoAcessoUsu = new Date();
				usu.save();				
			}
		}
		String admin = session.get("adminLogado");
		if(admin != null) {
			DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
				if(dadosSessaoAdmin != null) {
					Administrador adm = Administrador.findById(dadosSessaoAdmin.admin.id);
					adm.ultimoAcesso = new Date();
					adm.save();
			}
		}
		session.clear();
		Cache.clear();
	render();
	}
	
///// PAGINA DE LOGAR O USUARIO OU ADMIN /////
	public static void login() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Gerenciador.login() ... ["+ new Date()+"]");
	
		String usuario = session.get("usuarioLogado");
		if(usuario != null) {
			DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);
			if(dadosSessao != null) {
				Usuario usu = Usuario.findById(dadosSessao.usuario.id);
				usu.ultimoAcessoUsu = new Date();
				usu.save();				
			}
		}
		String admin = session.get("adminLogado");
		if(admin != null) {
			DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
				if(dadosSessaoAdmin != null) {
					Administrador adm = Administrador.findById(dadosSessaoAdmin.admin.id);
					adm.ultimoAcesso = new Date();
					adm.save();
			}
		}
		session.clear();
		Cache.clear();
		String titulo = "Insirar seu login e senha";
	render(titulo);
	}
	
///// AUTENTIFICAR O USUARIO E ADMIN /////
	public static void autenticar(String login, String senha) throws InterruptedException {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Gerenciador.autenticar() ...["+ new Date()+"]");
		
		String senhaCript = CriptografiaUtils.criptografarMD5(senha);
		Administrador admin = Administrador.find(" email = ?1 AND senha = ?2", login, senhaCript).first();
		Usuario user = Usuario.find(" matricula = ?1 AND senha = ?2", login, senhaCript).first();
		System.out.println("");
		System.out.println();
			
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
			Cache.set(session.getId(), dadosSessao);
			System.out.println("Administrador `"+ admin.nomeAdm +"` Autenticado no Banco de Dados...");
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
			System.out.println("Usuario `"+user.nomeUsu+"` Autenticado no Banco de Dados...");
		Usuarios.paginaUsuario();
		}else {
			flash.error("Falha na Autentificação!");
		login();
		}
	}
}



