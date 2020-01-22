package controllers;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import play.data.validation.Valid;

import org.apache.commons.fileupload.ParameterParser;

import annotations.Admin;
import models.Administrador;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import models.Pedido;
import models.StatusPedido;
import models.Usuario;
import models.Administrador;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;
import seguranca.CriptografiaUtils;
import seguranca.Seguranca;

@With(Seguranca.class)
public class Administradores extends Controller {
///// PÁGINA ADMIN /////
	@Admin
	public static void paginaAdmin() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administrador.paginaAdmin() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
	
		String matriculaDoUsuarioFiltro = params.get("matriculaDoUsuarioFiltro");
		String nomeDoArquivoFiltro = params.get("nomeDoArquivoFiltro");
			
		if (matriculaDoUsuarioFiltro == null && nomeDoArquivoFiltro == null) {
			matriculaDoUsuarioFiltro = "";
			nomeDoArquivoFiltro = "";
		}
					
		List<Pedido> listaPedidosPa = new ArrayList<Pedido>();
			
		if (matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty() ) {
			listaPedidosPa = Pedido.find(" status = ?1 ", StatusPedido.AGUARDANDO).fetch();
			System.out.println("Tentou filtrar sem nada ou simplesmente entrou na página!");
		}else if(!matriculaDoUsuarioFiltro.isEmpty() && !nomeDoArquivoFiltro.isEmpty()){
			listaPedidosPa = Pedido.find("usuario.matricula like ?1 AND lower(nomeArquivo) like ?2 AND status like ?3  ",
			"%" + matriculaDoUsuarioFiltro.trim()+ "%","%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%",
			StatusPedido.AGUARDANDO).fetch();
				
			System.out.println("Tentou filtrar por Matricula do Usuario e Nome do Arquivo!");
			System.out.println("Matricula do Usuario = \""+matriculaDoUsuarioFiltro+"\"\nNome do Arquivo = \""+nomeDoArquivoFiltro+"\"");
				
		}else if(!matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty()){
			listaPedidosPa = Pedido.find("usuario.matricula like ?1 AND status like ?2 ",
			"%" + matriculaDoUsuarioFiltro.trim() + "%", StatusPedido.AGUARDANDO).fetch();
			System.out.println("Tentou filtrar por matricula e Matricula do Usuario!");
			System.out.println("Matricula do Usuario = \""+matriculaDoUsuarioFiltro+"\"");
		}else if(!nomeDoArquivoFiltro.isEmpty() && matriculaDoUsuarioFiltro.isEmpty()){
			listaPedidosPa = Pedido.find("lower(nomeArquivo) like ?1 AND status like ?2",
			"%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%",StatusPedido.AGUARDANDO).fetch();
			System.out.println("Tentou filtrar só por Nome do Arquivo!");
			System.out.println("Nome do Arquivo = \""+nomeDoArquivoFiltro+"\"");
		}
		String filtroPa = "";
		String temFiltro = "tem";
	render(listaPedidosPa, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro, filtroPa, temFiltro);
	}
	
///// SÓ O ADMIN PADRAO PODE CADASTRAR MAIS ADMINS /////
	@Admin
	public static void cadastroDeAdms() {
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;

		if(admBanco.admPadrao) {// verificar se é o admin padrão
			System.out.println("_____________________________________________________________________________________");
			System.out.println("Administrador.cadastroDeAdms() ... ["+ new Date()+"]");
			String telaAdmin = "Tela Admin";
			render(telaAdmin, admBanco);
		}else {// se não for avisa ao admin comum
			System.out.println("_____________________________________________________________________________________");
			System.out.println("Administrador.cadastroDeAdms() ... ["+ new Date()+"]");
			System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.cadastroDeAdms()");
			
			flash.error("Acesso restrito ao administrador padrao do sistema");
	     paginaAdmin();
		}
	}
	
///// APOS CADASTRO FINALIZADO E MANDA PARA TELA DO ADMIN ///// 
	@Admin
	public static void salvarAdm(@Valid Administrador adm) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.salvarAdm() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
				
		Administrador adminBancoEmail = Administrador.find("email = ?1", adm.email).first();
		
		if(adm.id == null) {// se o id do adm for nulo é pq o aadmin padrão está cadastrando um novo
//			if(admBanco.admPadrao) {// verificar se é o admin padrão
				if(adminBancoEmail != null){
					flash.error("Email ja exite!");
					String email = "Esse Email Já Existe";
				renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco, email);
				}else {				
					if (validation.hasErrors()) {
						params.flash();
						flash.error("Falha no Cadastro do Usuario!");
					}
					boolean senhaIguais = adm.compararSenha();
		
					if(senhaIguais) {
						flash.success("Administrador "+ adm.nomeAdm+" cadastrado com sucesso!");
						String senhaCript = CriptografiaUtils.criptografarMD5(adm.senha );
						adm.senha = senhaCript;
					}else {
						flash.error("Comparacao de senha invalida!");
						String comparar = "Não Está Compatível";
						renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco, comparar);
					}
//				}
			}
		}else {// se não estiver cadastrando é pq está editando
			dadosSessaoAdmin.admin = adm;
			Cache.set(session.getId(), dadosSessaoAdmin);
		}
		if(!adm.nomeAdm.isEmpty() && !adm.email.isEmpty()) {
			if(adm.id == null) {
				flash.success("O administrador "+adm.nomeAdm+" cadastrado com sucesso!");				
			}else {
				flash.success("O administrador "+adm.nomeAdm+" editado com sucesso!");				
			}
			adm.save(); // salvar mesmo estando cadastrando ou editando	
		}else {
			if (validation.hasErrors()) {
				params.flash();
				flash.error("Falha!");
			renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco);
			}
		}
		Administrador admEditadoBanco = Administrador.findById(admBanco.id);// busca o admin para setar na cache
		dadosSessaoAdmin.admin = admEditadoBanco;
		Cache.set(session.getId(), dadosSessaoAdmin);// salvar o admin na cache após editar 
		session.put("adminLogado", admEditadoBanco.nomeAdm); // salvar o nome do admin na sessão após editar 
	paginaAdmin();
	}
	
///// ENVIAR OS DADOS PARA EDITAR O ADMINISTRADOR LOGADO /////
	@Admin
	public static void editar() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.editar() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador adm = dadosSessaoAdmin.admin;
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		renderTemplate("Administradores/cadastroDeAdms.html", adm, telaAdmin, admBanco);
	}
	
///// EDITAR A SENHA SEPARADAMENTE /////
	@Admin
	public static void editarSenha() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.editarSenha() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		render(telaAdmin, admBanco);
	}
	
///// SALVAR A SENHA ///// 
	@Admin
	public static void salvarSenha(String senha, String confirmarSenha) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.salvarSenha() ... ["+ new Date()+"]");
			
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		
		if (senha == null && confirmarSenha == null) {
			senha = "";
			confirmarSenha = "";
		}
		
		if( !senha.isEmpty() && !confirmarSenha.isEmpty()) {
			if(senha.length() > 5 && confirmarSenha.length() > 5) {
				if(senha.equals(confirmarSenha)) {
					String senhaCript = CriptografiaUtils.criptografarMD5(senha);
					admBanco.senha = senhaCript;
					admBanco.save();
					dadosSessaoAdmin.admin = admBanco;
					Cache.set(session.getId(), dadosSessaoAdmin);
					flash.success("senha alterada com sucesso!");
					editar();	
				}else {
					flash.error("as senha não são compatíveis!");
					String seis = "incompatíveis";
				renderTemplate("Administradores/editarSenha.html", seis, telaAdmin, admBanco);
				}
			}else {
				flash.error("No minimo 6 caracteres!");
				String seis = "no minimo 6 caracteres";
			renderTemplate("Administradores/editarSenha.html", seis, telaAdmin, admBanco);
			}
		}else{
			flash.error("falha na alteração de senha!");
			String seis = "obrigatório";
		renderTemplate("Administradores/editarSenha.html", seis, telaAdmin, admBanco);
		}
	}
	
///// LISTA TODOS OS ADMINISTRADORES /////
	@Admin
	public static void listarTodosAdmins() {		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		
		if(admBanco.admPadrao) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.listarTodosAdmins() ... ["+ new Date()+"]" );
			
			String nomeDoAdminFiltro = params.get("nomeDoAdminFiltro");
			String emailDoAdminFiltro = params.get("emailDoAdminFiltro");
			
			if (nomeDoAdminFiltro == null && emailDoAdminFiltro == null) {
				nomeDoAdminFiltro = "";
				emailDoAdminFiltro = "";
			}
			List<Administrador> listarDeAdmins = new ArrayList<Administrador>();
			
			if(nomeDoAdminFiltro.isEmpty() && emailDoAdminFiltro.isEmpty()) {
				listarDeAdmins = Administrador.find("admPadrao = ?1", false).fetch();
			}else if(!nomeDoAdminFiltro.isEmpty() && emailDoAdminFiltro.isEmpty()){
				listarDeAdmins = Administrador.find("admPadrao = ?1 And nomeAdm LIKE ?2", false, "%"+nomeDoAdminFiltro+"%").fetch();
			}else if(nomeDoAdminFiltro.isEmpty() && !emailDoAdminFiltro.isEmpty()){
				listarDeAdmins = Administrador.find("admPadrao = ?1 And email LIKE ?2", false, "%"+ emailDoAdminFiltro.toLowerCase()+"%").fetch();
			}else if(!nomeDoAdminFiltro.isEmpty() && !emailDoAdminFiltro.isEmpty()){
				listarDeAdmins = Administrador.find("admPadrao = ?1 And nomeAdm LIKE ?2 AND email LIKE ?3 ", false,"%"+ nomeDoAdminFiltro+"%", "%"+ emailDoAdminFiltro.toLowerCase()+"%").fetch();
			}
			String listaAdmins = "listaAdmins";
			String telaAdmin = "telaAdmin";
			String temFiltro = "tem";
		render(listarDeAdmins, listaAdmins, admBanco, nomeDoAdminFiltro, emailDoAdminFiltro, telaAdmin, temFiltro);
		}else {
			System.out.println("_____________________________________________________________________________________");
			System.out.println("Administradores.listarTodosAdmins() ... ["+ new Date()+"]" );
			System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.listarTodosAdmins()");

			flash.error("Acesso restrito ao administrador padrao do sistema");
		Administradores.paginaAdmin();
		}
	}
	
///// REMOVER ADMINISTRADORES /////
	@Admin
	public static void removerAdmin(Long id) {		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
			
		if(admBanco.admPadrao) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.removerAdmin() ... ["+ new Date()+"]");
			Administrador admin = Administrador.findById(id);
			flash.success("Administrador "+admin.nomeAdm+" removido com sucesso!");
			admin.delete();
		listarTodosAdmins();
		}else {
			System.out.println("_____________________________________________________________________________________");
			System.out.println("Administradores.removerAdmin() ... ["+ new Date()+"]" );
			System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.removerAdmin()");

			flash.error("Acesso restrito ao administrador padrao do sistema");
		Administradores.paginaAdmin();
		}
	}
	
///// PÁGINA DE PEDIDO DE COPIA /////
	@Admin
	public static void realizarPedidoCopia() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.realizarPedidoCopia() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		
		String telaAdmin = "Tela Admin";
		List<Usuario> listaDeUsuario = new ArrayList<Usuario>();
		listaDeUsuario = Usuario.findAll();
	render(admBanco, telaAdmin, listaDeUsuario);
	}
///// FAZ DOWNLOAD DO ARQUIVO DO USUARIO /////
	@Admin
	public static void download(Long id) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.download() ... ["+ new Date()+"]");
		
		Pedido ip = Pedido.findById(id);
		if(!ip.arquivo.exists()) {
			flash.error("Arquivo não encontrado");
		Administradores.paginaAdmin();
		}
	renderBinary(ip.arquivo.getFile(), ip.nomeArquivo);
	}
	
///// PARA O ADMINISTRADOR SAIR DO SISTEMA ///// 
	@Admin
	public static void sair() {
		System.out.println("___________________________________________________________________________________");
		System.out.println("Administradores.sair() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
		admin.ultimoAcesso = new Date();
		admin.save();
		session.clear();
		Cache.clear();
		
		flash.success("Voce saiu do sistema");
	Gerenciador.login();
	}
}
