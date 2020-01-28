package controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import models.Historico;
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
		Usuario usuarioBanco = dadosDeSessao.usuario;
			
		List<Pedido> listaPedidosConcluidos = new ArrayList<Pedido>();
		listaPedidosConcluidos = Pedido.find("usuario_id = ?1 AND status = ?2", usuarioBanco.id, StatusPedido.CONCLUIDO).fetch();
		List<Pedido> listaPedidosRecusados = new ArrayList<Pedido>();
		listaPedidosRecusados = Pedido.find("usuario_id = ?1 AND status = ?2", usuarioBanco.id, StatusPedido.RECUSADO).fetch();
		List<Pedido> listaPedidosEntregues = new ArrayList<Pedido>();
		listaPedidosEntregues = Pedido.find("usuario_id = ?1 AND status = ?2", usuarioBanco.id, StatusPedido.ENTREGUE).fetch();
		
		String voltar = "voltar";
	
	render(listaDePedidos, voltar, usuarioBanco, listaPedidosConcluidos, listaPedidosEntregues, listaPedidosRecusados);
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
		
		String nomeArq = params.get("name"); // recebe o nome do arquivo	
			
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
		// idLista serve para poder listar, adicionar e remover os pedidos da Cache
		int idLista = 0;
		if(listaDePedidos.size() <= 0) {
			idLista = 1;
		}else {
			Pedido ultimoPedido = new Pedido();
			for (int i = 0; i < dadosDeSessao.listaDePedidos.size(); i++) {
				ultimoPedido = dadosDeSessao.listaDePedidos.get(i);
			}
		// pegar o idLista do ultimo pedido e soma mais 1 para o proximo
			idLista = ultimoPedido.idLista + 1;
		}
		item.idLista = idLista;
		
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
	System.out.println(idPedido);
	
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
	
		
		for (int i = 0; i <dadosDeSessao.listaDePedidos.size(); i++) {
			System.out.println(dadosDeSessao.listaDePedidos.get(i).idLista);
			if (dadosDeSessao.listaDePedidos.get(i).idLista == idPedido) {
				// devolver a quantidade disponivel
			if(dadosDeSessao.listaDePedidos.get(i).frenteVerso.equals("frenteEverso")) {
				dadosDeSessao.usuario.qtdDisponivel = dadosDeSessao.usuario.qtdDisponivel + (dadosDeSessao.listaDePedidos.get(i).qtdCopias * 2); 
			}else {
				dadosDeSessao.usuario.qtdDisponivel = dadosDeSessao.usuario.qtdDisponivel + dadosDeSessao.listaDePedidos.get(i).qtdCopias;
			}
			dadosDeSessao.listaDePedidos.remove(i);
			}
		}
		if(dadosDeSessao.listaDePedidos.size() == 0) {
			dadosDeSessao.listaDePedidos = null;	
		}
		
		
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
		}else {
			System.out.println("Matricula: "+matriculaDoUsuarioFiltro);
			System.out.println("Nome do Arquivo: "+nomeDoArquivoFiltro);			
		}
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
					
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
		String telaAdmin = "Tela Admin";
		String temFiltro = "tem";
	render(listaconcluidos, telaAdmin, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro, temFiltro);
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
		}else {
			System.out.println("Matricula: "+matriculaDoUsuarioFiltro);
			System.out.println("Nome do Arquivo: "+nomeDoArquivoFiltro);			
		}
	
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
					
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
		String telaAdmin = "Tela Admin";
		String temFiltro = "tem ";
	render(listaRecusados, telaAdmin, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro, temFiltro);
	}
	
///// REALIZAR BAIXA /////
	@Admin
	public static void realizarBaixa(Pedido ped) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.realizarBaixa() ... ["+ new Date()+"]");
		
		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		
		if(ped.qtdCopias == 0){
			flash.error("A Quantidade de Copias é obrigatorio");
			Administradores.realizarPedidoCopia();
		}else if(ped.frenteVerso == null){
			flash.error("Frente ou Verso é obrigatorio");
			Administradores.realizarPedidoCopia();
		}
		int valor;
		if(ped.frenteVerso.equals("frenteEverso")) {
			valor = ped.usuario.qtdDisponivel - (ped.qtdCopias * 2);
			if(valor < 0) {
			flash.error("quatidade de copia indisponivel");
			Administradores.realizarPedidoCopia();
			}
			ped.usuario.qtdDisponivel = valor;
			if(valor < 0) {
			flash.error("quatidade de copia indisponivel");
			Administradores.realizarPedidoCopia();
			}
		}else {
		valor = ped.usuario.qtdDisponivel - ped.qtdCopias;
			if(valor < 0) {
				flash.error("quatidade de copia indisponivel");
				Administradores.realizarPedidoCopia();
			}
			ped.usuario.qtdDisponivel = valor;
			if(valor < 0) {
				flash.error("quatidade de copia indisponivel");
				Administradores.realizarPedidoCopia();
			}
		}
		
		ped.nomeArquivo = "Copia do Usuário: "+ped.usuario.matricula+" "+new Date().getDate()+" "+ new Date().getHours()+":"+new Date().getMinutes();
		ped.dataEnvio = new Date();
		ped.adm = admBanco;
		ped.atendimento = "Copia realizada pelo Admin: "+ dadosSessaoAdmin.admin.nomeAdm;
		ped.dataAtendimento = new Date();
		ped.dataEntrega = new Date();
		ped.status = StatusPedido.ENTREGUE;
		ped.usuario.save();
		ped.save();
		flash.success("Pedido de copia do usuário "+ped.usuario.nomeUsu+" realizado com sucesso!");
	Administradores.realizarPedidoCopia();
	}
	
///// ENTREGAR PEDIDO /////
	@Admin
	public static void entregarPedido(Long id) throws IOException{
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.entregarPedido() ... ["+ new Date()+"]");
		System.out.println("id do pedido = "+id);

			DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
			Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);
						
			Pedido ped = Pedido.findById(id);
			ped.status = StatusPedido.ENTREGUE;
			ped.dataEntrega = new Date();
			ped.adm = admin;
			ped.save();
			// gerar relatório
			gerarRelatorio(ped);
			
	listarConcluidos();
	}
	
///// GERAR RELATÓRIO DO PEDIDO APÓS DE ENTREGUE /////
	@Admin
	public static void gerarRelatorio(Pedido ped) throws IOException {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.geraRelatorio() ... ["+ new Date()+"]");
		
			System.out.println("|__________________Gerando relatório...__________________|");
//			Historico historico = new Historico();
		try {
			FileWriter arq = new FileWriter("E:\\GitHubRepositorios\\SIGICOP\\SIGICOP\\historicoDePedidos\\"+ped.id+"_relatorio.txt");
			PrintWriter gravarArq = new PrintWriter(arq);
			
			gravarArq.println("|________________Relatório do pedido "+ped.id+"__________________|");
			gravarArq.println("|USUÁRIO: "+ped.usuario.nomeUsu);
			System.out.println("|USUÁRIO: "+ped.usuario.nomeUsu);
			gravarArq.println("|ADMINISTRADOR: "+ped.adm.nomeAdm);
			System.out.println("|ADMINISTRADOR: "+ped.adm.nomeAdm);
			gravarArq.println("|ID DO PEDIDO: "+ped.id+"                                        |"); 
			System.out.println("|ID DO PEDIDO: "+ped.id+"                                        |");
			gravarArq.println("|NOME DO ARQUIVO: "+ped.nomeArquivo);
			System.out.println("|NOME DO ARQUIVO: "+ped.nomeArquivo);
			gravarArq.println("|QTD DE CÓPIAS: "+ped.qtdCopias+"                                        |");
			System.out.println("|QTD DE CÓPIAS: "+ped.qtdCopias+"                                        |");
			gravarArq.println("|FACE: "+ped.frenteVerso);
			System.out.println("|FACE: "+ped.frenteVerso);
			gravarArq.println("|DESCRIÇÃO: "+ped.descricao);
			System.out.println("|DESCRIÇÃO: "+ped.descricao);
			gravarArq.println("|DATA DE ENVIO: "+ped.dataEnvio+"       |");
			System.out.println("|DATA DE ENVIO: "+ped.dataEnvio+"       |");
			gravarArq.println("|DATA DE ATENDIMENTO: "+ped.dataAtendimento+" |");
			System.out.println("|DATA DE ATENDIMENTO: "+ped.dataAtendimento+" |");
			gravarArq.println("|DATA DE ENTREGA: "+ped.dataEntrega+"     |");
			System.out.println("|DATA DE ENTREGA: "+ped.dataEntrega+"     |");
			gravarArq.println("|STATUS: "+ped.status);
			System.out.println("|STATUS: "+ped.status);
			gravarArq.println("|___________________Fim de Relatorio_____________________|");
			
			arq.close();
			flash.success("Pedido do usuario "+ped.usuario.nomeUsu+" entregue!");
			System.out.println("|___________________relatório criado_____________________|");
		} catch (Exception e) {
			System.out.println("ERRO AO GERAR RELATORIO DE PEDIDO ENTREGUE");
			flash.error("ERRO AO GERAR RELATORIO DE PEDIDO ENTREGUE");
		listarConcluidos();
		}
	listarConcluidos();
	}
///// ADICIONA PEDIDOS NA CACHE COM AJAX/////
//	@User
//	public static void addPedidoAjax(@Valid Pedido item) {
//	System.out.println("_____________________________________________________________________________________");
//	System.out.println("Pedidos.addPedidoAjax() ...["+ new Date()+"]");
//			
//		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
//		List<Pedido> listaDePedidos = null;
//		
//		if(dadosDeSessao == null) {
//			dadosDeSessao = new DadosSessao();
//		}else {
//			listaDePedidos = dadosDeSessao.listaDePedidos;
//		}
//		if(listaDePedidos == null) {
//			listaDePedidos = new ArrayList<Pedido>();
//		}
//		int valor;
//		if(item.frenteVerso.equals("frenteEverso")) {
//			valor = dadosDeSessao.usuario.qtdDisponivel - (item.qtdCopias * 2);
//			if(valor < 0) {
//			flash.error("quatidade de copia indisponivel");
//		fazerPedido();
//		}
//		dadosDeSessao.usuario.qtdDisponivel = valor;
//		Cache.set(session.getId(), dadosDeSessao);
//		}else {
//		valor = dadosDeSessao.usuario.qtdDisponivel - item.qtdCopias;
//		if(valor < 0) {
//			flash.error("quatidade de copia indisponivel");
//		fazerPedido();
//		}
//		dadosDeSessao.usuario.qtdDisponivel = valor;
//		Cache.set(session.getId(), dadosDeSessao);
//		}
//	//		if (validation.hasErrors()) {
//	//			params.flash();
//	//			flash.error("Falha no Cadastro do Pedido!");
//	//			flash.keep();
//	//			fazerPedido();
//	//		}
//			
//		String nomeArq = params.get("name");
//			
//		if(item.arquivo == null || nomeArq == null) {
//			flash.error("O Envio do Arquivo é obrigatorio");
//			fazerPedido();
//		}else if(item.qtdCopias == 0){
//			flash.error("A Quantidade de Copias é obrigatorio");
//			fazerPedido();
//		}else if(item.frenteVerso == null){
//			flash.error("Frente ou Verso é obrigatorio");
//			fazerPedido();
//		}
//		
//		item.nomeArquivo = nomeArq;
//		item.usuario = dadosDeSessao.usuario;
//		
//		listaDePedidos.add(item);
//		
//		dadosDeSessao.listaDePedidos = listaDePedidos;
//		Cache.set(session.getId(), dadosDeSessao);
//	renderJSON(listaDePedidos);
//	}
}
