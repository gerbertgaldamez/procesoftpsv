package com.terium.modelos;

public class CBArchivosInsertadosModel {
	private int id_archivos_insertados;
	private String nombre_archivo;
	private int banco;
	private int agencia;
	private String fecha;
	private String creadoPor;

	public CBArchivosInsertadosModel() {

	}

	public CBArchivosInsertadosModel(int id_archivos_insertados,
			String nombre_archivo, int banco, int agencia, String fecha,
			String creadoPor) {
		super();
		this.id_archivos_insertados = id_archivos_insertados;
		this.nombre_archivo = nombre_archivo;
		this.banco = banco;
		this.agencia = agencia;
		this.fecha = fecha;
		this.creadoPor = creadoPor;
	}

	public int getId_archivos_insertados() {
		return id_archivos_insertados;
	}

	public void setId_archivos_insertados(int id_archivos_insertados) {
		this.id_archivos_insertados = id_archivos_insertados;
	}

	public String getNombre_archivo() {
		return nombre_archivo;
	}

	public void setNombre_archivo(String nombre_archivo) {
		this.nombre_archivo = nombre_archivo;
	}

	public int getBanco() {
		return banco;
	}

	public void setBanco(int banco) {
		this.banco = banco;
	}

	public int getAgencia() {
		return agencia;
	}

	public void setAgencia(int agencia) {
		this.agencia = agencia;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}
}
