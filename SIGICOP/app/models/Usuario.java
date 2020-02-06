package models;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.data.validation.Email;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import seguranca.CriptografiaUtils;

@Entity
public class Usuario extends Model{
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Long id;
	
	@Required
	@MaxSize(45)
	public String nomeUsu;
	
	@Required
	public String matricula;
	
	@Required
	@Email
	public String email;

	@Min(0)
	public int qtdDisponivel = 20;

	@Required
	@Enumerated(EnumType.STRING)
	public TipoUsuario tipo = TipoUsuario.ALUNO;

	@Temporal(TemporalType.TIMESTAMP)
	public Date ultimoAcessoUsu;
	
	@Required
	@MinSize(6)
	public String senha;
	
	@Transient
	public String confirmarSenha;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		 String linha = nomeUsu;
	      String pattern = "\\S+";

	      Pattern r = Pattern.compile(pattern);
	      Matcher m = r.matcher(linha);
	      if (m.find( )) {
	        System.out.println(m.group(0) );
	      }
	    
		return m.group(0)+" ("+matricula+") qtd:"+qtdDisponivel;
	}
		
//	public Usuario() {
//		qtdDisponivel = 20;
//	}
	public boolean compararSenha(){
		
		if(senha.equals(confirmarSenha)) {
			return true;
		}else {
			return false;
		}
	}

}