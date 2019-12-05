package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import util.CriptografiaUtils;

@Entity
public class Usuario extends Model{
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Long id;
	
	@Required
	@MaxSize(45)
	public String nomeUsu;
	
	@Required
//	@Unique(value = "matricula")
	public String matricula;
	
	@Required
	@MinSize(6)
	public String senha;
	
	@Transient
	public String confirmarSenha;
	
	@Required
	@Email
	public String email;
	
	public int qtdDisponivel;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date ultimoAcessoUsu;
	
//	@Enumerated
//	public Patente patente;
	
	public Usuario() {
		qtdDisponivel = 20;
	}
	public boolean compararSenha(){
		boolean cs = false;
		if(senha.equals(confirmarSenha)) {
			return cs = true;
		}else {
			return cs;
		}
	}
	public Usuario autenticar() {
		String senhaCript = CriptografiaUtils.criptografarMD5(senha);
		Usuario u = Usuario.find("matricula = ?1 and senha = ?2",
				matricula, senhaCript).first();
		return u;
	}
}
