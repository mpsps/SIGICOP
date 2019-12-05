package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.swing.text.StyledEditorKit.BoldAction;

import groovy.transform.Generated;
import net.sf.oval.constraint.Email;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.MinLength;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.CriptografiaUtils;

@Entity
public class Administrador extends Model {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Long id;
	public boolean admPadrao;
	
	@Required
	@MaxSize(45)
	public String nomeAdm;
	
	@Required
	@Email
	public String email;
	
	@Required
	@MinSize(6)
	public String senha;
	
	@Transient
	public String confirmarSenha;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date ultimoAcesso;
	
	
	public boolean compararSenha(){
		boolean s = false;
		if(senha.equals(confirmarSenha)) {
			System.out.println("confirmacao de senha: "+confirmarSenha);
			 s = true;
		}
		return s;
	}
	public Administrador autenticar() {
		
		String senhaCript = CriptografiaUtils.criptografarMD5(senha);
		Administrador a = Administrador.find("email = ?1 and senha = ?2", email, senhaCript).first();
	
		return a;
	}

}
