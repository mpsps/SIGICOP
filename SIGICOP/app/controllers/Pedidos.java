package controllers;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sun.mail.imap.protocol.Status;

import annotations.Admin;
import annotations.User;
import play.data.validation.Valid;
import models.Administrador;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import models.Pedido;
import models.StatusPedido;
import models.Usuario;
import play.cache.Cache;
import play.db.jpa.JPABase;
import play.mvc.Controller;
import play.mvc.With;
import seguranca.Seguranca;

@With(Seguranca.class)
public class Pedidos extends Controller {
	
///// PÁGINA DE FAZER PEDIDO /////
	@User
	public static void fazerPedido() {
	System.out.println("_____________________________________________________");
	System.out.println("Pedidos.fazerPedido() ...["+ new Date()+"]");
			
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = dadosDeSessao.listaDePedidos;
		dadosDeSessao.listaDePedidos = listaDePedidos; 
		
		String voltar = "voltar";
		Usuario usuarioBanco = dadosDeSessao.usuario;
	
	render(listaDePedidos, voltar, usuarioBanco);
	}
	
///// ADICIONA PEDIDOS NA CACHE /////
	@User
	public static void addPedido(@Valid Pedido item) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.addPedido() ...["+ new Date()+"]");
			
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = null;
		if(dadosDeSessao == null) {
			dadosDeSessao = new DadosSessao();
		}else {
			listaDePedidos = dadosDeSessao.listaDePedidos;
		}
		if(listaDePedidos == null) {
			listaDePedidos = new ArrayList<Pedido>();
		}
		int valor;
		if(item.frenteVerso.equals("frenteEverso")) {
			valor = dadosDeSessao.usuario.qtdDisponivel - (item.qtdCopias * 2);
			if(valor < 0) {
			flash.error("quatidade de copia indisponivel");
		fazerPedido();
		}
		dadosDeSessao.usuario.qtdDisponivel = valor;
		Cache.set(session.getId(), dadosDeSessao);
		}else {
		valor = dadosDeSessao.usuario.qtdDisponivel - item.qtdCopias;
		if(valor < 0) {
			flash.error("quatidade de copia indisponivel");
		fazerPedido();
		}
		dadosDeSessao.usuario.qtdDisponivel = valor;
		Cache.set(session.getId(), dadosDeSessao);
		}
	//		if (validation.hasErrors()) {
	//			params.flash();
	//			flash.error("Falha no Cadastro do Pedido!");
	//			flash.keep();
	//			fazerPedido();
	//		}
			
		String nomeArq = params.get("name");
			
		if(item.arquivo == null || nomeArq == null) {
			flash.error("O Envio do Arquivo é obrigatorio");
			fazerPedido();
		}else if(item.qtdCopias == 0){
			flash.error("A Quantidade de Copias é obrigatorio");
			fazerPedido();
		}else if(item.frenteVerso == null){
			flash.error("Frente ou Verso é obrigatorio");
			fazerPedido();
		}
		
		item.nomeArquivo = nomeArq;
		item.usuario = dadosDeSessao.usuario;
		
		listaDePedidos.add(item);
		
		dadosDeSessao.listaDePedidos = listaDePedidos;
		Cache.set(session.getId(), dadosDeSessao);
	fazerPedido();
	}
	
///// SALVAR PEDIDO(S) /////
	@User
	public static void salvar() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.salvar() ...["+ new Date()+"]");
			
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos;
		
		for (int i = 0; i < dadosDeSessao.listaDePedidos.size(); i++) {
			dadosDeSessao.listaDePedidos.get(i).dataEnvio = new Date();	
		}
		listaDePedidos	= dadosDeSessao.listaDePedidos;
		
		for (int i = 0; i < listaDePedidos.size(); i++) {
			// salvar o usuario descrecentando a quantidade disponivel
			listaDePedidos.get(i).usuario = dadosDeSessao.usuario;
			listaDePedidos.get(i).save();
		}
			
		Usuario user = Usuario.findById(dadosDeSessao.usuario.id);
		user.qtdDisponivel = dadosDeSessao.usuario.qtdDisponivel;
		user.save();
		dadosDeSessao.listaDePedidos = null;
		Cache.set(session.getId(), dadosDeSessao);
		flash.success("Pedido(s) salvo(s)!");
	fazerPedido();
	}
	
///// APAGAR O PEDIDO DA CACHE /////
	@User
	public static void cancelarPedido(Long idPedido) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.cancelarPedidos() ...["+ new Date()+"]");
	
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = dadosDeSessao.listaDePedidos;
		
		for (int i = 0; i <dadosDeSessao.listaDePedidos.size(); i++) {
			if (listaDePedidos.get(i).id == idPedido) {
				// devolver a quantidade disponivel
			if(listaDePedidos.get(i).frenteVerso.equals("frenteEverso")) {
				dadosDeSessao.usuario.qtdDisponivel = dadosDeSessao.usuario.qtdDisponivel + (listaDePedidos.get(i).qtdCopias * 2); 
			}else {
				dadosDeSessao.usuario.qtdDisponivel = dadosDeSessao.usuario.qtdDisponivel + listaDePedidos.get(i).qtdCopias;
			}
				listaDePedidos.remove(i);
			}
		}
		if(listaDePedidos.size() == 0) {
			listaDePedidos = null;	
		}
		 dadosDeSessao.listaDePedidos = listaDePedidos; 
		
		Cache.set(session.getId(), dadosDeSessao);
		flash.success("Pedido Cancelado!");
	fazerPedido();
	}
	
///// ALTERAR O STATUS PARA CONCLUIDO /////
	@Admin
	public static void concluido(Long idPedCon, String resposta) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.concluido() ... ["+ new Date()+"]");
			
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
					
		Pedido ped = Pedido.findById(idPedCon);
		ped.atendimento = resposta;
		ped.status = StatusPedido.CONCLUIDO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		ped.save();
		flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" concluido!");
	Administradores.paginaAdmin();
	}
	
///// ALTERAR O STATUS PARA RECUSADO /////
	@Admin
	public static void recusar(Long idPed, String motivo) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.recusar() ... ["+ new Date()+"]");
				
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
			
		Pedido ped = Pedido.findById(idPed);
		Usuario user = ped.usuario;
		if(ped.frenteVerso.equals("frenteEverso")) {
			user.qtdDisponivel = user.qtdDisponivel + (ped.qtdCopias * 2);
		}else {
			user.qtdDisponivel = user.qtdDisponivel + ped.qtdCopias;
		}
		ped.atendimento = motivo;
		ped.status = StatusPedido.RECUSADO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		user.save();
		ped.save();
				
		flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" Recusado!");
	Administradores.paginaAdmin();
	}
	
///// DE RECUSAR PARA CONCLUIDO DECREMENTANDO /////
	@Admin
	public static void recusarParaConcluido(Long idPedCon, String resposta) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.concluido() ... ["+ new Date()+"]");
				
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
						
		Pedido ped = Pedido.findById(idPedCon);
		Usuario user = ped.usuario;
		if(ped.frenteVerso.equals("frenteEverso")) {
			user.qtdDisponivel = user.qtdDisponivel - (ped.qtdCopias * 2);
		}else {
			user.qtdDisponivel = user.qtdDisponivel - ped.qtdCopias;
		}
		ped.atendimento = resposta;
		ped.status = StatusPedido.CONCLUIDO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		user.save();
		ped.save();
		flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" concluido!");	
	listarRecusados();
	}
	
///// DE RECUSAR PARA CONCLUIDO DECREMENTANDO /////
	@Admin
	public static void concluidoParaRecusar(Long idPed, String resposta) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.concluido() ... ["+ new Date()+"]");
				
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
						
		Pedido ped = Pedido.findById(idPed);
		Usuario user = ped.usuario;
		if(ped.frenteVerso.equals("frenteEverso")) {
			user.qtdDisponivel = user.qtdDisponivel + (ped.qtdCopias * 2);
		}else {
			user.qtdDisponivel = user.qtdDisponivel + ped.qtdCopias;
		}
		ped.atendimento = resposta;
		ped.status = StatusPedido.RECUSADO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		user.save();
		ped.save();
		flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" concluido!");	
	listarConcluidos();;
	}
	
///// LISTAR TODOS OS PEDIDO CONCLUIDOS /////
	@Admin
	public static void listarConcluidos() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.listarConcluidos() ... ["+ new Date()+"]");
		String matriculaDoUsuarioFiltro = params.get("matriculaDoUsuarioFiltro");
		String nomeDoArquivoFiltro = params.get("nomeDoArquivoFiltro");
		if (matriculaDoUsuarioFiltro == null && nomeDoArquivoFiltro == null) {
			matriculaDoUsuarioFiltro = "";
			nomeDoArquivoFiltro = "";
		}
		System.out.println("Matricula: "+matriculaDoUsuarioFiltro);
		System.out.println("Nome do Arquivo: "+nomeDoArquivoFiltro);
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
					
		List<Pedido> listaconcluidos = new ArrayList<Pedido>();
			
		if (matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty() ) {
			listaconcluidos = Pedido.find(" status = ?1 AND adm_id = ?2 ", StatusPedido.CONCLUIDO, admBanco).fetch();
			System.out.println("Tentou filtrar  pedidos concluidos sem nada, ou entrou na página concluido!");
		}else if(!matriculaDoUsuarioFiltro.isEmpty() && !nomeDoArquivoFiltro.isEmpty()){
			listaconcluidos = Pedido.find("usuario.matricula like ?1 AND lower(nomeArquivo) like ?2 AND status like ?3 AND adm_id = ?4",
			"%" + matriculaDoUsuarioFiltro.trim()+ "%","%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%", StatusPedido.CONCLUIDO, admBanco).fetch();
			System.out.println("Tentou filtrar pedidos concluidos com conteudo!( Nome do Arquivo e matricula do usuario)");
		}else if(!matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty()){
			listaconcluidos = Pedido.find("usuario.matricula like ?1 AND status like ?2 AND adm_id = ?3",
			"%" + matriculaDoUsuarioFiltro.trim() + "%", StatusPedido.CONCLUIDO, admBanco).fetch();
			System.out.println("Tentou filtrar pedidos concluidos com conteudo!(só a matricula)");
		}else if(!nomeDoArquivoFiltro.isEmpty() && matriculaDoUsuarioFiltro.isEmpty()){
			listaconcluidos = Pedido.find("lower(nomeArquivo) like ?1 AND status like ?2 AND adm_id = ?3",
			"%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%",StatusPedido.CONCLUIDO,  admBanco).fetch();
			System.out.println("Tentou filtrar pedidos concluidos com conteudo!(só nome do arquivo)");
		}
	render(listaconcluidos, telaAdmin, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro);
	}
	
///// LISTAR TODOS OS PEDIDO RECUSADOS /////
	@Admin
	public static void listarRecusados() {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.listarRecusados() ... ["+ new Date()+"]");
		String matriculaDoUsuarioFiltro = params.get("matriculaDoUsuarioFiltro");
		String nomeDoArquivoFiltro = params.get("nomeDoArquivoFiltro");
		if (matriculaDoUsuarioFiltro == null && nomeDoArquivoFiltro == null) {
		matriculaDoUsuarioFiltro = "";
		nomeDoArquivoFiltro = "";
		}
		System.out.println("Matricula: "+matriculaDoUsuarioFiltro);
		System.out.println("Nome do Arquivo: "+nomeDoArquivoFiltro);
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		String telaAdmin = "Tela Admin";
					
		List<Pedido> listaRecusados = new ArrayList<Pedido>();
				
		if (matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty() ) {
			listaRecusados = Pedido.find(" status = ?1 AND adm_id = ?2 ", StatusPedido.RECUSADO, admBanco).fetch();
			System.out.println("Tentou filtrar sem nada ou entrou na página!");
		}else if(!matriculaDoUsuarioFiltro.isEmpty() && !nomeDoArquivoFiltro.isEmpty()){
			listaRecusados = Pedido.find("usuario.matricula like ?1 AND lower(nomeArquivo) like ?2 AND status like ?3 AND adm_id = ?4",
			"%" + matriculaDoUsuarioFiltro.trim()+ "%","%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%", StatusPedido.RECUSADO, admBanco).fetch();
			System.out.println("Tentou filtrar com conteudo!( Nome do Arquivo e matricula do usuario)");			
		}else if(!matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty()){
			listaRecusados = Pedido.find("usuario.matricula like ?1 AND status like ?2 AND adm_id = ?3",
			"%" + matriculaDoUsuarioFiltro.trim() + "%", StatusPedido.RECUSADO, admBanco).fetch();
			System.out.println("Tentou filtrar com conteudo!(só a matricula)");				
		}else if(!nomeDoArquivoFiltro.isEmpty() && matriculaDoUsuarioFiltro.isEmpty()){
			listaRecusados = Pedido.find("lower(descricao) like ?1 AND status like ?2 AND adm_id = ?3",
			"%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%",StatusPedido.RECUSADO,  admBanco).fetch();
			System.out.println("Tentou filtrar com conteudo!(só nome do arquivo)");			
		}		
	render(listaRecusados, telaAdmin, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro);
	}
	
///// REALIZAR BAIXA /////
	@SuppressWarnings("deprecation")
	@Admin
	public static void realizarBaixa(Pedido ped) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.realizarBaixa() ... ["+ new Date()+"]");
				
		if(ped.qtdCopias == 0){
			flash.error("A Quantidade de Copias é obrigatorio");
			Administradores.realizarPedido();
		}else if(ped.frenteVerso == null){
			flash.error("Frente ou Verso é obrigatorio");
			Administradores.realizarPedido();
		}
		int valor;
		if(ped.frenteVerso.equals("frenteEverso")) {
			valor = ped.usuario.qtdDisponivel - (ped.qtdCopias * 2);
			if(valor < 0) {
			flash.error("quatidade de copia indisponivel");
			Administradores.realizarPedido();
			}
			ped.usuario.qtdDisponivel = valor;
			if(valor < 0) {
			flash.error("quatidade de copia indisponivel");
			Administradores.realizarPedido();
			}
		}else {
		valor = ped.usuario.qtdDisponivel - ped.qtdCopias;
			if(valor < 0) {
				flash.error("quatidade de copia indisponivel");
				fazerPedido();
			}
			ped.usuario.qtdDisponivel = valor;
			Cache.set(session.getId(), ped);
			if(valor < 0) {
				flash.error("quatidade de copia indisponivel");
				fazerPedido();
			}
		}
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		ped.nomeArquivo = "Copia do Usuário: "+ped.usuario.matricula+" "+new Date().getDate()+" "+ new Date().getHours()+":"+new Date().getMinutes();
		ped.dataEnvio = new Date();
		ped.adm = dadosSessaoAdmin.admin;
		ped.atendimento = "Copia realizada pelo Admin: "+ dadosSessaoAdmin.admin.nomeAdm;
		ped.dataAtendimento = new Date();
		ped.dataEntrega = new Date();
		ped.status = StatusPedido.ENTREGUE;
		ped.usuario.save();
		ped.save();
		flash.success("Pedido de copia realizado com sucesso!");
	Administradores.realizarPedido();
	}
	
///// ENTREGAR PEDIDO /////
	@Admin
	public static void entregarPedido(Long id) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.entregarPedido() ... ["+ new Date()+"]");
				
			DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
			Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
						
			Pedido ped = Pedido.findById(id);
			ped.status = StatusPedido.ENTREGUE;
			ped.dataEntrega = new Date();
			ped.adm = admin;
			ped.save();
			flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" entregue!");
	listarConcluidos();;
	}
}
