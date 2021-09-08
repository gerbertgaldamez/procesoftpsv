package com.terium.modelos;

import java.math.BigDecimal;

public class CBDataBancoModel {
	private int cBDataBancoId;
	private String codCliente;
	private String telefono;
	private String tipo;
	private String fecha;
	private int cBCatalogoBancoId;
	private int cBCatalogoAgenciaId;
	private int cBBancoAgenciaConfrontaId;
	private BigDecimal monto;
	private String transaccion;
	private int estado;
	private String mes;
	private String dia;
	private String texto1;
	private String texto2;
	private String creado_Por;
	private String modificado_Por;
	private String fecha_Creacion;
	private String fecha_Modificacion;
	private String idMaestroCarga;
	private String cbAgenciaVirfisCodigo;
	private String codigoAgencia;
	private String formatofecha;
	//Temporal para confrontas con mas de 1 convenio
		private boolean convenioconf;

	public boolean isConvenioconf() {
			return convenioconf;
		}

		public void setConvenioconf(boolean convenioconf) {
			this.convenioconf = convenioconf;
		}
	private BigDecimal comision;

	/**
	 * @return the cBDataBancoId
	 */
	public int getcBDataBancoId() {
		return cBDataBancoId;
	}

	/**
	 * @param cBDataBancoId
	 *            the cBDataBancoId to set
	 */
	public void setcBDataBancoId(int cBDataBancoId) {
		this.cBDataBancoId = cBDataBancoId;
	}

	/**
	 * @return the codCliente
	 */
	public String getCodCliente() {
		return codCliente;
	}

	/**
	 * @param codCliente
	 *            the codCliente to set
	 */
	public void setCodCliente(String codCliente) {
		this.codCliente = codCliente;
	}

	/**
	 * @return the telefono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * @param telefono
	 *            the telefono to set
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the fecha
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * @param fecha
	 *            the fecha to set
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the cBCatalogoBancoId
	 */
	public int getcBCatalogoBancoId() {
		return cBCatalogoBancoId;
	}

	/**
	 * @param cBCatalogoBancoId
	 *            the cBCatalogoBancoId to set
	 */
	public void setcBCatalogoBancoId(int cBCatalogoBancoId) {
		this.cBCatalogoBancoId = cBCatalogoBancoId;
	}

	/**
	 * @return the cBCatalogoAgenciaId
	 */
	public int getcBCatalogoAgenciaId() {
		return cBCatalogoAgenciaId;
	}

	/**
	 * @param cBCatalogoAgenciaId
	 *            the cBCatalogoAgenciaId to set
	 */
	public void setcBCatalogoAgenciaId(int cBCatalogoAgenciaId) {
		this.cBCatalogoAgenciaId = cBCatalogoAgenciaId;
	}

	/**
	 * @return the cBBancoAgenciaConfrontaId
	 */
	public int getcBBancoAgenciaConfrontaId() {
		return cBBancoAgenciaConfrontaId;
	}

	/**
	 * @param cBBancoAgenciaConfrontaId
	 *            the cBBancoAgenciaConfrontaId to set
	 */
	public void setcBBancoAgenciaConfrontaId(int cBBancoAgenciaConfrontaId) {
		this.cBBancoAgenciaConfrontaId = cBBancoAgenciaConfrontaId;
	}

	/**
	 * @return the monto
	 */
	public BigDecimal getMonto() {
		return monto;
	}

	/**
	 * @param monto
	 *            the monto to set
	 */
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	/**
	 * @return the transaccion
	 */
	public String getTransaccion() {
		return transaccion;
	}

	/**
	 * @param transaccion
	 *            the transaccion to set
	 */
	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
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
	 * @return the mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * @param mes
	 *            the mes to set
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	/**
	 * @return the texto1
	 */
	public String getTexto1() {
		return texto1;
	}

	/**
	 * @param texto1
	 *            the texto1 to set
	 */
	public void setTexto1(String texto1) {
		this.texto1 = texto1;
	}

	/**
	 * @return the texto2
	 */
	public String getTexto2() {
		return texto2;
	}

	/**
	 * @param texto2
	 *            the texto2 to set
	 */
	public void setTexto2(String texto2) {
		this.texto2 = texto2;
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

	/**
	 * @return the idMaestroCarga
	 */
	public String getIdMaestroCarga() {
		return idMaestroCarga;
	}

	/**
	 * @param idMaestroCarga
	 *            the idMaestroCarga to set
	 */
	public void setIdMaestroCarga(String idMaestroCarga) {
		this.idMaestroCarga = idMaestroCarga;
	}

	public String getCodigoAgencia() {
		return codigoAgencia;
	}

	public void setCodigoAgencia(String codigoAgencia) {
		this.codigoAgencia = codigoAgencia;
	}

	public String getCbAgenciaVirfisCodigo() {
		return cbAgenciaVirfisCodigo;
	}

	public void setCbAgenciaVirfisCodigo(String cbAgenciaVirfisCodigo) {
		this.cbAgenciaVirfisCodigo = cbAgenciaVirfisCodigo;
	}
	public String getFormatofecha() {
		return formatofecha;
	}

	public void setFormatofecha(String formatofecha) {
		this.formatofecha = formatofecha;
	}

	public BigDecimal getComision() {
		return comision;
	}

	public void setComision(BigDecimal comision) {
		this.comision = comision;
	}
	
	

}
