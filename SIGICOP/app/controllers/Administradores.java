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
		String telaAdmin = "Tela Admin";
		
		Administrador adminBancoEmail = Administrador.find("email = ?1", adm.email).first();
		
		if(adm.id == null) {// se o id do adm for nulo é pq o admin padrão está cadastrando um novo
				if(adminBancoEmail != null){ // se for diferente de nulo então existe admin com esse email
					if (validation.hasErrors()) { // enviar todos os erros se só preencer email
						params.flash();
					}
					flash.error("Email ja exite!");
					String email = "esse Email Já Existe";
				renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco, email, telaAdmin);
				}else { // se não, então não possui nenhum admin com esse email		
					if (validation.hasErrors()) { // guarda o(s) erro(s) se houver
						params.flash();
					}
					// verificar se o tamanho de senha e confirmarSenha são superior a 5
					if(adm.senha.length() > 5 && adm.confirmarSenha.length() > 5 ) { 
						boolean senhaIguais = adm.compararSenha();
						
						if(senhaIguais) {
							String senhaCript = CriptografiaUtils.criptografarMD5(adm.senha); // criptografa a senha
							adm.senha = senhaCript; // adm.senha recebe a senha criptografada
						}else {
							if (validation.hasErrors()) { // guarda o(s) erro(s) se houver
								params.flash();
							}
							flash.error("comparação de senha inválida!");
							String comparar = "não está compatível";
						renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco, comparar, telaAdmin);
						}
					}else { //se não, então senha e confirmarSenha são inferiores a 6
						flash.error("no mínimo 6 caracteres!");
						String comparar = "mínimo 6 caracteres";
					renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco, comparar, telaAdmin);
					}
				if (validation.hasErrors()) { // verificar depois de tudo se contém algum erro
					validation.keep();
					flash.error("Falha no Cadastro do Usuario!");
				cadastroDeAdms();
				}else { // se não contém nenhum erro, então permite cadastrar
					flash.success("O administrador "+adm.nomeAdm+" cadastrado com sucesso!");
					adm.save(); // salvar o admin após cadastrar
				paginaAdmin();
				}
			}
		}else { // se não estiver cadastrando é pq está editando
			if(adm.nomeAdm == null && adm.email == null) { // se vier nulo, transformar em vazio
				adm.nomeAdm = "";
				adm.email = "";
			}
			if(!adm.nomeAdm.isEmpty() && !adm.email.isEmpty()) { // verificar se está vazio
				flash.success("O administrador "+adm.nomeAdm+" editado com sucesso!");				
				adm.save(); // salvar o admin após editar	
				
				dadosSessaoAdmin.admin = adm; // salvar o admin na cache após editar 
				Cache.set(session.getId(), dadosSessaoAdmin);
				session.put("adminLogado", adm.nomeAdm); // salvar o nome do admin na sessão após editar 
			paginaAdmin();
			}else {
				if (validation.hasErrors()) {
					params.flash();
					flash.error("Falha ao editar o admin!");
				editar(); // manda parar editar(), se ocorrer algun erro
				}
			}
		}
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
				String seis = "no mínimo 6 caracteres";
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
