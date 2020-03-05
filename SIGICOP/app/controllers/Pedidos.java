package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.sun.mail.imap.protocol.Status;

import annotations.Admin;
import annotations.User;
import play.data.validation.Valid;
import models.Administrador;
import models.DadosSessao;
import models.DadosSessaoAdmin;
import models.Pedido;
import models.Usuario;
import play.cache.Cache;
import play.db.jpa.JPABase;
import play.mvc.Controller;
import play.mvc.With;
import seguranca.Seguranca;
import util.Historico;
import util.StatusPedido;

@With(Seguranca.class)
public class Pedidos extends Controller {

///// PÁGINA DE FAZER PEDIDO /////
	@User
	public static void solicitar() {
		System.out.println("_____________________________________________________");
		System.out.println("Pedidos.solicitar() ...[" + new Date() + "]");

		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = dadosDeSessao.listaDePedidos;
		dadosDeSessao.listaDePedidos = listaDePedidos;
		Usuario usuarioBanco = dadosDeSessao.usuario;

		List<Pedido> listaPedidosConcluidos = new ArrayList<Pedido>(); // lista todos os pedidos com o status concluido
		listaPedidosConcluidos = Pedido.find("usuario_id = ?1 AND status = ?2", usuarioBanco.id, StatusPedido.CONCLUIDO)
				.fetch();
		List<Pedido> listaPedidosRecusados = new ArrayList<Pedido>(); // lista todos os pedidos com o status recusado
		listaPedidosRecusados = Pedido.find("usuario_id = ?1 AND status = ?2", usuarioBanco.id, StatusPedido.RECUSADO)
				.fetch();
		List<Pedido> listaPedidosEntregues = new ArrayList<Pedido>(); // lista todos os pedidos com o status entregue
		listaPedidosEntregues = Pedido.find("usuario_id = ?1 AND status = ?2", usuarioBanco.id, StatusPedido.ENTREGUE)
				.fetch();

		String voltar = "voltar";
		String titulo = "Solicitar";
		render(listaDePedidos, voltar, usuarioBanco, listaPedidosConcluidos, listaPedidosEntregues,
				listaPedidosRecusados, titulo);
	}

///// ADICIONA PEDIDOS NA CACHE /////
	@User
	public static void addPedido(@Valid Pedido item) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.addPedido() ...[" + new Date() + "]");

		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = null;

		if (dadosDeSessao == null) {
			dadosDeSessao = new DadosSessao();
		} else {
			listaDePedidos = dadosDeSessao.listaDePedidos;
		}
		if (listaDePedidos == null) {
			listaDePedidos = new ArrayList<Pedido>();
		}
		int valorTotal = dadosDeSessao.usuario.qtdDisponivel - (item.paginas * item.qtdCopias);
		if (valorTotal < 0) { // se o valor da qtd disponível for menor que 0 então a qtd está indisponível
			flash.error("quatidade de impressão indisponível");
			solicitar();
		}
	
		dadosDeSessao.usuario.qtdDisponivel = valorTotal; // setar a qtd disponível atualizada no usuário da cache
		Cache.set(session.getId(), dadosDeSessao);
		// if (validation.hasErrors()) {
		// params.flash();
		// flash.error("Falha no Cadastro do Pedido!");
		// flash.keep();
		// solicitar();
		// }

		String nomeArq = params.get("name"); // recebe o nome do arquivo

		if (item.arquivo == null || nomeArq == null) { // vereficar se o arquivo existe
			flash.error("O Envio do Arquivo é obrigatorio");
			solicitar();
		} else if (item.qtdCopias == 0) { // vereficar se a qtdCopias é 0
			flash.error("A Quantidade de Copias é obrigatorio");
			solicitar();
		} else if (item.paginas == 0) { // vereficar frente ou frenteEverso foi escolhido
			flash.error("a quantidade de páginas é obrigatorio");
			solicitar();
		}
		// idLista serve para poder listar, adicionar e remover os pedidos da Cache
		int idLista = 0;
		if (listaDePedidos.size() <= 0) { // na primeira vez id lista recebe 1
			idLista = 1;
		} else {
			// serve para quando o usuário apagar um pedido que está no meio da listagem
			Pedido ultimoPedido = new Pedido();
			for (int i = 0; i < dadosDeSessao.listaDePedidos.size(); i++) {
				ultimoPedido = dadosDeSessao.listaDePedidos.get(i);
			}
			// pegar o idLista do ultimo pedido e soma mais 1 para o proximo
			idLista = ultimoPedido.idLista + 1;
		}
		item.idLista = idLista; // o idLista do item recebe o idLista do servidor

		item.nomeArquivo = nomeArq; // o item recebe o nome do arquivo
		item.usuario = dadosDeSessao.usuario; // o item recebe o usuário da cache

		listaDePedidos.add(item); // item adicionado na lista

		dadosDeSessao.listaDePedidos = listaDePedidos; // lista adicionada na cache
		Cache.set(session.getId(), dadosDeSessao);
		solicitar();
	}

///// SALVAR PEDIDO(S) /////
	@User
	public static void salvar() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.salvar() ...[" + new Date() + "]");

		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos;

		for (int i = 0; i < dadosDeSessao.listaDePedidos.size(); i++) {
			dadosDeSessao.listaDePedidos.get(i).dataEnvio = new Date(); // seta data de envio em todos os pedidos
		}
		listaDePedidos = dadosDeSessao.listaDePedidos;

		for (int i = 0; i < listaDePedidos.size(); i++) {
			listaDePedidos.get(i).usuario = dadosDeSessao.usuario;
			listaDePedidos.get(i).save(); // salva todos os pedidos da lista
		}

		Usuario user = Usuario.findById(dadosDeSessao.usuario.id);
		user.qtdDisponivel = dadosDeSessao.usuario.qtdDisponivel;
		user.save(); // salvar o usuario descrecentando a quantidade disponivel no banco
		if(dadosDeSessao.listaDePedidos.size() < 2) {
			flash.success("Pedido salvo!");
		}else {
			flash.success("Pedidos salvos!");
		}
		dadosDeSessao.listaDePedidos = null;
		Cache.set(session.getId(), dadosDeSessao);
		solicitar();
	}

///// APAGAR O PEDIDO DA CACHE /////
	@User
	public static void cancelarPedido(Long idPedido) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.cancelarPedidos() ...[" + new Date() + "]");
		System.out.println(idPedido);

		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);

		for (int i = 0; i < dadosDeSessao.listaDePedidos.size(); i++) {
			System.out.println(dadosDeSessao.listaDePedidos.get(i).idLista);
			if (dadosDeSessao.listaDePedidos.get(i).idLista == idPedido) {
				// devolver a quantidade disponivel
					dadosDeSessao.usuario.qtdDisponivel = dadosDeSessao.usuario.qtdDisponivel
							+ (dadosDeSessao.listaDePedidos.get(i).qtdCopias * dadosDeSessao.listaDePedidos.get(i).paginas) ;
				dadosDeSessao.listaDePedidos.remove(i);
			}
		}
		if (dadosDeSessao.listaDePedidos.size() == 0) {
			dadosDeSessao.listaDePedidos = null;
		}

		Cache.set(session.getId(), dadosDeSessao);
		flash.success("Pedido Cancelado!");
		solicitar();
	}

///// PARA O USUARIO PODER APAGAR O PEDIDO DO BANCO DE DADOS /////
	@User
	public static void apagarPedidoAguardando(Long id) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.apagarPedido() ... [" + new Date() + "]");

		Pedido ped = Pedido.findById(id);
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);

		if (ped != null) { // vereficar se o pedido é nulo
			if (ped.status.name() == "AGUARDANDO") { // vereficar se o pedido está com status "AGUARDANDO"
				if (ped.usuario.id == dadosDeSessao.usuario.id) { // vereficar se o usuário do pedido é o mesmo que está
																	// apagando ele

					Long idRemover = dadosDeSessao.usuario.id;
					Usuario usuarioBanco = Usuario.findById(idRemover);
					if (usuarioBanco != null) {
						usuarioBanco.qtdDisponivel = usuarioBanco.qtdDisponivel + ped.qtdCopias;
						usuarioBanco.save();
						dadosDeSessao.usuario = usuarioBanco;
						ped.delete();
						flash.success("Pedido apagado do Banco de Dados!");
						Usuarios.paginaUsuario();
					} else {
						flash.error("Erro ao apagar o Pedido, usuário não encontrado!");
						Usuarios.paginaUsuario();
					}
				} else {
					flash.error("Este Pedido não foi encontrado ou não lhe pertençe!");
					Usuarios.paginaUsuario();
				}
			} else {
				flash.error("Este Pedido não está em AGUARDANDO!");
				Usuarios.paginaUsuario();
			}
		} else {
			flash.error("Pedido não encontrado no Banco de Dados!");
			Usuarios.paginaUsuario();
		}
	}

///// ALTERAR O STATUS PARA CONCLUIDO /////
	@Admin
	public static void concluido(Long idPedCon, String resposta) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.concluido() ... [" + new Date() + "]");

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);

		Pedido ped = Pedido.findById(idPedCon);
		ped.atendimento = resposta;
		ped.status = StatusPedido.CONCLUIDO; // concluir o pedido
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		ped.save();
		flash.success("Pedido do usuario " + ped.usuario.nomeUsu + " concluido!");
		Administradores.paginaAdmin();
	}

///// ALTERAR O STATUS PARA RECUSADO /////
	@Admin
	public static void recusar(Long idPed, String motivo) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.recusar() ... [" + new Date() + "]");

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);

		Pedido ped = Pedido.findById(idPed);
		Usuario user = ped.usuario;
		
		user.qtdDisponivel = user.qtdDisponivel + (ped.qtdCopias * ped.paginas); // devolver a qtd disponível para o usuário

		ped.atendimento = motivo;
		ped.status = StatusPedido.RECUSADO; // recusar o pedido
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		user.save(); // salvar o usuário com a qtd disponível devolvida
		ped.save();

		flash.success("Pedido do usuario " + ped.usuario.nomeUsu + " Recusado!");
		Administradores.paginaAdmin();
	}

///// DE RECUSAR PARA CONCLUIDO DECREMENTANDO /////
	@Admin
	public static void recusarParaConcluido(Long idPedCon, String resposta) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.concluido() ... [" + new Date() + "]");

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);

		Pedido ped = Pedido.findById(idPedCon);
		Usuario user = ped.usuario;
		
		user.qtdDisponivel = user.qtdDisponivel - (ped.qtdCopias * ped.paginas); // diminuir a qtd disponível do usuário
		
		ped.atendimento = resposta;
		ped.status = StatusPedido.CONCLUIDO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		user.save(); // salvar o usuário com a qtd disponível devolvida
		ped.save();

		flash.success("Pedido do usuario " + ped.usuario.nomeUsu + " concluido!");
		listarRecusados();
	}

///// DE RECUSAR PARA CONCLUIDO DECREMENTANDO /////
	@Admin
	public static void concluidoParaRecusar(Long idPed, String resposta) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.concluido() ... [" + new Date() + "]");

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);

		Pedido ped = Pedido.findById(idPed);
		Usuario user = ped.usuario;

		user.qtdDisponivel = user.qtdDisponivel + (ped.qtdCopias * ped.paginas); // devolver a qtd disponível para o usuário
		
		ped.atendimento = resposta;
		ped.status = StatusPedido.RECUSADO;
		ped.dataAtendimento = new Date();
		ped.adm = admin;
		user.save(); // salvar o usuário com a qtd disponível devolvida
		ped.save();

		flash.success("Pedido do usuario " + ped.usuario.nomeUsu + " concluido!");
		listarConcluidos();
	}

///// LISTAR TODOS OS PEDIDO CONCLUIDOS /////
	@Admin
	public static void listarConcluidos() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.listarConcluidos() ... [" + new Date() + "]");

		String matriculaDoUsuarioFiltro = params.get("matriculaDoUsuarioFiltro");
		String nomeDoArquivoFiltro = params.get("nomeDoArquivoFiltro");

		if (matriculaDoUsuarioFiltro == null && nomeDoArquivoFiltro == null) {
			matriculaDoUsuarioFiltro = "";
			nomeDoArquivoFiltro = "";
		} else {
			System.out.println("Matricula: " + matriculaDoUsuarioFiltro);
			System.out.println("Nome do Arquivo: " + nomeDoArquivoFiltro);
		}

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;

		List<Pedido> listaconcluidos = new ArrayList<Pedido>();

		if (matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty()) {
			listaconcluidos = Pedido.find(" status = ?1 AND adm_id = ?2 ", StatusPedido.CONCLUIDO, admBanco).fetch();
			System.out.println("Tentou filtrar  pedidos concluidos sem nada, ou entrou na página concluido!");
		} else if (!matriculaDoUsuarioFiltro.isEmpty() && !nomeDoArquivoFiltro.isEmpty()) {
			listaconcluidos = Pedido
					.find("usuario.matricula like ?1 AND lower(nomeArquivo) like ?2 AND status like ?3 AND adm_id = ?4",
							"%" + matriculaDoUsuarioFiltro.trim() + "%",
							"%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%", StatusPedido.CONCLUIDO, admBanco)
					.fetch();
			System.out.println(
					"Tentou filtrar pedidos concluidos com conteudo!( Nome do Arquivo e matricula do usuario)");
		} else if (!matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty()) {
			listaconcluidos = Pedido.find("usuario.matricula like ?1 AND status like ?2 AND adm_id = ?3",
					"%" + matriculaDoUsuarioFiltro.trim() + "%", StatusPedido.CONCLUIDO, admBanco).fetch();
			System.out.println("Tentou filtrar pedidos concluidos com conteudo!(só a matricula)");
		} else if (!nomeDoArquivoFiltro.isEmpty() && matriculaDoUsuarioFiltro.isEmpty()) {
			listaconcluidos = Pedido
					.find("lower(nomeArquivo) like ?1 AND status like ?2 AND adm_id = ?3",
							"%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%", StatusPedido.CONCLUIDO, admBanco)
					.fetch();
			System.out.println("Tentou filtrar pedidos concluidos com conteudo!(só nome do arquivo)");
		}
		String telaAdmin = "Tela Admin";
		String temFiltro = "tem";
		String titulo = "Listagem de Concluídos";
		render(listaconcluidos, telaAdmin, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro, temFiltro, titulo);
	}

///// LISTAR TODOS OS PEDIDO RECUSADOS /////
	@Admin
	public static void listarRecusados() {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.listarRecusados() ... [" + new Date() + "]");

		String matriculaDoUsuarioFiltro = params.get("matriculaDoUsuarioFiltro");
		String nomeDoArquivoFiltro = params.get("nomeDoArquivoFiltro");

		if (matriculaDoUsuarioFiltro == null && nomeDoArquivoFiltro == null) {
			matriculaDoUsuarioFiltro = "";
			nomeDoArquivoFiltro = "";
		} else {
			System.out.println("Matricula: " + matriculaDoUsuarioFiltro);
			System.out.println("Nome do Arquivo: " + nomeDoArquivoFiltro);
		}

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;

		List<Pedido> listaRecusados = new ArrayList<Pedido>();

		if (matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty()) {
			listaRecusados = Pedido.find(" status = ?1 AND adm_id = ?2 ", StatusPedido.RECUSADO, admBanco).fetch();
			System.out.println("Tentou filtrar sem nada ou entrou na página!");
		} else if (!matriculaDoUsuarioFiltro.isEmpty() && !nomeDoArquivoFiltro.isEmpty()) {
			listaRecusados = Pedido
					.find("usuario.matricula like ?1 AND lower(nomeArquivo) like ?2 AND status like ?3 AND adm_id = ?4",
							"%" + matriculaDoUsuarioFiltro.trim() + "%",
							"%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%", StatusPedido.RECUSADO, admBanco)
					.fetch();
			System.out.println("Tentou filtrar com conteudo!( Nome do Arquivo e matricula do usuario)");
		} else if (!matriculaDoUsuarioFiltro.isEmpty() && nomeDoArquivoFiltro.isEmpty()) {
			listaRecusados = Pedido.find("usuario.matricula like ?1 AND status like ?2 AND adm_id = ?3",
					"%" + matriculaDoUsuarioFiltro.trim() + "%", StatusPedido.RECUSADO, admBanco).fetch();
			System.out.println("Tentou filtrar com conteudo!(só a matricula)");
		} else if (!nomeDoArquivoFiltro.isEmpty() && matriculaDoUsuarioFiltro.isEmpty()) {
			listaRecusados = Pedido
					.find("lower(nomeArquivo) like ?1 AND status like ?2 AND adm_id = ?3",
							"%" + nomeDoArquivoFiltro.trim().toLowerCase() + "%", StatusPedido.RECUSADO, admBanco)
					.fetch();
			System.out.println("Tentou filtrar com conteudo!(só nome do arquivo)");
		}
		String telaAdmin = "Tela Admin";
		String temFiltro = "tem ";
		String titulo = "Listagem de Recusados";
		render(listaRecusados, telaAdmin, admBanco, nomeDoArquivoFiltro, matriculaDoUsuarioFiltro, temFiltro, titulo);
	}

///// REALIZAR BAIXA /////
	@SuppressWarnings("deprecation")
	@Admin
	public static void realizarBaixa(Pedido ped) throws IOException {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.realizarBaixa() ... [" + new Date() + "]");

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admBanco = dadosSessaoAdmin.admin;
		if (ped.usuario == null) {
			flash.error("Selecione o Usuário do Pedido");
			Administradores.realizarPedidoCopia();
		}
		if (ped.qtdCopias == 0) {
			flash.error("A Quantidade de Copias é obrigatorio");
			Administradores.realizarPedidoCopia();
		} else if (ped.paginas == 0) {
			flash.error("a quantidade de páginas é obrigatorio");
			Administradores.realizarPedidoCopia();
		}
		
		int valor = ped.usuario.qtdDisponivel - (ped.qtdCopias * ped.paginas);
		if (valor < 0) {
			flash.error("quatidade de copia indisponivel");
			Administradores.realizarPedidoCopia();
		}
		
		ped.usuario.qtdDisponivel = valor;
		if (valor < 0) {
			flash.error("quatidade de copia indisponivel");
			Administradores.realizarPedidoCopia();
		}

		ped.nomeArquivo = "Copia do Usuário: " + ped.usuario.matricula + " " + new Date().getDate() + " "
				+ new Date().getHours() + ":" + new Date().getMinutes();
		ped.dataEnvio = new Date();
		ped.adm = admBanco;
		ped.atendimento = "Copia realizada pelo Admin: " + dadosSessaoAdmin.admin.nomeAdm;
		ped.dataAtendimento = new Date();
		ped.dataEntrega = new Date();
		ped.status = StatusPedido.ENTREGUE;
		ped.usuario.save();
		ped.save();
		try {
			FileWriter arq = new FileWriter(
					"E:\\GitHubRepositorios\\SIGICOP\\SIGICOP\\historicoDePedidos\\" + ped.id + "_relatorio.txt");
			PrintWriter gravarArq = new PrintWriter(arq);

			gravarArq.println("|________________Relatório do pedido " + ped.id + "__________________|");
			gravarArq.println("|USUÁRIO: " + ped.usuario.nomeUsu);
			System.out.println("|USUÁRIO: " + ped.usuario.nomeUsu);
			gravarArq.println("|ADMINISTRADOR: " + ped.adm.nomeAdm);
			System.out.println("|ADMINISTRADOR: " + ped.adm.nomeAdm);
			gravarArq.println("|NOME DO ARQUIVO: " + ped.nomeArquivo);
			System.out.println("|NOME DO ARQUIVO: " + ped.nomeArquivo);
			gravarArq.println("|QTD DE CÓPIAS: " + ped.qtdCopias + "                                        |");
			System.out.println("|QTD DE CÓPIAS: " + ped.qtdCopias + "                                        |");
			gravarArq.println("|FACE: " + ped.frenteVerso);
			System.out.println("|FACE: " + ped.frenteVerso);
			gravarArq.println("|DESCRIÇÃO: " + ped.descricao);
			System.out.println("|DESCRIÇÃO: " + ped.descricao);
			gravarArq.println("|DATA DE ENVIO: " + ped.dataEnvio + "       |");
			System.out.println("|DATA DE ENVIO: " + ped.dataEnvio + "       |");
			gravarArq.println("|DATA DE ATENDIMENTO: " + ped.dataAtendimento + " |");
			System.out.println("|DATA DE ATENDIMENTO: " + ped.dataAtendimento + " |");
			gravarArq.println("|DATA DE ENTREGA: " + ped.dataEntrega + "     |");
			System.out.println("|DATA DE ENTREGA: " + ped.dataEntrega + "     |");
			gravarArq.println("|STATUS: " + ped.status);
			System.out.println("|STATUS: " + ped.status);
			gravarArq.println("|___________________Fim de Relatorio_____________________|");

			arq.close();
			ped.save();
			flash.success("Pedido do usuario " + ped.usuario.nomeUsu + " entregue!");
			System.out.println("|___________________relatório criado_____________________|");
		} catch (Exception e) {
			System.out.println("ERRO AO GERAR RELATORIO DE PEDIDO ENTREGUE");
			flash.error("ERRO AO GERAR RELATORIO DE PEDIDO ENTREGUE");
			Administradores.realizarPedidoCopia();
		}

		flash.success("Pedido de copia do usuário " + ped.usuario.nomeUsu + " realizado com sucesso!");
		Administradores.realizarPedidoCopia();
		gerarRelatorio(ped);

	}

///// ENTREGAR PEDIDO /////
	@Admin
	public static void entregarPedido(Long id) throws IOException {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.entregarPedido() ... [" + new Date() + "]");
		System.out.println("id do pedido = " + id);

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador admin = Administrador.findById(dadosSessaoAdmin.admin.id);

		Pedido ped = Pedido.findById(id);
		ped.status = StatusPedido.ENTREGUE;
		ped.dataEntrega = new Date();
		ped.adm = admin;
		// gerar relatório
		gerarRelatorio(ped);

		listarConcluidos();
	}

///// GERAR RELATÓRIO DO PEDIDO APÓS DE ENTREGUE /////
	@Admin
	public static void gerarRelatorio(Pedido ped) throws IOException {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.geraRelatorio() ... [" + new Date() + "]");

		System.out.println("|__________________Gerando relatório...__________________|");
//			Historico historico = new Historico();
		try {
			FileWriter arq = new FileWriter(
					"E:\\GitHubRepositorios\\SIGICOP\\SIGICOP\\historicoDePedidos\\" + ped.id + "_relatorio.txt");
			PrintWriter gravarArq = new PrintWriter(arq);

			gravarArq.println("|________________Relatório do pedido " + ped.id + "__________________|");
			gravarArq.println("|USUÁRIO: " + ped.usuario.nomeUsu);
			System.out.println("|USUÁRIO: " + ped.usuario.nomeUsu);
			gravarArq.println("|ADMINISTRADOR: " + ped.adm.nomeAdm);
			System.out.println("|ADMINISTRADOR: " + ped.adm.nomeAdm);
			gravarArq.println("|NOME DO ARQUIVO: " + ped.nomeArquivo);
			System.out.println("|NOME DO ARQUIVO: " + ped.nomeArquivo);
			gravarArq.println("|QTD DE CÓPIAS: " + ped.qtdCopias + "                                        |");
			System.out.println("|QTD DE CÓPIAS: " + ped.qtdCopias + "                                        |");
			gravarArq.println("|FACE: " + ped.frenteVerso);
			System.out.println("|FACE: " + ped.frenteVerso);
			gravarArq.println("|DESCRIÇÃO: " + ped.descricao);
			System.out.println("|DESCRIÇÃO: " + ped.descricao);
			gravarArq.println("|DATA DE ENVIO: " + ped.dataEnvio + "       |");
			System.out.println("|DATA DE ENVIO: " + ped.dataEnvio + "       |");
			gravarArq.println("|DATA DE ATENDIMENTO: " + ped.dataAtendimento + " |");
			System.out.println("|DATA DE ATENDIMENTO: " + ped.dataAtendimento + " |");
			gravarArq.println("|DATA DE ENTREGA: " + ped.dataEntrega + "     |");
			System.out.println("|DATA DE ENTREGA: " + ped.dataEntrega + "     |");
			gravarArq.println("|STATUS: " + ped.status);
			System.out.println("|STATUS: " + ped.status);
			gravarArq.println("|___________________Fim de Relatorio_____________________|");

			arq.close();
			ped.save();
			flash.success("Pedido do usuario " + ped.usuario.nomeUsu + " entregue!");
			System.out.println("|___________________relatório criado_____________________|");
		} catch (Exception e) {
			System.out.println("ERRO AO GERAR RELATORIO DE PEDIDO ENTREGUE");
			flash.error("ERRO AO GERAR RELATORIO DE PEDIDO ENTREGUE");
			listarConcluidos();
		}
		listarConcluidos();
	}

///// DOWNLOAD DE RELATÓRIO /////
	@User
	public static void downloadRelatório(Long id) throws IOException {
		  File file = new File("E:\\GitHubRepositorios\\SIGICOP\\SIGICOP\\historicoDePedidos\\" +id+ "_relatorio.txt");	
		  if(!file.exists()) {
			  flash.error("Arquivo não encontrado ou não existe");
		  Usuarios.paginaUsuario();
		  }
	renderBinary(file, id+"_relatorio.txt");
	}

///// APAGAR TODOS OS PEDIDOS COM O STATUS ENTREGUE OU RECUSADO /////
	@Admin
	public static void apagarPedidoEntregueOuRecusado(StatusPedido status) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.apagarPedidoEntregueOuRecusado() ... [" + new Date() + "]");

		DadosSessaoAdmin dadosSessaoAdmin = Cache.get(session.getId(), DadosSessaoAdmin.class);
		Administrador adminSessao = dadosSessaoAdmin.admin;

		if (adminSessao.admPadrao) {
			List<Pedido> listaPed = Pedido.find("status = ?1", status).fetch();

			if (listaPed.isEmpty()) {
				if(status == StatusPedido.ENTREGUE) {
					flash.error("Ainda não possui pedido com o status " + status);
				listarConcluidos();
				}else if(status == StatusPedido.RECUSADO){
					flash.error("Ainda não possui pedido com o status " + status);
				listarRecusados();
				}
			} else {
				for (int i = 0; i < listaPed.size(); i++) {
					if(listaPed.get(i).status == StatusPedido.ENTREGUE) {
						File file = new File("E:\\GitHubRepositorios\\SIGICOP\\SIGICOP\\historicoDePedidos\\" + listaPed.get(i).id + "_relatorio.txt");
						file.delete();
					}
					listaPed.get(i).delete();
				}
				if(status == StatusPedido.ENTREGUE) {
					flash.success("Todos os pedidos com o status " + status + " foram apagados com sucesso");
				listarConcluidos();
				}else if(status == StatusPedido.RECUSADO){
					flash.success("Todos os pedidos com o status " + status + " foram apagados com sucesso");
				listarRecusados();
				}
			}
		} else {
			flash.error("Acesso restrito ao administrador padrao do sistema");
			Administradores.paginaAdmin();
		}
	}
	
///// PARA O USUÁRIO FAZER O DOWNLOAD DO ARQUIVO DO PEDIDO /////
	@User
	public static void downloadUser(Long id) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.downloadUser() ... ["+ new Date()+"]");
		Pedido ip = Pedido.findById(id);
		
		if(!ip.arquivo.exists()) {
			flash.error("Arquivo não encontrado");
			Usuarios.paginaUsuario();
		}
		System.out.println("pedido download: "+ ip.nomeArquivo);
	renderBinary(ip.arquivo.getFile(), ip.nomeArquivo);
	}
	
///// PARA O ADMINISTRODOR FAZER O DOWNLOAD DO ARQUIVO DO PEDIDO  /////
	@Admin
	public static void downloadAdmin(Long id) {
	System.out.println("_____________________________________________________________________________________");
	System.out.println("Pedidos.downloadAdmin() ... ["+ new Date()+"]");
		Pedido ip = Pedido.findById(id);
		
		if(!ip.arquivo.exists()) {
			flash.error("Este arquivo não foi encontrado");
			Administradores.paginaAdmin();
		}
		System.out.println("pedido download: "+ ip.nomeArquivo);
	renderBinary(ip.arquivo.getFile(), ip.nomeArquivo);
	}
	
///// ADICIONA PEDIDOS NA CACHE COM AJAX/////
	@User
	public static void addPedidoAjax(@Valid Pedido item) {
		System.out.println("_____________________________________________________________________________________");
		System.out.println("Pedidos.addPedido() ...[" + new Date() + "]");

		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = null;

		if (dadosDeSessao == null) {
			dadosDeSessao = new DadosSessao();
		} else {
			listaDePedidos = dadosDeSessao.listaDePedidos;
		}
		if (listaDePedidos == null) {
			listaDePedidos = new ArrayList<Pedido>();
		}
		int valor;
		if (item.frenteVerso.equals("frenteEverso")) { // se for frenteEverso multliplica a qtd de cópias por 2
			valor = dadosDeSessao.usuario.qtdDisponivel - (item.qtdCopias * 2);
//			if (valor < 0) { // se o valor da qtd disponível for menor que 0 então a qtd está indisponível
//				flash.error("quatidade de copia indisponivel");
//				solicitar();
//			}
			dadosDeSessao.usuario.qtdDisponivel = valor;
			Cache.set(session.getId(), dadosDeSessao);
		} else {
			valor = dadosDeSessao.usuario.qtdDisponivel - item.qtdCopias;
//			if (valor < 0) { // se o valor da qtd disponível for menor que 0 então a qtd está indisponível
//				flash.error("quatidade de copia indisponivel");
//				solicitar();
//			}
			dadosDeSessao.usuario.qtdDisponivel = valor; // setar a qtd disponível atualizada no usuário da cache
			Cache.set(session.getId(), dadosDeSessao);
		}
		// if (validation.hasErrors()) {
		// params.flash();
		// flash.error("Falha no Cadastro do Pedido!");
		// flash.keep();
		// solicitar();
		// }

		String nomeArq = params.get("name"); // recebe o nome do arquivo

//		if (item.arquivo == null || nomeArq == null) { // vereficar se o arquivo existe
//			flash.error("O Envio do Arquivo é obrigatorio");
//			solicitar();
//		} else if (item.qtdCopias == 0) { // vereficar se a qtdCopias é 0
//			flash.error("A Quantidade de Copias é obrigatorio");
//			solicitar();
//		} else if (item.frenteVerso == null) { // vereficar frente ou frenteEverso foi escolhido
//			flash.error("Frente ou FrenteEVerso é obrigatorio");
//			solicitar();
//		}
		// idLista serve para poder listar, adicionar e remover os pedidos da Cache
		int idLista = 0;
		if (listaDePedidos.size() <= 0) { // na primeira vez id lista recebe 1
			idLista = 1;
		} else {
			// serve para quando o usuário apagar um pedido que está no meio da listagem
			Pedido ultimoPedido = new Pedido();
			for (int i = 0; i < dadosDeSessao.listaDePedidos.size(); i++) {
				ultimoPedido = dadosDeSessao.listaDePedidos.get(i);
			}
			// pegar o idLista do ultimo pedido e soma mais 1 para o proximo
			idLista = ultimoPedido.idLista + 1;
		}
		item.idLista = idLista; // o idLista do item recebe o idLista do servidor

		item.nomeArquivo = nomeArq; // o item recebe o nome do arquivo
		item.usuario = dadosDeSessao.usuario; // o item recebe o usuário da cache

		listaDePedidos.add(item); // item adicionado na lista

		dadosDeSessao.listaDePedidos = listaDePedidos;
		
		// lista adicionada na cache
		Cache.set(session.getId(), dadosDeSessao);
		
		Gson gson = new Gson();
		String lista = gson.toJson(listaDePedidos);
	 
	 renderJSON(lista);
	}
}
