package com.terium.modelos;

public class CBDataSinProcesarModel {

	private int cBDataSinProcesarId;
	private String nombre_Archivo;
	private String data_Archivo;
	private String causa;
	private int estado;
	private String creado_Por;
	private String modificado_Por;
	private String fecha_Creacion;
	private String fecha_Modificacion;
	private String idMaestroCarga;

	/**
	 * @return the cBDataSinProcesarId
	 */
	public int getcBDataSinProcesarId() {
		return cBDataSinProcesarId;
	}

	/**
	 * @param cBDataSinProcesarId
	 *            the cBDataSinProcesarId to set
	 */
	public void setcBDataSinProcesarId(int cBDataSinProcesarId) {
		this.cBDataSinProcesarId = cBDataSinProcesarId;
	}

	/**
	 * @return the nombre_Archivo
	 */
	public String getNombre_Archivo() {
		return nombre_Archivo;
	}

	/**
	 * @param nombre_Archivo
	 *            the nombre_Archivo to set
	 */
	public void setNombre_Archivo(String nombre_Archivo) {
		this.nombre_Archivo = nombre_Archivo;
	}

	/**
	 * @return the data_Archivo
	 */
	public String getData_Archivo() {
		return data_Archivo;
	}

	/**
	 * @param data_Archivo
	 *            the data_Archivo to set
	 */
	public void setData_Archivo(String data_Archivo) {
		this.data_Archivo = data_Archivo;
	}

	/**
	 * @return the causa
	 */
	public String getCausa() {
		return causa;
	}

	/**
	 * @param causa
	 *            the causa to set
	 */
	public void setCausa(String causa) {
		this.causa = causa;
	}

	/**
	 * @return the estado
	 */
	public int getEstado() {
		return estado;
	}

	/**
	 * @param estado
	 *            the estado to set
	 */
	public void setEstado(int estado) {
		this.estado = estado;
	}

	/**
	 * @return the creado_Por
	 */
	public String getCreado_Por() {
		return creado_Por;
	}

	/**
	 * @param creado_Por
	 *            the creado_Por to set
	 */
	public void setCreado_Por(String creado_Por) {
		this.creado_Por = creado_Por;
	}

	/**
	 * @return the modificado_Por
	 */
	public String getModificado_Por() {
		return modificado_Por;
	}

	/**
	 * @param modificado_Por
	 *            the modificado_Por to set
	 */
	public void setModificado_Por(String modificado_Por) {
		this.modificado_Por = modificado_Por;
	}

	/**
	 * @return the fecha_Creacion
	 */
	public String getFecha_Creacion() {
		return fecha_Creacion;
	}

	/**
	 * @param fecha_Creacion
	 *            the fecha_Creacion to set
	 */
	public void setFecha_Creacion(String fecha_Creacion) {
		this.fecha_Creacion = fecha_Creacion;
	}

	/**
	 * @return the fecha_Modificacion
	 */
	public String getFecha_Modificacion() {
		return fecha_Modificacion;
	}

	/**
	 * @param fecha_Modificacion
	 *            the fecha_Modificacion to set
	 */
	public void setFecha_Modificacion(String fecha_Modificacion) {
		this.fecha_Modificacion = fecha_Modificacion;
	}

	public String getIdMaestroCarga() {
		return idMaestroCarga;
	}

	public void setIdMaestroCarga(String idMaestroCarga) {
		this.idMaestroCarga = idMaestroCarga;
	}

}
