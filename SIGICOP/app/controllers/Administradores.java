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
import segurancaDoSistema.Seguranca;
import segurancaDoSistema.SegurancaAdmin;
import util.CriptografiaUtils;

@With(SegurancaAdmin.class)
public class Administradores extends Controller {
	//A TELA DO ADMIN, RECEBE UM ADMIN
	public static void paginaAdmin() {
		String NomeDoUsuarioFiltro = params.get("NomeDoUsuarioFiltro");
		String matriculaDoUserFiltro = params.get("matriculaDoUserFiltro");
		String NomeDoArquivoFiltro = params.get("NomeDoArquivoFiltro");
		String descricaoFiltro = params.get("descricaoFiltro");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		
		String status = "AGUARDANDO";
		List<Pedido> listaPedidos =  new ArrayList<Pedido>();
		
		
		if(NomeDoUsuarioFiltro == null || NomeDoUsuarioFiltro.isEmpty() && matriculaDoUserFiltro == null || matriculaDoUserFiltro.isEmpty()
				&& NomeDoArquivoFiltro == null || NomeDoArquivoFiltro.isEmpty() && descricaoFiltro == null || descricaoFiltro.isEmpty()) {
			listaPedidos = Pedido.findAll();	
		}else if(NomeDoUsuarioFiltro != null || !NomeDoUsuarioFiltro.isEmpty() || matriculaDoUserFiltro != null || !matriculaDoUserFiltro.isEmpty()
				|| NomeDoArquivoFiltro != null || !NomeDoArquivoFiltro.isEmpty() || descricaoFiltro != null || !descricaoFiltro.isEmpty()) {
			listaPedidos = Pedido.find("adm = ?1 = ?1 AND lower(usuario.nome) like ?2  AND lower(usuario.matricula) = ?3"
					+ " AND lower(nomeArquivo) like ?4  AND lower(descricao) like ?5 AND status = ?6", admBanco.id,
					"%"+NomeDoUsuarioFiltro.toLowerCase()+"%", matriculaDoUserFiltro.toLowerCase()+"%",
					"%"+NomeDoArquivoFiltro.toLowerCase()+"%", "%"+descricaoFiltro.toLowerCase()+"%", status).fetch();
		}
		
		render(listaPedidos, admBanco);
	}
	
	// SÓ O ADMIN PADRAO CADASTRAR MAIS ADMINS
	public static void cadastroDeAdms() {
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		render(telaAdmin, admBanco);
	}
	
	// APOS CADASTRO FINALIZADO E MANDA PARA TELA DO ADMIN 
	public static void salvarAdm(@Valid Administrador adm) {
		Administrador adminBancoEmail = Administrador.find("email = ?1", adm.email).first();
		if(adm.id == null) {
			if(adminBancoEmail != null){
				flash.error("Email ja exite!");
				cadastroDeAdms();
			}else {				
				if (validation.hasErrors()) {
					params.flash();
					flash.error("Falha no Cadastro do Usuario!");
				cadastroDeAdms();
				}
			boolean senhaIguais = adm.compararSenha();

			if(senhaIguais) {
				flash.success("Administrador "+ adm.nomeAdm+" cadastrado com sucesso!");
				String senhaCript = CriptografiaUtils.criptografarMD5(adm.senha );
				adm.senha = senhaCript;
			}else {
				flash.error("Comparacao de senha invalida!");
				cadastroDeAdms();
			}
			}
		}else {
			flash.success("Administrador "+ adm.nomeAdm+" alterado com sucesso!");
			DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
			dadosSessaoAdmin.admin = adm;
			Cache.set(session.getId(), dadosSessaoAdmin);
		}
		adm.save();
		paginaAdmin();
	}
	
	// ENVIAR OS DADOS PARA EDITAR O ADMINISTRADOR LOGADO
	public static void editar() {
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador adm = dadosSessaoAdmin.admin;
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		renderTemplate("Administradores/cadastroDeAdms.html", adm, telaAdmin, admBanco);
	}
	
	// EDITAR A SENHA SEPARADAMENTE
	public static void editarSenha() {
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
		render(telaAdmin, admBanco);
	}
	// SALVAR A SENHA 
	public static void salvarSenha(String senha, String confirmarSenha) {
		DadosSessaoAdmin dadosSessao = Cache.get(session.getId(), DadosSessaoAdmin.class);
		
		if(senha.equals(confirmarSenha) && !senha.isEmpty() && !confirmarSenha.isEmpty()&& senha != null && confirmarSenha != null) {
			if(senha.length() > 5) {
			Administrador admBanco = Administrador.findById(dadosSessao.admin.id);
			String senhaCript = CriptografiaUtils.criptografarMD5(senha);
			admBanco.senha = senhaCript;
			admBanco.save();
			dadosSessao.admin = admBanco;
			Cache.set(session.getId(), dadosSessao);
			flash.success("senha alterada com sucesso!");
			editar();
			}else {
				flash.error("No minimo 6 caracteres!");
				editarSenha();
			}
		}else{
			flash.error("Confimarçao de senha invalida!");
			editarSenha();
		}
	}
	
	// FAZ DOWNLOAD DO ARQUIVO DO USUARIO
	public static void download(Long id) {
		Pedido ip = Pedido.findById(id);
		renderBinary(ip.arquivo.getFile(), ip.nomeArquivo);
	}
	// ALTERAR O STATUS PARA CONCLUIDO
	public static void concluido(Long idPedCon, String resposta) {
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
			
		Pedido ped = Pedido.findById(idPedCon);
		ped.atendimento = resposta;
		ped.status = StatusPedido.CONCLUIDO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		ped.save();
		flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" concluido!");
		paginaAdmin();
	}
	// ALTERAR O STATUS PARA RECUSADO
	public static void recusar(Long idPed, String motivo) {
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
	
		Pedido ped = Pedido.findById(idPed);
		ped.atendimento = motivo;
		ped.status = StatusPedido.RECUSADO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		ped.save();
		
		flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" Recusado!");
		paginaAdmin();
	}
	// LISTAR TODOS OS PEDIDO CONCLUIDOS
	public static void listarConcluidos() {
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
		String telaAdmin = "Tela Admin";
		String status = "CONCLUIDO";
		List<Pedido> listaconcluidos = Pedido.find(" STATUS = ?1 AND ADM_ID = ?2 ",status, admin).fetch();
		Administrador admBanco = dadosSessaoAdmin.admin;
		render(listaconcluidos, telaAdmin, admBanco);
	}
	// LISTAR TODOS OS PEDIDO RECUSADOS
	public static void listarRecusados() {
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
		String telaAdmin = "Tela Admin";
		String status = "RECUSADO";
		List<Pedido> listaRecusados = Pedido.find(" STATUS = ?1 AND ADM_ID = ?2 ",status, admin).fetch();
		Administrador admBanco = dadosSessaoAdmin.admin;
		render(listaRecusados, telaAdmin, admBanco);
	}
	public static void restaurarQtd() {
		List<Pedido> listaResetarQtd = Pedido.findAll();
	for (int i = 0; i < listaResetarQtd.size(); i++) {
		Usuario user = listaResetarQtd.get(i).usuario;
		user.qtdDisponivel = 20;
		user.save();
		}
		flash.success("Quantidade de solicitações restaurados para 20");
		paginaAdmin();
	}
	// PARA O ADMINISTRADOR SAIR DO SISTEMA 
	public static void sair() {
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
