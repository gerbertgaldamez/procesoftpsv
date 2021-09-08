package com.terium.modelos;

public class CBConexionConfModel {
	private String idConexionConf;
	private String ipConexion;
	private String usuario;
	private String pass;
	private String creadoPor;
	private String fechaCreacion;
	private String nombreConexion;

	public CBConexionConfModel() {

	}

	public CBConexionConfModel(String idConexionConf, String ipConexion,
			String usuario, String pass, String creadoPor,
			String fechaCreacion, String nombreConexion) {
		super();
		this.idConexionConf = idConexionConf;
		this.ipConexion = ipConexion;
		this.usuario = usuario;
		this.pass = pass;
		this.creadoPor = creadoPor;
		this.fechaCreacion = fechaCreacion;
		this.nombreConexion = nombreConexion;
	}

	public String getIdConexionConf() {
		return idConexionConf;
	}

	public void setIdConexionConf(String idConexionConf) {
		this.idConexionConf = idConexionConf;
	}

	public String getIpConexion() {
		return ipConexion;
	}

	public void setIpConexion(String ipConexion) {
		this.ipConexion = ipConexion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	public String getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getNombreConexion() {
		return nombreConexion;
	}

	public void setNombreConexion(String nombreConexion) {
		this.nombreConexion = nombreConexion;
	}

}
