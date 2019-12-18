package controllers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.fabric.xmlrpc.base.Array;

import annotations.Admin;
import annotations.User;
import models.Administrador;
import models.DadosSessao;
import models.Pedido;
import models.StatusPedido;
import models.Usuario;
import play.cache.Cache;
import play.data.validation.Valid;
import play.db.jpa.Blob;
import play.mvc.Controller;
import play.mvc.With;
import seguranca.CriptografiaUtils;
import seguranca.Seguranca;

@With(Seguranca.class)
public class Usuarios extends Controller {
	
	// TELA DO USUARIO, MANDA USUARIO E LISTA DE PEDIDOS
	@User
		public static void paginaUsuario() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.paginaUsuario() ... ["+ new Date()+"]");
			
				DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);				
				Usuario usuarioBanco = dadosSessao.usuario;
			
				List<Pedido> listaPedidos = new ArrayList<Pedido>();
					listaPedidos = Pedido.find("usuario_id = ?1 ", usuarioBanco.id).fetch();	
				String solicitar = "solicitar";
				render(usuarioBanco, listaPedidos, solicitar);
		}
		
		// FILTRO DE PEDIDOS NA PAGINA DO USUARIO
	@User
		public static void filtro(String NomeDoArquivoFiltro, String descricaoFiltro) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.filtro() ... ["+ new Date()+"]");	
			
			DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);				
			Usuario usuarioBanco = dadosSessao.usuario;
		
			List<Pedido> listaPedidos = new ArrayList<Pedido>();
			
			if (NomeDoArquivoFiltro.isEmpty() && descricaoFiltro.isEmpty()) {
				listaPedidos = Pedido.find("usuario_id = ?1", usuarioBanco).fetch();
				System.out.println("Tentou filtrar sem nada!");
				
			}else if(!NomeDoArquivoFiltro.isEmpty() || !descricaoFiltro.isEmpty()){
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND lower(descricao) like ?2 AND usuario_id = ?3",
						"%" + NomeDoArquivoFiltro.toLowerCase() + "%","%" + descricaoFiltro.toLowerCase() + "%",
						usuarioBanco).fetch();
				System.out.println("Tentou filtrar com conteudo!(só Nome do Arquivo e Descricao)"+ descricaoFiltro.trim().replaceAll("\\s+"," "));

				}else if(!NomeDoArquivoFiltro.isEmpty() || descricaoFiltro.isEmpty()){
			listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND usuario_id = ?2",
					"%" + NomeDoArquivoFiltro.toLowerCase() + "%", usuarioBanco).fetch();
			System.out.println("Tentou filtrar com conteudo!(só nome do arquivo)");

			}else if(!descricaoFiltro.isEmpty()|| NomeDoArquivoFiltro.isEmpty()){
				listaPedidos = Pedido.find("lower(descricao) like ?1 AND usuario_id = ?2",
						"%" + descricaoFiltro.toLowerCase() + "%", usuarioBanco).fetch();
				System.out.println("Tentou filtrar com conteudo!(só descricao)");

				}
			
					String solicitar = "solicitar";
			renderTemplate("Usuarios/paginaUsuario.html", usuarioBanco, listaPedidos,solicitar, NomeDoArquivoFiltro, descricaoFiltro);
		}
		
		// TELA DE CADASTRO DE USUARIO
	@User
		public static void cadastroDeUsuario() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.cadastroDeUsuario() ...["+ new Date()+"]");
		
			session.clear();
			Cache.clear();
			render();
		}
		
		
	// SALVAR O USUARIO
	public static void salvarUsuario(@Valid Usuario user) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.salvarUsuario() ... ["+ new Date()+"]");
		
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
	
	// FAZ DOWNLOAD DO ARQUIVO DO USUARIO
	@User
	public static void download(Long id) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.download() ... ["+ new Date()+"]");
			Pedido ip = Pedido.findById(id);
			
			if(!ip.arquivo.exists()) {
				flash.error("Arquivo não encontrado");
				paginaUsuario();
			}
			System.out.println("pedido download: "+ ip.nomeArquivo);
			renderBinary(ip.arquivo.getFile(), ip.nomeArquivo);
		}
	// RESTAURAR A QUANTIDADE DISPONIVEL DE TODOS OS USUARIOS
	// ADMINISTRADOR PADRAO MANIPULA
	@Admin
	public static void restaurarQtd(String qtd) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.restaurarQtd() ... ["+ new Date()+"]");
		
		int restQtd = Integer.parseInt(qtd);
		List<Pedido> listaResetarQtd = Pedido.findAll();
	for (int i = 0; i < listaResetarQtd.size(); i++) {
		Usuario user = listaResetarQtd.get(i).usuario;
		user.qtdDisponivel = restQtd;
		user.save();
		}
		flash.success("Quantidade de solicitações restaurados para "+ restQtd);
		Administradores.paginaAdmin();
	}
	// SAIR
	@User
	public static void sair() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.sair() ... ["+ new Date()+"]");
		
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
