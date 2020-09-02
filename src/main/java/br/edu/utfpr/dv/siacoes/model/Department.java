package br.edu.utfpr.dv.siacoes.model;

import jdk.nashorn.internal.objects.annotations.Setter;

import javax.annotation.Generated;
import java.io.Serializable;

@NoArgsConstructor @AllArgsConstructor
public class Department implements Serializable {


	
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	@Getter private int idDepartment;
	@Getter @Setter private Campus campus;
	@Getter @Setter private String name;
	@Getter @Setter private String fullName;
	@Getter @Setter private transient byte[] logo;
	@Getter @Setter private boolean active;
	@Getter @Setter private String site;
	@Getter @Setter private String initials;
	
/*	public Department(){
		this.setIdDepartment(0);
		this.setCampus(new Campus());
		this.setName("");
		this.setFullName("");
		this.setLogo(null);
		this.setActive(true);
		this.setSite("");
		this.setInitials("");
	} */

/*	public void setIdDepartment(int idDepartment) {
		this.idDepartment = idDepartment;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setSite(String site){
		this.site = site;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	} */
	
	public String toString(){
		return this.getName();
	}
	
}
