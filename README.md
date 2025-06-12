# SIGICOP 
Sistema de Gerenciamento de Impressões e Cópias

## Descrição:
**O SIGICOP é um projeto de conclusão do curso subsequente em informática,
 realizado por Magdiel Pereira de Souza ([@Magdiel :octocat:](https://github.com/mpsps))) e Manacio Pereira de Souza ([@Manacio :octocat:](https://github.com/Manacio)).
 O SIGICOP é um sistema de gerenciamento de cópias e impressões,
 que facilite e agilize a demanda de impressões e cópias do IFRN-CAMPUS-JC,
 onde o usuário possa ter controle sobre a quantidade de solicitação disponíveis e o administrador possar gerenciar as solicitações**

# Diagrama de Classes :page_facing_up:
![Diagrama_Classes_UML](https://user-images.githubusercontent.com/55263599/114252697-f9bf7280-997c-11eb-965b-60d16d5a1228.jpg)

# Diagrama de Caso de Uso do Usuário ![usu](https://user-images.githubusercontent.com/55263599/71699011-6f14be80-2d9c-11ea-9657-b5e8f9ab43d9.png)
![User Caso de Uso](https://user-images.githubusercontent.com/55263599/100518868-24015800-3173-11eb-9086-260e89b975af.jpg)

# Diagrama de Caso de Uso do Administrador ![adm](https://user-images.githubusercontent.com/55263599/71698974-48ef1e80-2d9c-11ea-9963-3d43d39500d5.png)
![Operador-Adm Caso de Uso](https://user-images.githubusercontent.com/55263599/100758345-2b706d80-33ce-11eb-8e1b-1522cedf80ca.jpg)

# PASSO A PASSO DO USUÁRIO ![usu](https://user-images.githubusercontent.com/55263599/71699011-6f14be80-2d9c-11ea-9657-b5e8f9ab43d9.png)

* O Usuário deverá acessar o site da SIGICOP;
* Quando visitar a página principal clicar em "Entrar" ou em "Entrar agora";
* Se caso o Usuário ainda não seja cadastrado, O cadasto será realizado na sala da Coordenação de Apoio Acadêmico (COAPAC). Dados a ser fornecido, são: Nome completo, matrícula, email e senha (pode ser alterada após cadastramento);
* Ao entrar na página de login, fornecer o login e senha para entrar no sistema;
* O Usuário ao logar, poderá visualizar seus pedidos anteriores (se houver), e poderá filtra-los e efetuar download;
* Em "Meus Dados" (icone de usuário) poderá visualizar a quantidade disponível de solicitações, clicando poderá vizualizar seu nome, email e ultimo acesso;
* Poderá solicitar pedidos de impressões, ao solicitar, se houver solicitacão disponivel, aparecerá o formulário, se não, aparecerá mensagem informando que não é possivel efetuar nenhuma solicitacão;
* Ao solicitar, os pedido serão listado ao lado do formulário, onde o Usuário poderá cancelar e salvar quando quiser;
* O Usuário poderá ver os históricos de pedidos anteriores finalizados;
* Após logar, o Usuário poderá sair a qualquer momento;

## Páginas do SIGICOP para Usuário :computer:
![demostração_Usuario](https://user-images.githubusercontent.com/55263599/113459595-64583780-93ec-11eb-9154-94712c88bc57.gif)

## Páginas do SIGICOP para Usuário - Versão Mobile :iphone:
![demostração_Usuario_cell](https://user-images.githubusercontent.com/55263599/113459615-6b7f4580-93ec-11eb-8c40-c1aa0a8bc1dc.gif)

# PASSO A PASSO DO ADMINISTRADOR ![adm](https://user-images.githubusercontent.com/55263599/71698974-48ef1e80-2d9c-11ea-9963-3d43d39500d5.png)

* O Administrador deverá acessar o site da SIGICOP;
* Quando visitar a página principal clicar em "Logar" ou em "logar agora";
* Ao entrar na página de login, fornecer o login e senha para entrar no sistema;
* O Administrador ao logar, poderá visualizar todos os pedidos com o status "AGUARDANDO" (se houver), filtra-los e efetuar download;
* Em "Meus Dados" (icone do Administrador) poder visualizar seu nome, email e ultimo acesso, e poderá editar seus dados;
* O Administrador poderá concluir ou recusar os pedidos (se houver), se recusar será obrigado a dá uma justificativa, se concluir, será opcional a justificativa;
* O Administrador poderá listar todos os pedidos concluídos, e poderá filtra-los;
* O Administrador poderá listar todos os pedidos recusados, e poderá filtra-los;
* O Administrador em lista de concluído, poderá entregar o pedido depois que o Usuário recebe a impressão;
* O Administrador poderá realizar baixa para o Usuário, quando for pedido de cópia;
* O Administrador padrão poderá cadastrar novos Administradores;
* O Administrador padrão poderá listar todos os Administradores do sistema e remover-los;
* O Administrador padrão poderá restaurar a quantidade de solicitações de todos os Usuários, informando a quantidade;
* O Administrador poderá ver os históricos de pedidos anteriores finalizados;
* Ao logar, o Administrador poderá sair a qualquer momento;

## Páginas do SIGICOP para Administrador :computer:
![demostração_Admin](https://user-images.githubusercontent.com/55263599/113459634-7508ad80-93ec-11eb-9748-f8853ada8bbc.gif)

## Páginas do SIGICOP para Administrador - Versão Mobile :iphone: 
![demostração_Admin_cell](https://user-images.githubusercontent.com/55263599/113459646-7afe8e80-93ec-11eb-9252-46a016764cec.gif)

## As importações mais importantes :exclamation: :

[*Jquery*](https://jquery.com/) ([laySeg.html](https://github.com/MagdielPS/SIGICOP/blob/master/SIGICOP/app/views/laySeg.html))
```html
<!-- JS JQUERY -->
		<script src="@{'/public/javascripts/Jquery/jquery-3.4.1.js'}" type="text/javascript" charset="${_response_encoding}"></script>		
		<script src="@{'/public/javascripts/Jquery/jquery.mask.js'}" type="text/javascript" charset="${_response_encoding}"></script>
```

[*Bootstrap*](https://getbootstrap.com/docs/4.4/getting-started/introduction/) ([layPri.html](https://github.com/MagdielPS/SIGICOP/blob/master/SIGICOP/app/views/layPri.html) e  [laySeg.html](https://github.com/MagdielPS/SIGICOP/blob/master/SIGICOP/app/views/laySeg.html))
```html
<!-- BOOTSTRAP - CSS -->
		<link rel="stylesheet" type="text/css" href="@{'/public/bootstrap/css/bootstrap.css'}">
<!-- BOOTSTRAP JS -->
		<script src="@{'/public/bootstrap/js/bootstrap.bundle.min.js'}"></script>
 ``` 

[*Semantic*](https://semantic-ui.com/introduction/getting-started.html) ([layPri.html](https://github.com/MagdielPS/SIGICOP/blob/master/SIGICOP/app/views/layPri.html) e [laySeg.html](https://github.com/MagdielPS/SIGICOP/blob/master/SIGICOP/app/views/laySeg.html))
```html
<!--SEAMNTIC CSS -->
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/reset.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/site.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/container.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/grid.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/header.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/menu.min.css'}">		
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/divider.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/segment.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/button.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/icon.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/sidebar.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/transition.min.css'}">
		<link rel="stylesheet" type="text/css" href="@{'/public/stylesheets/SIGICOP.css'}">
		
<!-- SEMANTIC JS -->
		<script src="@{'/public/Semantic/js/jquery.min.js'}"></script>
		<script src="@{'/public/Semantic/js/visibility.min.js'}"></script>
		<script src="@{'/public/Semantic/js/sidebar.min.js'}"></script>
		<script src="@{'/public/Semantic/js/transition.min.js'}"></script>
		<script src="@{'/public/Semantic/js/meuJs.js'}"></script>
```

[*Sweetalert*](https://sweetalert2.github.io/) ([layPri.html](https://github.com/MagdielPS/SIGICOP/blob/master/SIGICOP/app/views/layPri.html) e [laySeg.html](https://github.com/MagdielPS/SIGICOP/blob/master/SIGICOP/app/views/laySeg.html))
```html
<!-- SWEETALERT2 -->
 		<script src="@{'/public/javascripts/meuJs/sweetalert2@9.js'}"></script>
```
## Tabela de Referências de Sites :link::
Referências    | Categoria   | Links
-------------- | :---------: | ----
Play Framework | Plataforma de Desenvolvimento |  [1.4.5](https://www.playframework.com/documentation/1.4.x/home)
MySQL          | BD          |  [MySQL](https://www.mysql.com/products/workbench/) 
Bootstrap      | CSS         |  [Bootstrap](https://getbootstrap.com/docs/4.4/getting-started/introduction/)
Semantic UI    | CSS         |  [Semantic](https://semantic-ui.com/introduction/getting-started.html) 
Sweeetalert2   | JS          |  [Sweeetalert2](https://sweetalert2.github.io/)
Animate        |CSS-ANIMAÇÃO |  [Animate](https://daneden.github.io/animate.css/)
Jquery         | JS          |  [Jquery](https://jquery.com/)
W3school       | Plataforma de Conhecimento |  [W3school](https://www.w3schools.com/)
Icon8          | Plataforma de Icones | [Icons8](https://icons8.com.br/icons)
Trello         | Plataforma de Organização do Projeto | [Projeto TCC (SIGICOP)](https://trello.com/b/bKk0fVbE/projeto-tcc-sigicop)

## Apresentação do Trabalho de Conclusão de Curso em Informática:
[![Apresentação do TCC](http://img.youtube.com/vi/T7Z6dKuT03E/0.jpg)](http://www.youtube.com/watch?v=T7Z6dKuT03E "Apresentação do TCC")
