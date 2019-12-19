# SIGICOP
Sistema de Gerenciamento de Impressões e Copias

## Descrição:
**A SIGICOP é um projeto de conclusão do curso subsequente em informática,
 realizado por Manacio Pereira de Souza ([@Manacio](https://github.com/Manacio)) e por Magdiel Pereira de Souza ([@MagdielPS](https://github.com/MagdielPS)).
 A SIGICOP é um sistema de gerenciamento de cópias e impressões,
 que facilite e agilize a demanda de impressões e cópias do IFRN-*CAMPUS*-JC,
 onde o usuário possa ter controle sobre a quantidade de solicitação disponíveis e o administrdor possar gerenciar as solicitacões**

## PASSO A PASSO

* O usuário deverá acessar o site da SIGICOP;
* Quando visitar a página principal clicar em "Logar" ou em "logar agora";
* Ao entrar na página de login, fornecer o login e senha para entrar no sistema;
* caso as credenciais seja de usuário comum, ele poderá visualizar seus pedidos
 anteriores e poder filtra-los, e poderá e visualizar a quantidade disponível de 
solicitações, se não for zero perderá realizar mais;
* Se as credenciais for de um administrador, ele poderá visualizar todos pedidos 
solicitados e filtra-los, poderá concluir e recusar, se recusar será obrigado à dar uma justificativa,
 também poderá ver e editar seus dados;
* Se o administrador for "padrão" poderá adicionar mais administradores e remover-los;

## As importações mais importantes utlizado nos templates layPri.html e laySeg.html:

[**Jquery**](https://jquery.com/)
```html
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.11/jquery.mask.min.js"></script>
```

[**Bootstrap**](https://getbootstrap.com/docs/4.4/getting-started/introduction/)
```html
<link rel="stylesheet" type="text/css" href="@{'/public/bootstrap/css/bootstrap.css'}">
  
<link rel="stylesheet" media="screen" href="@{'/public/stylesheets/meuCss/cssPersonalizado.css'}">   
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/meuCss.css'}">
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script src="@{'/public/bootstrap/js/bootstrap.bundle.min.js'}"></script>
```

[**Semantic**](https://semantic-ui.com/introduction/getting-started.html)
```html
<script src="@{'/public/Semantic/js/semantic.js'}"/></script>
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/icon.css'}">
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/grid.css'}"><!-- rodape utilizar-->
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/header.css'}"><!-- utilizado em loginUser e logarAdm  -->
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/segment.css'}"><!-- rodape utilizar-->
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/button.css'}">
```
### Javascript Incorporado no Templates laySeg.html
**Mensagem de Alerta**
```javascript
<!-- MESSAGEM DE ALERTA -->
<script type="text/javascript">
$().ready(function() {
		setTimeout(function () {
			$('#messagemAlerta').hide(); // "messagemAlerta" ï¿½ o id do elemento que seja manipular.
		}, 2000); // O valor ï¿½ representado em milisegundos.
});
$().ready(function() {
	setTimeout(function () {
		$('#messagemArquivo').hide(); // "messagemAlerta" ï¿½ o id do elemento que seja manipular.
	}, 2000); // O valor ï¿½ representado em milisegundos.
});
</script>
```
**Mascara de Campos**
```javascript
<!-- MASCARA DE CAMPOS -->
<script type="text/javascript">
    $("#telefone, #celular").mask("(00) 0000-0000");
    $('#matricula').mask('00000000000000');
    $('#qtdCopiasFiltro').mask('00');
    $('#dataEnvio').mask('00/00/0000');
    $('#dataEnvtrega').mask('00/00/0000');
   
</script>
   ```
**Dialog do [SweetAlert2](https://sweetalert2.github.io/)**

**Dados do usuário e do Admin**
   ```javascript
<!-- DIALOGS DO SWEETALERT2 -->
<!-- DIALOGS COM DADOS DO USUARIO E ADMIN -->
<script type="text/javascript">
    $("#dadosAdmin").click(function () {
		Swal.fire({
			  position: 'top-end',
			  icon: '',
			  title: 'Meus Dados <br> <a href="@{Administradores.editar}" class="btn primary ml-5" style="background-color: #529fed;">Editar</a>',
			  footer: 'Administrador: ${admBanco?.nomeAdm} <br> Email: ${admBanco?.email} <br> #{if admBanco?.ultimoAcesso} Ultimo acesso: ${admBanco?.ultimoAcesso?.format("dd/MM/yyyy HH:mm:ss")} #{/if} ' ,
			  showCloseButton: true,
			  showConfirmButton: false,
			})
	});
    
    $("#dadosUser").click(function () {
		Swal.fire({
			  position: 'top-end',
			  icon: '',
			  title: 'Meus Dados',
			  footer: 'Usuario: ${usuarioBanco?.nomeUsu} <br> Email: ${usuarioBanco?.email} <br>  Quantidade de Copias disponiveis: ${usuarioBanco?.qtdDisponivel} #{if usuarioBanco?.ultimoAcessoUsu}   <br> Ultimo acesso: ${usuarioBanco?.ultimoAcessoUsu?.format("dd/MM/yyyy HH:mm:ss")}#{/if}' ,
			  showCloseButton: true,
			  showConfirmButton: false,
			})
	});
 </script>
```
**Dialog Mensagem de Restaurar**
```javascript
<!-- DIALOGS DO COM AVISOS E/OU COM PREENCHIMENTO DE MENSAGENS -->
 <script type="text/javascript">
	$("#restaurar").click(function() {
		Swal.fire({
			  title: 'Tem certeza que deseja restaurar a disponibilidade de solicitaÃ§Ãµes de todos os usuÃ¡rios?',
			  text: 'lembre-se que essa funÃ§Ã£o sÃ³ Ã© recomendada em inicio de cada mÃªs',
			  icon: 'question',
			  showCancelButton: true,
			  confirmButtonColor: '#21ac0d',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'Ok',
			  showCloseButton: true,
			}).then((result) => {
			  if (result.value) {
			$('#formRestaurar').submit(); 
			  }
			})
	});
	
	
</script>
```
**Dialogs com Preenchimento de Justificativa de Conclusão ou recusa**
```javascript
<script type="text/javascript">
$(document).ready(function(){
	$(".recusar").click(function() {
		var idPedido = $(this).data("item");
		
		Swal.fire({
			  title: 'Motivo da recusa:',
			  input: 'textarea',
			  inputPlaceholder: 'descreva o motivo do pedido ser recusado',
			  inputAttributes: {
			    'aria-label': 'Type your message here'
			  },
			  showCloseButton: true,
			  showCancelButton: true
		}).then((result) => {
			if(result.value == ""){
				Swal.fire({
					  icon: 'error',
					  title: 'ERROR...',
					  text: 'preencha o motivo!',
					  footer: '<a href>Limpar e tentar novamente</a>',
					  showCloseButton: true
					})
								}
			if (result.value) {
				$('#idPed').val(idPedido);
				$('#motivo').val(result.value);
				$('#recusa').submit();
			}
		})
	});

	$(".concluir").click(function() {
		var idPedido = $(this).data("item");
		
		Swal.fire({
			  title: 'Mensagem de conclusÃ£o:',
			  input: 'textarea',
			  inputPlaceholder: 'digite alguma mensagem complementar',
			  inputAttributes: {
			    'aria-label': 'Type your message here'
			  },
			  showCloseButton: true,
			  showCancelButton: true
		}).then((result) => {
			if(result.value == ""){
				result.value = ".";
				}
			if (result.value) {
				$('#idPedCon').val(idPedido);
				$('#resposta').val(result.value);
				$('#con').submit();
			}
		})
	});
});
</script>
```
**Reload Automatico de Página**
```javascript
<script type="text/javascript">
	$(document).ready(function () {
	    setTimeout(function () {
	        window.location.reload(1);
	    }, 180000); //tempo em milisegundos. Neste caso, o refresh vai acontecer de 5 em 5 segundos.
	});
</script>
```
**Mensagem ao passar o mouse no link**
```javascript
<script type="text/javascript">
$(function () {
		  $('[data-toggle="tooltip"]').tooltip()
		  $('#example').tooltip({ boundary: 'window' })
		})
</script>
```
### Javascript Incorporado no Templates layPri.html

**Voltar ao Topo**
```javascript
<!-- VOLTAR AO TOPO -->
<script type="text/javascript">
$(document).ready(function(){
    $(window).scroll(function(){
        if ($(this).scrollTop() > 500) {
            $('a[href="#top"]').fadeIn();
        } else {
            $('a[href="#top"]').fadeOut();
        }
    });
    $('a[href="#top"]').click(function(){
        $('html, body').animate({scrollTop : 0},800);
        return false;
    });

});
</script> 
```
**Sobre Nós**
```javascript
<script type="text/javascript">
$(document).ready(function(){ 
		$(".sobreNos").click(function () {
	Swal.fire({
		  position: 'top-center',
		  icon: 'information',
		  title: '<div  class=sticky-top> <img src=/public/images/icones/gitInfo.png height=30 style=float: left; margin-right: 5px; alt=GitHub title=GitHub /> <spanclass=text-white h4>SIGICOP</h4> </span>',
		  showCloseButton: true,
		  showConfirmButton: false,
		})
}); 
</script> 
```
# Páginas do SIGICOP para Usuário
![Webp net-gifmaker](https://user-images.githubusercontent.com/55263599/71113258-f7e00780-21ab-11ea-8106-f04db75df755.gif)

# Páginas do SIGICOP para Administrador

## Tabela de Referências
Referências    | Categoria   | Links
-------------- | :---------: | ----
Play Framework | Plataforma de Desenvolvimento |  [1.4.5](https://www.playframework.com/documentation/1.4.x/home) 
Bootstrap      | CSS | [Bootstrap](https://getbootstrap.com/docs/4.4/getting-started/introduction/)
Semantic UI      | CSS |  [Semantic](https://semantic-ui.com/introduction/getting-started.html) 
Uikit          | CSS | [Uikit](https://getuikit.com/docs/introduction) 
Sweeetalert2     | JS |  [Sweeetalert2](https://sweetalert2.github.io/)
Jquery          | JS |  [Jquery](https://jquery.com/)
W3school       | Plataforma de Conhecimento |  [W3school](https://www.w3schools.com/)
Icon8          | Plataforma de Icones | [Icons8](https://icons8.com.br/icons)
 
