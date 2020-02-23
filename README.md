# SIGICOP 
Sistema de Gerenciamento de Impressões e Copias

## Descrição:
**A SIGICOP é um projeto de conclusão do curso subsequente em informática,
 realizado por Manacio Pereira de Souza ([@Manacio :octocat:](https://github.com/Manacio)) e por Magdiel Pereira de Souza ([@MagdielPS :octocat:](https://github.com/MagdielPS)).
 A SIGICOP é um sistema de gerenciamento de cópias e impressões,
 que facilite e agilize a demanda de impressões e cópias do IFRN-CAMPUS-JC,
 onde o usuário possa ter controle sobre a quantidade de solicitação disponíveis e o administrdor possar gerenciar as solicitacões**

# Diagrama de Classes :page_facing_up:
![DiagramaUMLClasses](https://user-images.githubusercontent.com/55263575/74077291-559b0e00-49fd-11ea-8b8e-d56638a05e86.jpg)

# Diagrama de Caso de Uso do Usuário ![usu](https://user-images.githubusercontent.com/55263599/71699011-6f14be80-2d9c-11ea-9657-b5e8f9ab43d9.png)

![Caso_de_Uso_Usuario](https://user-images.githubusercontent.com/55263599/73952983-7a4c9400-48de-11ea-91ec-7fe424556a6f.jpg)

# Diagrama de Caso de Uso do Administrador ![adm](https://user-images.githubusercontent.com/55263599/71698974-48ef1e80-2d9c-11ea-9963-3d43d39500d5.png)
![Caso_de_uso_Administrador](https://user-images.githubusercontent.com/55263599/73953103-b122aa00-48de-11ea-90f7-3f694b19540e.jpg)


# PASSO A PASSO DO USUÁRIO ![usu](https://user-images.githubusercontent.com/55263599/71699011-6f14be80-2d9c-11ea-9657-b5e8f9ab43d9.png)

* O usuário deverá acessar o site da SIGICOP;
* Quando visitar a página principal clicar em "Entrar" ou em "Entrar agora";
* Se caso o usuário ainda não for cadastrado, deverá se cadastrar clicando em "Cadastra-se", preencher o formulário e salvar os dados;
* Ao entrar na página de login, fornecer o login e senha para entrar no sistema;
* O usuário ao logar, poderá visualizar seus pedidos anteriores (se houver), e poderá filtra-los e efetuar download;
* Em "Meus Dados" (icone de usuário) poderá visualizar a quantidade disponível de solicitações, clicando poderá vizualizar seu nome, email e ultimo acesso;
* Poderá solicitar pedidos de impressões, ao solicitar, se houver solicitacão disponivel, aparecerá o formulário, se não, aparecerá mensagem em vermelho informando que não é possivel efetuar nenhuma solicitacão;
* Ao solicitar, os pedido serão listado ao lado do formulário, onde o usuário poderá cancelar e salvar quando quiser;
* Após logar, o usuário poderá sair a qualquer momento;

## Páginas da SIGICOP para Usuário :computer:
![SIGICOP_USUARIO_COMPUTADOR](https://user-images.githubusercontent.com/55263599/74994975-4faa2180-542e-11ea-9496-7cd99505dd2b.gif)

## Páginas da SIGICOP para Usuário - Versão Mobile :iphone: (260 x 450)
![CELULAR_USUARIO](https://user-images.githubusercontent.com/55263599/75049646-57f37280-54a9-11ea-9598-c4dd39cd6dbc.gif)

# PASSO A PASSO DO ADMINISTRADOR ![adm](https://user-images.githubusercontent.com/55263599/71698974-48ef1e80-2d9c-11ea-9963-3d43d39500d5.png)

* O administrador deverá acessar o site da SIGICOP;
* Quando visitar a página principal clicar em "Logar" ou em "logar agora";
* Ao entrar na página de login, fornecer o login e senha para entrar no sistema;
* O administrador ao logar, poderá visualizar todos os pedidos com o status "AGUARDANDO" (se houver), filtra-los e efetuar download;
* Em "Meus Dados" (icone do administrador) poder visualizar seu nome, email e ultimo acesso, e poderá editar seus dados;
* O administrador poderá concluir ou recusar os pedidos (se houver), se recusar será obrigado a dá uma justificativa, se concluir, será opcional a justificativa;
* O administrador poderá listar todos os pedidos concluídos, e poderá filtra-los;
* O administrador poderá listar todos os pedidos recusados, e poderá filtra-los;
* O administrador em lista de concluído, poderá entregar o pedido depois que o usuário recebe a impressão;
* O administrador poderá realizar baixa para o usuário, quando for pedido de cópia;
* O administrador padrão poderá cadastrar novos administradores;
* O administrador padrão poderá listar todos os administradores do sistema e remover-los;
* O administrador padrão poderá restaurar a quantidade de solicitações de todos os usuários, informando a quantidade;
* Ao logar, o administrador poderá sair a qualquer momento;

## Páginas da SIGICOP para Administrador :computer:
![Webp net-gifmaker](https://user-images.githubusercontent.com/55263599/71113258-f7e00780-21ab-11ea-8106-f04db75df755.gif)

## Páginas da SIGICOP para Administrador - Versão Mobile :iphone: (260 x 450)
![Webp net-gifmaker](https://user-images.githubusercontent.com/55263599/71113258-f7e00780-21ab-11ea-8106-f04db75df755.gif)

## As importações mais importantes utlizado nos templates layPri.html e laySeg.html :exclamation: :

[*Jquery*](https://jquery.com/)
```html
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.11/jquery.mask.min.js"></script>
```

[*Bootstrap*](https://getbootstrap.com/docs/4.4/getting-started/introduction/)
```html
<link rel="stylesheet" type="text/css" href="@{'/public/bootstrap/css/bootstrap.css'}">

<link rel="stylesheet" media="screen" href="@{'/public/stylesheets/meuCss/cssPersonalizado.css'}">   
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/meuCss.css'}">
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script src="@{'/public/bootstrap/js/bootstrap.bundle.min.js'}"></script>
 ``` 

[*Semantic*](https://semantic-ui.com/introduction/getting-started.html)
```html
<script src="@{'/public/Semantic/js/semantic.js'}"/></script>
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/icon.css'}">
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/grid.css'}"><!-- rodape utilizar-->
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/header.css'}"><!-- utilizado em loginUser e logarAdm  -->
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/segment.css'}"><!-- rodape utilizar-->
<link rel="stylesheet" type="text/css" href="@{'/public/Semantic/css/button.css'}">
```

## Tabela de Referências de Sites :link::
Referências    | Categoria   | Links
-------------- | :---------: | ----
Play Framework | Plataforma de Desenvolvimento |  [1.4.5](https://www.playframework.com/documentation/1.4.x/home) 
Bootstrap      | CSS         | [Bootstrap](https://getbootstrap.com/docs/4.4/getting-started/introduction/)
Semantic UI    | CSS         |  [Semantic](https://semantic-ui.com/introduction/getting-started.html) 
Uikit          | CSS         | [Uikit](https://getuikit.com/docs/introduction) 
Sweeetalert2   | JS          |  [Sweeetalert2](https://sweetalert2.github.io/)
Animate        |CSS-ANIMAÇÃO |  [Animate](https://daneden.github.io/animate.css/)
Jquery         | JS          |  [Jquery](https://jquery.com/)
W3school       | Plataforma de Conhecimento |  [W3school](https://www.w3schools.com/)
Icon8          | Plataforma de Icones | [Icons8](https://icons8.com.br/icons)
