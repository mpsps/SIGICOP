package controllers;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import play.data.validation.Valid;

import models.DadosSessao;
import models.Pedido;
import models.Usuario;
import play.cache.Cache;
import play.db.jpa.JPABase;
import play.mvc.Controller;
import play.mvc.With;
import segurancaDoSistema.Seguranca;

@With(Seguranca.class)
public class Pedidos extends Controller {
	// TELA DE FAZER PEDIDO, RECEBE O ID DO USUARIO LOGADO
	public static void fazerPedido() {
		
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = dadosDeSessao.listaDePedidos;
		dadosDeSessao.listaDePedidos = listaDePedidos; 
		String voltar = "voltar";
		Usuario usuarioBanco = dadosDeSessao.usuario;
		render(listaDePedidos, voltar, usuarioBanco);
	}

	public static void addPedido(@Valid Pedido item) {
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
//		int qtdDisp = item.usuario.qtdDisponivel;
//		int qtdCop = item.qtdCopias;
//		if(item.frenteVerso.equals("frenteEverso")) {
//			int valor = qtdDisp - (qtdCop * 2);
//			if(valor < 0) {
//				flash.error("quatidade de copia indisponivel");
//				fazerPedido();
//			}
//		}else {
//			int valor = qtdDisp - qtdCop;
//			if(valor < 0) {
//				flash.error("quatidade de copia indisponivel");
//				fazerPedido();
//			}
//		}
		
		item.nomeArquivo = nomeArq;
		item.usuario = dadosDeSessao.usuario;
		
		listaDePedidos.add(item);
		
		dadosDeSessao.listaDePedidos = listaDePedidos;
		Cache.set(session.getId(), dadosDeSessao);
		flash.success("Pedido Listado!");
		fazerPedido();
	}
	
	// SALVAR PEDIDO(S)
	public static void salvar() {
		
		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos;
		
		
		for (int i = 0; i < dadosDeSessao.listaDePedidos.size(); i++) {
			
			dadosDeSessao.listaDePedidos.get(i).dataEnvio = new Date();
			
		}
		listaDePedidos	= dadosDeSessao.listaDePedidos;
		
		for (int i = 0; i < listaDePedidos.size(); i++) {
			
			
			listaDePedidos.get(i).save();
			
		}
		dadosDeSessao.listaDePedidos = null;
		flash.success("Pedido(s) salvo(s)!");
		fazerPedido();
	}
	
	// APAGAR O PEDIDO DO BANCO DE DADOS
	public static void cancelarPedidos(Long idPedido) {

		DadosSessao dadosDeSessao = Cache.get(session.getId(), DadosSessao.class);
		List<Pedido> listaDePedidos = dadosDeSessao.listaDePedidos;
		
		for (int i = 0; i <dadosDeSessao.listaDePedidos.size(); i++) {

			if (listaDePedidos.get(i).id == idPedido) {

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

	
}
