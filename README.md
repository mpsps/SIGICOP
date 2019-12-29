# SIGICOP
Sistema de Gerenciamento de Impressões e Copias

## Descrição:
**A SIGICOP é um projeto de conclusão do curso subsequente em informática,
 realizado por Manacio Pereira de Souza ([@Manacio](https://github.com/Manacio)) e por Magdiel Pereira de Souza ([@MagdielPS](https://github.com/MagdielPS)).
 A SIGICOP é um sistema de gerenciamento de cópias e impressões,
 que facilite e agilize a demanda de impressões e cópias do IFRN-CAMPUS-JC,
 onde o usuário possa ter controle sobre a quantidade de solicitação disponíveis e o administrdor possar gerenciar as solicitacões**

# Diagrama de Classes
![DiagramaUMLClasses](https://user-images.githubusercontent.com/55263599/71190212-e6a60200-2262-11ea-9943-93c5a2809e5e.jpg)

# Diagrama de Caso de Uso do Usuário
![Caso de Uso Usuario](https://user-images.githubusercontent.com/55263599/71537780-b9f78780-28ff-11ea-9626-8dc391f2091c.jpg)

# Diagrama de Caso de Uso Administrador
![Caso de uso Administrador](https://user-images.githubusercontent.com/55263599/71537757-98969b80-28ff-11ea-974d-c886ad2fbdd8.jpg)


# PASSO A PASSO DO USUÁRIO

* O usuário deverá acessar o site da SIGICOP;
* Quando visitar a página principal clicar em "Logar" ou em "logar agora";
* Ao entrar na página de login, fornecer o login e senha para entrar no sistema;
* O usuário ao logar, poderá visualizar seus pedidos anteriores (se houver), e poderá filtra-los e efetuar download;
* Em "Meus Dados" (icone de usuário) poderá visualizar a quantidade disponível de solicitações, e também poderá vizualizar seu nome, email e ultimo acesso;
* Poderá solicitar pedidos de impressões, ao solicitar, se houver solicitacão disponivel, aparecerá o formulário, se não, aparecerá mensagem em vermelho informando que não é possivel efetuar nenhuma solicitacão;
* Após logar, o usuário poderá sair a qualquer momento;

## Páginas da SIGICOP para Usuário
![Webp net-gifmaker](https://user-images.githubusercontent.com/55263599/71113258-f7e00780-21ab-11ea-8106-f04db75df755.gif)

## Páginas da SIGICOP para Usuário - Versão Mobile (260 x 450)
![passoApassoUsuario](https://user-images.githubusercontent.com/55263599/71545632-55264680-296c-11ea-8b5d-1b49adb92406.gif)

# PASSO A PASSO DO ADMINISTRADOR

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

## Páginas da SIGICOP para Administrador
![Webp net-gifmaker](https://user-images.githubusercontent.com/55263599/71113258-f7e00780-21ab-11ea-8106-f04db75df755.gif)

## Páginas da SIGICOP para Administrador - Versão Mobile (260 x 450)
![Webp net-gifmaker](https://user-images.githubusercontent.com/55263599/71113258-f7e00780-21ab-11ea-8106-f04db75df755.gif)

## As importações mais importantes utlizado nos templates layPri.html e laySeg.html:

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

## Tabela de Referências de Sites:
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
