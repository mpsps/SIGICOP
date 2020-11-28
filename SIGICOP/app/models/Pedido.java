package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import net.sf.oval.constraint.MaxLength;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.SituacaoPedido;
import util.StatusPedido;

@Entity
public class Pedido extends Model {		
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Long id;
	
	@Transient 
	public int idLista;
	
	@Required
	public Blob arquivo;
	
	@Required
	public String nomeArquivo;
	
	@Required
	@Min(1)
	public int qtdCopias;
	
	@Required
	public int paginas;
	
	@Required
	public String frenteVerso;
	
	public String descricao;
	
	@Required
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataEnvio;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataEntrega;
	
	@Required
	@Enumerated(EnumType.STRING)
	public StatusPedido status = StatusPedido.AGUARDANDO;

	@Required
	@Enumerated(EnumType.STRING)
	public SituacaoPedido situacao;

	@ManyToOne
	public Usuario usuario;
	
	@ManyToOne
	public Operador adm;
	
	@Lob
	public String atendimento;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataAtendimento;

}
