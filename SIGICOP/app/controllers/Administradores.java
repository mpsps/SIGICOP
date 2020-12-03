package controllers;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import play.data.validation.Valid;

import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.mail.EmailException;

import annotations.Admin;
import models.Operador;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import models.Pedido;
import models.Usuario;
import models.Operador;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;
import seguranca.CriptografiaUtils;
import seguranca.Seguranca;
import util.Select2VO;
import util.SituacaoPedido;
import util.StatusPedido;

@With(Seguranca.class)
public class Administradores extends Controller {
///// PÁGINA ADMIN /////
	@Admin
	public static void paginaAdmin() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.paginaAdmin() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
	
		String matriculaDoUsuarioFiltro = params.get("matriculaDoUsuarioFiltro");
		String nomeDoArquivoFiltro = params.get("nomeDoArquivoFiltro");
			
		if (matriculaDoUsuarioFiltro == null && nomeDoArquivoFiltro == null) {
			matriculaDoUsuarioFiltro = "";
			nomeDoArquivoFiltro = "";
		}			
		List<Pedido> listaPedidosPa = new ArrayList<Pedido>();
		// INICIO FILTRO
		if (matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty() ) {
			listaPedidosPa = Pedido.find(" status = ?1", StatusPedido.AGUARDANDO).fetch();
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
		// FIM FILTRO
		String filtroPa = "";
		String temFiltro = "tem";
		String titulo;
		if (admBanco.administrador) {
			titulo = "Pagina do Administrador";
		} else {
			titulo = "Pagina do Operador";
		}
	render(listaPedidosPa, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro, filtroPa, temFiltro, titulo);
	}	
///// SÓ O ADMIN PADRAO PODE CADASTRAR MAIS ADMINS /////
	@Admin
	public static void cadastroDeAdms() {
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;

		if(admBanco.administrador && admBanco.id == 1) {// verificar se é o admin padrão
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administrador.cadastroDeAdms() ... ["+ new Date()+"]");
			String telaAdmin = "Tela Admin";
			String titulo = "Cadastro de Administrador";
		render(telaAdmin, admBanco, titulo);
		}else {// se não for avisa ao admin comum
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administrador.cadastroDeAdms() ... ["+ new Date()+"]");
		System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.cadastroDeAdms()");
			
			flash.error("Acesso restrito ao administrador padrão-absoluto do sistema");
		paginaAdmin();
		}
	}
///// APOS CADASTRO FINALIZADO E MANDA PARA TELA DO ADMIN ///// 
	@Admin
	public static void salvarAdm(@Valid Operador adm) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.salvarAdm() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		
		Operador adminBancoEmail = Operador.find("email = ?1", adm.email).first();
		
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
							String titulo = "Cadastro de Administrador";
						renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco, comparar, telaAdmin, titulo);
						}
					}else { //se não, então senha e confirmarSenha são inferiores a 6
						flash.error("no mínimo 6 caracteres!");
						String comparar = "mínimo 6 caracteres";
						String titulo = "Editar Meus Dados";
					renderTemplate("Administradores/cadastroDeAdms.html", adm, admBanco, comparar, telaAdmin, titulo);
					}
				if (validation.hasErrors()) { // verificar depois de tudo se contém algum erro
					validation.keep();
					flash.error("Falha no Cadastro do Operador!");
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
		Operador adm = dadosSessaoAdmin.admin;
		Operador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		String titulo = "Editar Meus Dados";
	renderTemplate("Administradores/cadastroDeAdms.html", adm, telaAdmin, admBanco, titulo);
	}
///// EDITAR A SENHA SEPARADAMENTE /////
	@Admin
	public static void editarSenha() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.editarSenha() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		String titulo = "Editar Senha";
	render(telaAdmin, admBanco, titulo);
	}
///// SALVAR A SENHA ///// 
	@Admin
	public static void salvarSenha(String senha, String confirmarSenha) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.salvarSenha() ... ["+ new Date()+"]");
			
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		
		if (senha == null && confirmarSenha == null) {
			senha = "";
			confirmarSenha = "";
		}
		
		if( !senha.isEmpty() && !confirmarSenha.isEmpty()) {
			if(senha.length() > 5 && confirmarSenha.length() > 5) {
				if(senha.equals(confirmarSenha)) {
					String senhaCript = CriptografiaUtils.criptografarMD5(senha);
					Operador admin = Operador.findById(admBanco.id);
					admin.senha = senhaCript;
					System.out.println("idddddddddddddddd= "+admin.id);
					admin.save();
					dadosSessaoAdmin.admin = admin;
					Cache.set(session.getId(), dadosSessaoAdmin);
					flash.success("senha alterada com sucesso!");
					editar();	
				}else {
					flash.error("as senha não são compatíveis!");
					String seis = "incompatíveis";
					String titulo = "Editar senha";
				renderTemplate("Administradores/editarSenha.html", seis, telaAdmin, admBanco, titulo);
				}
			}else {
				flash.error("No minimo 6 caracteres!");
				String seis = "no mínimo 6 caracteres";
				String titulo = "Editar senha";
			renderTemplate("Administradores/editarSenha.html", seis, telaAdmin, admBanco, titulo);
			}
		}else{
			flash.error("falha na alteração de senha!");
			String seis = "obrigatório";
			String titulo = "Editar senha";
		renderTemplate("Administradores/editarSenha.html", seis, telaAdmin, admBanco, titulo);
		}
	}
///// LISTA TODOS OS ADMINISTRADORES /////
	@Admin
	public static void listarTodosAdmins() {		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		
		if(admBanco.administrador) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.listarTodosAdmins() ... ["+ new Date()+"]" );
			
			String nomeDoAdminFiltro = params.get("nomeDoAdminFiltro");
			String emailDoAdminFiltro = params.get("emailDoAdminFiltro");
			
			if (nomeDoAdminFiltro == null && emailDoAdminFiltro == null) {
				nomeDoAdminFiltro = "";
				emailDoAdminFiltro = "";
			}
			List<Operador> listarDeAdmins = new ArrayList<Operador>();
			// INICIO FILTRO
			if(nomeDoAdminFiltro.isEmpty() && emailDoAdminFiltro.isEmpty()) {
				if (admBanco.id == 1) {
					listarDeAdmins = Operador.find("id != 1").fetch();
				}else {
					listarDeAdmins = Operador.find("admPadrao = ?1", false).fetch();				
				}
			}else if(!nomeDoAdminFiltro.isEmpty() && emailDoAdminFiltro.isEmpty()){
				if (admBanco.id == 1) {
					listarDeAdmins = Operador.find("id != 1 AND nomeAdm LIKE ?1", "%"+nomeDoAdminFiltro+"%").fetch();
				}else {
					listarDeAdmins = Operador.find("admPadrao = ?1 AND nomeAdm LIKE ?2", false, "%"+nomeDoAdminFiltro+"%").fetch();
				}
			}else if(nomeDoAdminFiltro.isEmpty() && !emailDoAdminFiltro.isEmpty()){
				if (admBanco.id == 1) {
					listarDeAdmins = Operador.find("id != 1 AND email LIKE ?1", "%"+ emailDoAdminFiltro.toLowerCase()+"%").fetch();
				}else {
					listarDeAdmins = Operador.find("admPadrao = ?1 AND email LIKE ?2", false, "%"+ emailDoAdminFiltro.toLowerCase()+"%").fetch();
				}
			}else if(!nomeDoAdminFiltro.isEmpty() && !emailDoAdminFiltro.isEmpty()){
				if (admBanco.id == 1) {
					listarDeAdmins = Operador.find("id != 1 AND nomeAdm LIKE ?1 AND email LIKE ?2 ", "%"+ nomeDoAdminFiltro+"%", "%"+ emailDoAdminFiltro.toLowerCase()+"%").fetch();
				}else {
					listarDeAdmins = Operador.find("admPadrao = ?1 And nomeAdm LIKE ?2 AND email LIKE ?3 ", false,"%"+ nomeDoAdminFiltro+"%", "%"+ emailDoAdminFiltro.toLowerCase()+"%").fetch();
				}
			}
			// FIM FILTRO
			String listaAdmins = "listaAdmins";
			String telaAdmin = "telaAdmin";
			String temFiltro = "tem";
			String titulo = "Listagem de Administradores";
		render(listarDeAdmins, listaAdmins, admBanco, nomeDoAdminFiltro, emailDoAdminFiltro, telaAdmin, temFiltro, titulo);
		}else {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.listarTodosAdmins() ... ["+ new Date()+"]" );
		System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.listarTodosAdmins()");

			flash.error("Acesso restrito ao administrador padrao do sistema");
		paginaAdmin();
		}
	}
///// REMOVER ADMINISTRADORES /////
	@Admin
	public static void removerAdmin(Long id) {		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
			
		if(admBanco.administrador) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.removerAdmin() ... ["+ new Date()+"]");
			Operador admin = Operador.findById(id);
			flash.success("Administrador "+admin.nomeAdm+" removido com sucesso!");
			admin.delete();
		listarTodosAdmins();
		}else {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.removerAdmin() ... ["+ new Date()+"]" );
		System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.removerAdmin()");

			flash.error("Acesso restrito ao administrador padrao do sistema");
		paginaAdmin();
		}
	}
///// PÁGINA DE PEDIDO DE COPIA /////
	@Admin
	public static void realizarPedidoCopia() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.realizarPedidoCopia() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		
		List<Usuario> listaDeUsuario = new ArrayList<Usuario>();
		listaDeUsuario = Usuario.findAll();
		
		String telaAdmin = "Tela Admin";
		String titulo = "Realizar Pedido de Cópia";
	render(admBanco, telaAdmin, listaDeUsuario, titulo);
	}
///// HISTORICO DE PEDIDOS ////
	@Admin
	public static void historicoPedAdm() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.historicoPedAdm()... ["+ new Date()+"]");
	
	DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
	Operador admBanco = dadosSessaoAdmin.admin;
		
		String descricaoFiltro = params.get("descricaoFiltro");
		String NomeDoArquivoFiltro = params.get("NomeDoArquivoFiltro");
			
		if (descricaoFiltro == null && NomeDoArquivoFiltro == null) {
			descricaoFiltro = "";
			NomeDoArquivoFiltro = "";
		}
		
		List<Pedido> listaPedidosHistorico = new ArrayList<Pedido>();
		// INICIO FILTRO
		if(descricaoFiltro.isEmpty() && NomeDoArquivoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("situacao = ?1 AND adm_id = ?2", SituacaoPedido.ARQUIVADO, admBanco).fetch();
		}else if(!descricaoFiltro.isEmpty() && !NomeDoArquivoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("lower(nomeArquivo) like ?1 AND lower(descricao) like ?2 AND situacao = ?3 AND adm_id = ?4", 
					"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", "%" + descricaoFiltro.trim().toLowerCase() + "%", SituacaoPedido.ARQUIVADO, admBanco).fetch();
		}else if(!descricaoFiltro.isEmpty() && NomeDoArquivoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("lower(descricao) like ?1 AND situacao = ?2 AND adm_id = ?3", 
					"%" + descricaoFiltro.trim().toLowerCase() + "%", SituacaoPedido.ARQUIVADO, admBanco).fetch();
		}else if(!NomeDoArquivoFiltro.isEmpty() && descricaoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("lower(nomeArquivo) like ?1 AND situacao = ?2 AND adm_id = ?3", 
					"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", SituacaoPedido.ARQUIVADO, admBanco).fetch();
		}
		// FIM FILTRO
		String temFiltro = "tem";
		String filtroHistorico = "tem";
		String telaAdmin = "telaAdmin";
		String titulo = "Histórico de Pedidos";
	render(admBanco, listaPedidosHistorico, filtroHistorico, titulo, descricaoFiltro, NomeDoArquivoFiltro,temFiltro, telaAdmin);
	}
///// TELA DE CADASTRO DE USUARIO /////
	@Admin
	public static void cadastroDeUsuario() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.cadastroDeUsuario() ...["+ new Date()+"]");
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		
		String titulo = "Cadastro de Usuário";
		String telaAdmin = "Tela Admin";
	render(admBanco,titulo,telaAdmin);
	}
///// REMOVER USUARIO /////
	@Admin
	public static void removerUsuario() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Administradores.removerUsuario() ...["+ new Date()+"]");
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		
		if(admBanco.administrador) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Administradores.buscarUsuario() ... ["+ new Date()+"]" );
				
			String matDoUsuario = params.get("matDoUsuario");
			String emailDoUsuario = params.get("emailDoUsuario");
			
			if(emailDoUsuario == null) {
				emailDoUsuario = "";
			}
			if (matDoUsuario == null) {
				matDoUsuario = "";
			}
			Usuario usuarioBuscado = new Usuario();
			// INICIO FILTRO
			if(!matDoUsuario.isEmpty() && emailDoUsuario.isEmpty()){
				usuarioBuscado = Usuario.find("matricula = ?1", matDoUsuario).first();
			}else if(matDoUsuario.isEmpty() && !emailDoUsuario.isEmpty()){	
				usuarioBuscado = Usuario.find("email = ?1", emailDoUsuario).first();
			}else if(!matDoUsuario.isEmpty() && !emailDoUsuario.isEmpty()){	
				usuarioBuscado = Usuario.find("matricula = ?1 AND email = ?2", matDoUsuario, emailDoUsuario.toLowerCase()).first();
			}else if(matDoUsuario.isEmpty() && emailDoUsuario.isEmpty()){
				usuarioBuscado = null;
			}
			// FIM FILTRO
				String temFiltro = "tem";
				String telaAdmin = "telaAdmin";
				String titulo = "Remover Usuário";
			renderTemplate("Administradores/removerUsuario.html", usuarioBuscado, admBanco, matDoUsuario, emailDoUsuario, telaAdmin, titulo, temFiltro);
			}else {
				System.out.println("_____________________________________________________________________________________");
				System.out.println("Administradores.listarTodosAdmins() ... ["+ new Date()+"]" );
				System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.listarTodosAdmins()");

				flash.error("Acesso restrito ao administrador padrao do sistema");
			paginaAdmin();
			}
		
		String temFiltro = "tem";
		String titulo = "Remover Usuário";
		String telaAdmin = "Tela Admin";	
	render(admBanco, titulo, telaAdmin, temFiltro);
	}
///// PARA O ADMINISTRADOR SAIR DO SISTEMA ///// 
	@Admin
	public static void sair() {
	System.out.println("___________________________________________________________________________________");
	System.out.println("Administradores.sair() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admin = Operador.findById(dadosSessaoAdmin.admin.id);
		admin.ultimoAcesso = new Date();
		admin.save();
		session.clear();
		Cache.clear();
		
		flash.success("Voce saiu do sistema");
	Gerenciador.login();
	}
}