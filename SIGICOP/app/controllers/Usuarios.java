package controllers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.fabric.xmlrpc.base.Array;

import models.Administrador;
import models.DadosSessao;
import models.Pedido;
import models.Usuario;
import play.cache.Cache;
import play.data.validation.Valid;
import play.db.jpa.Blob;
import play.mvc.Controller;
import play.mvc.With;
import segurancaDoSistema.Seguranca;
import util.CriptografiaUtils;

@With(Seguranca.class)
public class Usuarios extends Controller {
	
	// TELA DE CADASTRO DE USUARIO
	public static void cadastroDeUsuario() {
		session.clear();
		Cache.clear();
		render();
	}

	// SALVAR O USUARIO
	public static void salvarUsuario(@Valid Usuario user) {
		
		Usuario userBancoMat = Usuario.find("matricula = ?1 ", user.matricula).first();
		Usuario userBancoEmail = Usuario.find("email = ?1", user.email).first();
		if(userBancoMat != null) {
			flash.error("Matricula Já Existente!");
			String mat = "Matricula Já Existe!";
			renderTemplate("usuarios/cadastroDeUsuario.html", user, mat);
		}else if (userBancoEmail != null){
			flash.error("Matricula Já Existente!");
			String ema = "email Já Existe!";
			renderTemplate("usuarios/cadastroDeUsuario.html", user, ema);
		}else {
		// COMPARAR SENHAS
			boolean userSenhaCompara = user.compararSenha();
		if(userSenhaCompara) {
			String senhaCript = CriptografiaUtils.criptografarMD5(user.senha);
			user.senha = senhaCript;
			if (validation.hasErrors()) {
				params.flash();
				flash.error("Falha no Cadastro do Usuario!");
				renderTemplate("Usuarios/cadastroDeUsuario.html", user);
			}
			flash.success("Usuário Cadastrado com Sucesso!");
			user.save();
		}else {
			if(!userSenhaCompara) {
				flash.error("Confimarçao de senha invalida!");		
			}
			renderTemplate("Usuarios/cadastroDeUsuario.html", user);
		}
		}
		Gerenciador.login();
	}
	
	// TELA DO USUARIO, MANDA USUARIO E LISTA DE PEDIDOS
	public static void paginaUsuario() {
			String NomeAquirvo = params.get("NomeDoArquivoFiltro");
			String descricao = params.get("descricaoFiltro");
			
			DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);				
			Usuario usuarioBanco = dadosSessao.usuario;
		
			List<Pedido> listaPedidos = new ArrayList<Pedido>();
			if(NomeAquirvo == null || NomeAquirvo.isEmpty()) {
				listaPedidos = Pedido.find("usuario_id = ?1 ", usuarioBanco.id).fetch();	
			}else if(NomeAquirvo != null || !NomeAquirvo.isEmpty() || descricao != null || !descricao.isEmpty()) {
				listaPedidos = Pedido.find("usuario_id = ?1 AND lower(nomeArquivo) like ?2  AND lower(descricao) like ?3",
						usuarioBanco.id, "%"+NomeAquirvo.toLowerCase()+"%", "%"+descricao.toLowerCase()+"%").fetch();
			}
			String solicitar = "solicitar";
			render(usuarioBanco, listaPedidos, NomeAquirvo,solicitar);
	}
	// FAZ DOWNLOAD DO ARQUIVO DO USUARIO
		public static void download(Long id) {
			Pedido ip = Pedido.findById(id);
			renderBinary(ip.arquivo.getFile(), ip.nomeArquivo);
		}
	// LOGOFF
	public static void sair() {
		DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);
		Usuario usu = Usuario.findById(dadosSessao.usuario.id);
		usu.ultimoAcessoUsu = new Date();
		usu.save();
		session.clear();
		Cache.clear();
		flash.success("Voce saiu do sistema");
		Gerenciador.login();
	}
}
