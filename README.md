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

# Diagrama de Caso de Uso

![Caso_de_uso](https://user-images.githubusercontent.com/55263599/71190066-a21a6680-2262-11ea-8f1c-7cf2cf5c059a.jpg)

# Diagrama de Classes
![DiagramaUMLClasses](https://user-images.githubusercontent.com/55263599/71190212-e6a60200-2262-11ea-9943-93c5a2809e5e.jpg)

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
 
