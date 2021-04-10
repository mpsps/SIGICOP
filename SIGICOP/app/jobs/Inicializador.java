package jobs;

import java.util.Date;
import java.util.List;

import models.Operador;
import models.Pedido;
import models.Usuario;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import seguranca.CriptografiaUtils;
import util.SituacaoPedido;
import util.StatusPedido;
import util.TipoUsuario;

@OnApplicationStart
public class Inicializador extends Job {

	@Override
	public void doJob() throws Exception {
		// 	GERANDO UM ADMINISTRADOR PADRAO
		Operador admPadrao = new Operador();
		Operador adm = new Operador();
		if (Operador.count() == 0) {

			
			admPadrao.administrador = true;
			admPadrao.nomeAdm = "Administrador";
			admPadrao.email = "admin@email.com";
			String senhaCript = CriptografiaUtils.criptografarMD5("123456");
			admPadrao.senha = senhaCript;
			admPadrao.save();
			
			
			adm.administrador = false;
			adm.nomeAdm = "Operador";
			adm.email = "operador@email.com";
			String senhaCriptAdm = CriptografiaUtils.criptografarMD5("123456");
			adm.senha = senhaCriptAdm;
			adm.save();
		}		
		// GERANDO DOIS USUARIOS E DOIS PEDIDO POR USUARIO
		if (Usuario.count() == 0) {

			Usuario magdiel = new Usuario();
			magdiel.nomeUsu = "Magdiel Pereira de Souza";
			magdiel.matricula = "20181064110021";
			String senhaCriptMagdiel = CriptografiaUtils.criptografarMD5("123456");
			magdiel.senha = senhaCriptMagdiel;
			magdiel.email = "magdiel@gmail.com";
			magdiel.qtdDisponivel = 15;
			magdiel.save();
			
			Usuario manacio = new Usuario();
			manacio.nomeUsu = "Manacio Pereira de Souza";
			manacio.matricula = "20181064110023";
			String senhaCriptManacio = CriptografiaUtils.criptografarMD5("123456");
			manacio.senha = senhaCriptManacio;
			manacio.email = "manacio@gmail.com";
			manacio.qtdDisponivel = 15;
			manacio.save();
			
			Usuario usuario = new Usuario();
			usuario.nomeUsu = "Usuario Sobrenome da Geração";
			usuario.matricula = "20181064110025";
			String senhaCriptUsuario = CriptografiaUtils.criptografarMD5("123456");
			usuario.senha = senhaCriptUsuario;
			usuario.email = "usuario@email.com";
			usuario.qtdDisponivel = 3;
			usuario.save();
			
			Pedido pedUsu1 = new Pedido();
			pedUsu1.nomeArquivo = "Aquivo1.pdf";
			pedUsu1.dataEnvio = new Date();
			pedUsu1.descricao = "Pedido do usuario de testes";
			pedUsu1.frenteVerso = "frente";
			pedUsu1.qtdCopias = 1;
			pedUsu1.paginas = 3;
			pedUsu1.situacao = SituacaoPedido.DESARQUIVADO;
			pedUsu1.usuario = usuario;
			pedUsu1.adm = admPadrao;
			pedUsu1.save();
			
			Pedido pedUsu2 = new Pedido();
			pedUsu2.nomeArquivo = "Arquivo2.pdf";
			pedUsu2.dataEnvio = new Date();
			pedUsu2.descricao = "Pedido do usuario de testes";
			pedUsu2.frenteVerso = "frente";
			pedUsu2.qtdCopias = 3;
			pedUsu2.paginas = 4;
			pedUsu2.status = StatusPedido.CONCLUIDO;
			pedUsu2.situacao = SituacaoPedido.DESARQUIVADO;
			pedUsu2.usuario = usuario;
			pedUsu2.adm = admPadrao;
			pedUsu2.save();
			
			Pedido pedUsu3 = new Pedido();
			pedUsu3.nomeArquivo = "Arquivo3.docx";
			pedUsu3.dataEnvio = new Date();
			pedUsu3.descricao = "Pedido do usuario de testes";
			pedUsu3.frenteVerso = "frente";
			pedUsu3.qtdCopias = 3;
			pedUsu3.paginas = 1;
			pedUsu3.status = StatusPedido.RECUSADO;
			pedUsu3.situacao = SituacaoPedido.DESARQUIVADO;
			pedUsu3.usuario = usuario;
			pedUsu3.adm = admPadrao;
			pedUsu3.save();
			
			Pedido pedUsu4 = new Pedido();
			pedUsu4.nomeArquivo = "Arquivo4.pptx";
			pedUsu4.dataEnvio = new Date();
			pedUsu4.descricao = "Pedido do usuario de testes";
			pedUsu4.frenteVerso = "frente";
			pedUsu4.qtdCopias = 2;
			pedUsu4.paginas = 5;
			pedUsu4.status = StatusPedido.AGUARDANDO;
			pedUsu4.situacao = SituacaoPedido.DESARQUIVADO;
			pedUsu4.usuario = usuario;
			pedUsu4.adm = adm;
			pedUsu4.save();
			
			
			Pedido pedUsu5 = new Pedido();
			pedUsu5.nomeArquivo = "Arquivo5.png";
			pedUsu5.dataEnvio = new Date();
			pedUsu5.descricao = "Pedido do usuario de testes";
			pedUsu5.frenteVerso = "frente";
			pedUsu5.qtdCopias = 1;
			pedUsu5.paginas = 1;
			pedUsu5.status = StatusPedido.ENTREGUE;
			pedUsu5.situacao = SituacaoPedido.DESARQUIVADO;
			pedUsu5.usuario = usuario;
			pedUsu5.adm = adm;
			pedUsu5.save();
			
			Usuario prof = new Usuario();
			prof.nomeUsu = "Professor";
			prof.matricula = "201810";
			String senhaCriptProf = CriptografiaUtils.criptografarMD5("123456");
			prof.senha = senhaCriptProf;
			prof.email = "prof@email.com";
			prof.tipo = TipoUsuario.SERVIDOR;
			prof.qtdDisponivel = 100;
			prof.save();
//			
//			Pedido pedMagdiel2 = new Pedido();
//			pedMagdiel2.nomeArquivo = "Arquivo de teste do nome 2";
//			pedMagdiel2.dataEnvio = new Date();
//			pedMagdiel2.descricao = "Pedido de Magdiel2";
//			pedMagdiel2.frenteVerso = "frenteEverso";
//			pedMagdiel2.qtdCopias = 1;
//			pedMagdiel2.usuario = magdiel;
//			pedMagdiel2.save();
//			
//			Pedido pedManacio = new Pedido();
//			pedManacio.nomeArquivo = "Arquivo de teste do nome 1";
//			pedManacio.dataEnvio = new Date();
//			pedManacio.descricao = "Pedido de Manacio1";
//			pedManacio.frenteVerso = "frenteEverso";
//			pedManacio.qtdCopias = 7;
//			pedManacio.usuario = manacio;
//			pedManacio.save();
//			
//			Pedido pedManacio2 = new Pedido();
//			pedManacio2.nomeArquivo = "Arquivo de teste do nome 2";
//			pedManacio2.dataEnvio = new Date();
//			pedManacio2.descricao = "Pedido de Manacio2";
//			pedManacio2.frenteVerso = "frente";
//			pedManacio2.qtdCopias = 2;
//			pedManacio2.usuario = manacio;
//			pedManacio2.save();
		}
	}

}
