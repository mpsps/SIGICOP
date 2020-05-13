package jobs;

import java.util.Date;
import java.util.List;

import models.Administrador;
import models.Pedido;
import models.Usuario;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import seguranca.CriptografiaUtils;
import util.StatusPedido;
import util.TipoUsuario;

@OnApplicationStart
public class Inicializador extends Job {

	@Override
	public void doJob() throws Exception {
		// 	GERANDO UM ADMINISTRADOR PADRAO
		if (Administrador.count() == 0) {

			Administrador admPadrao = new Administrador();
			admPadrao.admPadrao = true;
			admPadrao.nomeAdm = "Admin Absoluto";
			admPadrao.email = "padrao@email.com";
			String senhaCript = CriptografiaUtils.criptografarMD5("123456");
			admPadrao.senha = senhaCript;
			admPadrao.save();
			
			Administrador adm = new Administrador();
			adm.admPadrao = false;
			adm.nomeAdm = "Admin Comum";
			adm.email = "comum@email.com";
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
			usuario.qtdDisponivel = 15;
			usuario.save();
			
			Pedido pedUsuario = new Pedido();
			pedUsuario.nomeArquivo = "Arquivo de teste";
			pedUsuario.dataEnvio = new Date();
			pedUsuario.descricao = "Pedido do usuario de testes";
			pedUsuario.frenteVerso = "frente";
			pedUsuario.qtdCopias = 5;
			pedUsuario.usuario = usuario;
			pedUsuario.save();
			
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
