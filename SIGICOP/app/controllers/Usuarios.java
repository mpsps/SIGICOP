package controllers;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import com.mysql.fabric.xmlrpc.base.Array;

import annotations.Admin;
import annotations.User;
import models.Operador;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import models.Pedido;
import models.Usuario;
import play.cache.Cache;
import play.data.validation.Valid;
import play.db.jpa.Blob;
import play.libs.Mail;
import play.mvc.Controller;
import play.mvc.With;
import seguranca.CriptografiaUtils;
import seguranca.Seguranca;
import util.Select2VO;
import util.SituacaoPedido;
import util.StatusPedido;

@With(Seguranca.class)
public class Usuarios extends Controller {
///// PAGINA DO USUARIO /////
	@User
	public static void paginaUsuario() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.paginaUsuario() ... ["+ new Date()+"]");
			
		DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);				
		Usuario usuarioBanco = dadosSessao.usuario;
		
		List<Pedido> listaPedidos = new ArrayList<Pedido>();
		listaPedidos = Pedido.find("usuario_id = ?1 AND situacao = ?2", usuarioBanco.id, SituacaoPedido.DESARQUIVADO).fetch();	

		String solicitar = "solicitar";
		String temFiltro = "tem";
		String titulo = "Página do Usuário";
	render(usuarioBanco, listaPedidos, solicitar, temFiltro, titulo);
	}	
///// FILTRO DE PEDIDOS NA PAGINA DO USUARIO /////
	@User
	public static void filtro(String NomeDoArquivoFiltro, String descricaoFiltro, String statusFiltro) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.filtro() ... ["+ new Date()+"]");	
			
		DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);				
		Usuario usuarioBanco = dadosSessao.usuario;
		
		StatusPedido status = StatusPedido.AGUARDANDO;
		
		List<Pedido> listaPedidosConcluidos = new ArrayList<Pedido>();
		List<Pedido> listaPedidosRecusados = new ArrayList<Pedido>();
		List<Pedido> listaPedidosEntregues = new ArrayList<Pedido>();
		
		if(statusFiltro == null) {
			statusFiltro = "";
		}
		
		if (NomeDoArquivoFiltro == null && descricaoFiltro == null) {
			NomeDoArquivoFiltro = "";
			descricaoFiltro = "";
		}
		if(NomeDoArquivoFiltro.equals("") && descricaoFiltro.equals("") && statusFiltro.equals("TODOS")) {
			paginaUsuario();
		}
		
		if(statusFiltro.equals("ENTREGUE")) {
			status = StatusPedido.ENTREGUE;
		}else if(statusFiltro.equals("CONCLUIDO")) {
			status = StatusPedido.CONCLUIDO;
		}else if(statusFiltro.equals("RECUSADO")) {
			status = StatusPedido.RECUSADO;
		}else if(statusFiltro.equals("AGUARDANDO")){
			status = StatusPedido.AGUARDANDO;
		}
		
		List<Pedido> listaPedidos = new ArrayList<Pedido>();
			
		if (NomeDoArquivoFiltro.isEmpty() && descricaoFiltro.isEmpty()) {
			if(statusFiltro.equals("TODOS")) {
				listaPedidos = Pedido.find("usuario_id = ?1 AND situacao = ?2", usuarioBanco, SituacaoPedido.DESARQUIVADO).fetch();
			}else {
				listaPedidos = Pedido.find("usuario_id = ?1 AND status = ?2 AND situacao = ?3", usuarioBanco, status, SituacaoPedido.DESARQUIVADO).fetch();	
			}
			System.out.println("Tentou filtrar sem nada!");
		}else if(!NomeDoArquivoFiltro.isEmpty() || !descricaoFiltro.isEmpty()){
			if(statusFiltro.equals("TODOS")) {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND lower(descricao) like ?2 AND usuario_id = ?3 AND situacao = ?4",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%","%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco, SituacaoPedido.DESARQUIVADO).fetch();
			}else {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND lower(descricao) like ?2 AND usuario_id = ?3 AND status = ?4 AND situacao = ?5",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%","%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco, status, SituacaoPedido.DESARQUIVADO).fetch();		
			}
			System.out.println("Tentou filtrar com conteudo!(só Nome do Arquivo e Descricao)"+ descricaoFiltro.trim().replaceAll("\\s+"," "));
		}else if(!NomeDoArquivoFiltro.isEmpty() || descricaoFiltro.isEmpty()){
			if(statusFiltro.equals("TODOS")) {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND usuario_id = ?2 AND situacao = ?3",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", usuarioBanco, SituacaoPedido.DESARQUIVADO).fetch();
			}else {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND usuario_id = ?2 AND status = ?3 AND situacao = ?4",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", usuarioBanco, status, SituacaoPedido.DESARQUIVADO).fetch();
			}
			System.out.println("Tentou filtrar com conteudo!(só nome do arquivo)");
		}else if(!descricaoFiltro.isEmpty()|| NomeDoArquivoFiltro.isEmpty()){
			if(statusFiltro.equals("TODOS")) {
				listaPedidos = Pedido.find("lower(descricao) like ?1 AND usuario_id = ?2 AND situacao = ?3",
						"%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco, SituacaoPedido.DESARQUIVADO).fetch();
				System.out.println("Tentou filtrar com conteudo!(só descricao)");
			}else {
				listaPedidos = Pedido.find("lower(descricao) like ?1 AND usuario_id = ?2 AND status = ?3 AND situacao = ?4",
						"%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco, status, SituacaoPedido.DESARQUIVADO).fetch();
			}
			System.out.println("Tentou filtrar com conteudo!(só descricao)");		
		}
		String solicitar = "solicitar";
		String temFiltro = "tem";
		String titulo = "Página do Usuário";
	renderTemplate("Usuarios/paginaUsuario.html", usuarioBanco, listaPedidos, solicitar, NomeDoArquivoFiltro, descricaoFiltro, temFiltro, statusFiltro, listaPedidosConcluidos, listaPedidosEntregues, listaPedidosRecusados, titulo);
	}
///// HISTÓRICO DE PEDIDOS /////
	@User
	public static void historicoPedUsu() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuaios.historicoPedUsu()... ["+ new Date()+"]");
	
		String descricaoFiltro = params.get("descricaoFiltro");
		String NomeDoArquivoFiltro = params.get("NomeDoArquivoFiltro");
		
		DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);
		Usuario usuarioBanco = dadosSessao.usuario;
		
		if (descricaoFiltro == null && NomeDoArquivoFiltro == null) {
			descricaoFiltro = "";
			NomeDoArquivoFiltro = "";
		}
		List<Pedido> listaPedidosHistorico = new ArrayList<Pedido>();
		
		if(descricaoFiltro.isEmpty() && NomeDoArquivoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("situacao = ?1 AND usuario_id = ?2", SituacaoPedido.ARQUIVADO, usuarioBanco).fetch();
		}else if(!descricaoFiltro.isEmpty() && !NomeDoArquivoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("lower(nomeArquivo) like ?1 AND lower(descricao) like ?2 AND situacao = ?3 AND usuario_id = ?4", 
					"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", "%" + descricaoFiltro.trim().toLowerCase() + "%", SituacaoPedido.ARQUIVADO, usuarioBanco).fetch();
		}else if(!descricaoFiltro.isEmpty() && NomeDoArquivoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("lower(descricao) like ?1 AND situacao = ?2 AND usuario_id = ?3", 
					"%" + descricaoFiltro.trim().toLowerCase() + "%", SituacaoPedido.ARQUIVADO, usuarioBanco).fetch();
		}else if(!NomeDoArquivoFiltro.isEmpty() && descricaoFiltro.isEmpty()) {
			listaPedidosHistorico = Pedido.find("lower(nomeArquivo) like ?1 AND situacao = ?2 AND usuario_id = ?3", 
					"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", SituacaoPedido.ARQUIVADO, usuarioBanco).fetch();
		}
		
		String voltar = "voltar";
		String solicitar = "solicitar";
		String temFiltro = "tem";
		String titulo = "Histórico de Pedidos";
	render(usuarioBanco, listaPedidosHistorico, solicitar, temFiltro, titulo, voltar, descricaoFiltro, NomeDoArquivoFiltro);
	}
///// SALVAR O USUARIO /////
	@Admin
	public static void salvarUsuario(@Valid Usuario user) throws EmailException {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuaios.salvarUsuario() ... ["+ new Date()+"]");
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		
		String titulo = "Cadastro de Usuário";
		String telaAdmin = "Tela Admin";

		Usuario userBancoMat = Usuario.find("matricula = ?1 ", user.matricula).first();
		Usuario userBancoEmail = Usuario.find("email = ?1", user.email).first();
		if(userBancoMat != null) {
			flash.error("Matricula Já Existente!");
			String mat = "essa matricula já existe";
		renderTemplate("Administradores/cadastroDeUsuario.html", user, mat,admBanco, titulo, telaAdmin);
		}else if (userBancoEmail != null){
			flash.error("Matricula Já Existente!");
			String email = "esse email já existe";
		renderTemplate("Administradores/cadastroDeUsuario.html", user, email,admBanco, titulo, telaAdmin);
		}else {
			///// COMPARAR SENHAS /////
			boolean userSenhaCompara = user.compararSenha();
			if(userSenhaCompara) {
				String senhaCript = CriptografiaUtils.criptografarMD5(user.senha);
				user.senha = senhaCript;
				if (validation.hasErrors()) {
					params.flash();
					flash.error("Falha no Cadastro do Usuario!");
				renderTemplate("Administradores/cadastroDeUsuario.html", user,admBanco, titulo, telaAdmin);
				}
				flash.success("Usuário Cadastrado com Sucesso!");
				try {
//					SimpleEmail email = new SimpleEmail();
//					email.setFrom("magdielpereira07@gmail.com.br");
//					email.addTo("magdiel.pereira@escolar.ifrn.edu.br");
//					email.setSubject("subject");
//					email.setMsg("você está cadastrado na SIGICOP");
//					Mail.send(email);
				} catch (Exception e) {
					System.out.println(e);
					System.out.println("falha no envio do email");
				}
				user.save();
			}else {
				flash.error("Confimarçao de senha invalida!");	
				String comparar = "não está compatível";
			renderTemplate("Administradores/cadastroDeUsuario.html", user, comparar,admBanco, titulo, telaAdmin);
			}	
		}
	Administradores.paginaAdmin();
	}
///// DELETAR USUARIO /////
	@Admin
	public static void deletarUsuario(Long id) {
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Operador admBanco = dadosSessaoAdmin.admin;
		Usuario user = Usuario.findById(id);
			
		if(admBanco.admPadrao) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.deletarUsuario() ... ["+ new Date()+"]");
		
			flash.success("Usuario "+user.nomeUsu+" removido com sucesso!");
			user.delete();
		Administradores.removerUsuario();
		}else {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Usuarios.deletarUsuario() ... ["+ new Date()+"]" );
		System.out.println("Um Administrador comum: '"+admBanco.nomeAdm+"' tentou acessar Administrador.removerAdmin()");

			flash.error("Acesso restrito ao administrador padrao do sistema");
		Administradores.removerUsuario();
		}
	}
///// EDITAR A SENHA /////
	@User
	public static void editarSenha() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.editarSenha() ... ["+ new Date()+"]");
		
		DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);				
		Usuario usuarioBanco = dadosSessao.usuario;
		
		String solicitar = "solicitar";
		String titulo = "Editar senha";
		String voltar = "voltar";
	render(usuarioBanco, titulo, voltar, solicitar);
	}
	
///// SALVAR A SENHA ///// 
	@User
	public static void salvarSenha(String senha, String confirmarSenha) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.salvarSenha() ... ["+ new Date()+"]");
			
		DadosSessao dadosSessao = Cache.get(session.getId(), DadosSessao.class);				
		Usuario usuarioBanco = dadosSessao.usuario;
		
		String voltar = "voltar";
		String solicitar = "solicitar";
		if (senha == null && confirmarSenha == null) {
			senha = "";
			confirmarSenha = "";
		}
		
		if( !senha.isEmpty() && !confirmarSenha.isEmpty()) {
			if(senha.length() > 5 && confirmarSenha.length() > 5) {
				if(senha.equals(confirmarSenha)) {
					String senhaCript = CriptografiaUtils.criptografarMD5(senha);
					Usuario user = Usuario.findById(usuarioBanco.id);
					user.senha = senhaCript;
					user.save();
					dadosSessao.usuario = usuarioBanco;
					Cache.set(session.getId(), dadosSessao);
					flash.success("senha alterada com sucesso!");
					paginaUsuario();	
				}else {
					flash.error("as senha não são compatíveis!");
					String seis = "incompatíveis";
					String titulo = "Editar senha";
				renderTemplate("Usuarios/editarSenha.html", seis, voltar, usuarioBanco, titulo, solicitar);
				}
			}else {
				flash.error("No minimo 6 caracteres!");
				String seis = "no mínimo 6 caracteres";
				String titulo = "Editar senha";
			renderTemplate("Usuarios/editarSenha.html", seis, voltar, usuarioBanco, titulo, solicitar);
			}
		}else{
			flash.error("falha na alteração de senha!");
			String seis = "obrigatório";
			String titulo = "Editar senha";
		renderTemplate("Usuarios/editarSenha.html", seis, voltar, usuarioBanco, titulo, solicitar);
		}
	}
///// SAIR /////
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
///// RESTAURAR A QUANTIDADE DISPONIVEL DE TODOS OS USUARIOS /////
	@Admin
	public static void restaurarQtd(int qtd) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.restaurarQtd() ... ["+ new Date()+"]");
		
		//	int restQtd = Integer.parseInt(qtd);
		List<Usuario> listaResetarQtd = Usuario.findAll();
		for (int i = 0; i < listaResetarQtd.size(); i++) {
			Usuario user = listaResetarQtd.get(i);
			user.qtdDisponivel = qtd;
			user.save();
		}
		flash.success("Quantidade de solicitações restaurados para "+ qtd);
	Administradores.paginaAdmin();
	}
///// ESSE METODO É CHAMADO COM AJAX + SELECT2 NA NA PAGINA DE REALIZAR BAIXA LOCALIZADO EM AMINISTRADORES /////
	@Admin
	public static void listarUsuarios(String term) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.listarUsuarios() ... ["+ new Date()+"]");
	
		if (term == null) {
			List<Usuario> usuarios = Usuario.findAll();
			List<Select2VO> results = new ArrayList<Select2VO>();
			for (Usuario u : usuarios) {
				Select2VO sVO = new Select2VO(u.id.toString(), u.toString());
				results.add(sVO);
			}
			
		}else {
			List<Usuario> usuarios = Usuario.find("lower(matricula) like ?1 OR lower(nomeUsu) like ?2",  "%"+term.toLowerCase() + "%", "%"+term.toLowerCase() + "%").fetch(20);	
			List<Select2VO> results = new ArrayList<Select2VO>();
			for (Usuario u : usuarios) {
				Select2VO sVO = new Select2VO(u.id.toString(), u.toString());
				results.add(sVO);
			}
		renderJSON(results);
		}		
	}
}
