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
import seguranca.CriptografiaUtils;

@Entity
public class Operador extends Model {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Long id;
	
	public boolean administrador = false;
	
	@Required
	public String nomeAdm;
	
	@Required
	@Email
	public String email;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date ultimoAcesso;
	
	@Required
	@MinSize(6)
	public String senha;
	
	@Transient
	public String confirmarSenha;
	
	public boolean compararSenha() {

		if (senha.equals(confirmarSenha)) {
			return true;
		} else {
			return false;
		}
	}

}
