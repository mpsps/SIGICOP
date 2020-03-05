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
import models.Administrador;
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
		listaPedidos = Pedido.find("usuario_id = ?1 ", usuarioBanco.id).fetch();	

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
				listaPedidos = Pedido.find("usuario_id = ?1", usuarioBanco).fetch();
			}else {
				listaPedidos = Pedido.find("usuario_id = ?1 AND status = ?2", usuarioBanco, status).fetch();	
			}
			System.out.println("Tentou filtrar sem nada!");
		}else if(!NomeDoArquivoFiltro.isEmpty() || !descricaoFiltro.isEmpty()){
			if(statusFiltro.equals("TODOS")) {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND lower(descricao) like ?2 AND usuario_id = ?3",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%","%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco).fetch();
			}else {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND lower(descricao) like ?2 AND usuario_id = ?3 AND status = ?4",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%","%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco, status).fetch();		
			}
			System.out.println("Tentou filtrar com conteudo!(só Nome do Arquivo e Descricao)"+ descricaoFiltro.trim().replaceAll("\\s+"," "));
		}else if(!NomeDoArquivoFiltro.isEmpty() || descricaoFiltro.isEmpty()){
			if(statusFiltro.equals("TODOS")) {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND usuario_id = ?2",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", usuarioBanco).fetch();
			}else {
				listaPedidos = Pedido.find("lower(nomeArquivo) like ?1 AND usuario_id = ?2 AND status = ?3",
						"%" + NomeDoArquivoFiltro.trim().toLowerCase() + "%", usuarioBanco, status).fetch();
			}
			System.out.println("Tentou filtrar com conteudo!(só nome do arquivo)");
		}else if(!descricaoFiltro.isEmpty()|| NomeDoArquivoFiltro.isEmpty()){
			if(statusFiltro.equals("TODOS")) {
				listaPedidos = Pedido.find("lower(descricao) like ?1 AND usuario_id = ?2",
						"%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco).fetch();
				System.out.println("Tentou filtrar com conteudo!(só descricao)");
			}else {
				listaPedidos = Pedido.find("lower(descricao) like ?1 AND usuario_id = ?2 AND status = ?3",
						"%" + descricaoFiltro.trim().toLowerCase() + "%", usuarioBanco, status).fetch();
			}
			System.out.println("Tentou filtrar com conteudo!(só descricao)");		
		}
		String solicitar = "solicitar";
		String temFiltro = "tem";
		String titulo = "Página do Usuário";
	renderTemplate("Usuarios/paginaUsuario.html", usuarioBanco, listaPedidos, solicitar, NomeDoArquivoFiltro, descricaoFiltro, temFiltro, statusFiltro, listaPedidosConcluidos, listaPedidosEntregues, listaPedidosRecusados, titulo);
	}
	
///// TELA DE CADASTRO DE USUARIO /////
	public static void cadastroDeUsuario() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.cadastroDeUsuario() ...["+ new Date()+"]");
	
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
		String titulo = "Cadastro de Usuário";
	render(titulo);
	}
	
///// SALVAR O USUARIO /////
	public static void salvarUsuario(@Valid Usuario user) throws EmailException {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Usuarios.salvarUsuario() ... ["+ new Date()+"]");
		
		Usuario userBancoMat = Usuario.find("matricula = ?1 ", user.matricula).first();
		Usuario userBancoEmail = Usuario.find("email = ?1", user.email).first();
		if(userBancoMat != null) {
			flash.error("Matricula Já Existente!");
			String mat = "essa matricula já existe";
		renderTemplate("usuarios/cadastroDeUsuario.html", user, mat);
		}else if (userBancoEmail != null){
			flash.error("Matricula Já Existente!");
			String email = "esse email já existe";
		renderTemplate("usuarios/cadastroDeUsuario.html", user, email);
		}else {
			///// COMPARAR SENHAS /////
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
			renderTemplate("Usuarios/cadastroDeUsuario.html", user, comparar);
			}	
		}
	Gerenciador.login();
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
