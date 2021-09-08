package com.terium.dao;

import java.sql.Connection;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.terium.modelos.CBArchivosInsertadosModel;
import com.terium.utils.DBUtils;

public class CBInsertMaestroCargaDAO {
	private String INSERTAR_ARCHIVOS = "INSERT "
			+ "INTO CB_ARCHIVOS_INSERTADOS " + "  ( "
			+ "    ID_ARCHIVOS_INSERTADOS, " + "    NOMBRE_ARCHIVO, "
			+ "    BANCO, " + "    AGENCIA, " + "   FECHA ," + " CREADO_POR,"
			+ " FECHA_CREACION " + " ) " + "  VALUES " + "  ( " + "    ? "
			+ "  , " + "   ?, " + "   ?, " + "   ?, " + "   sysdate, " + " ?, "
			+ " sysdate " + ")";

	// inserta en tabla maestro
	public int insertaArchivos(CBArchivosInsertadosModel registro,
			String idMaestroCarga) {
		int ret = 0;
		Object[] param = new Object[] { idMaestroCarga,
				registro.getNombre_archivo(), registro.getBanco(),
				registro.getAgencia(), registro.getCreadoPor() };
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				ret = qr.update(con, INSERTAR_ARCHIVOS, param);
				System.out.println("se a insertado en tabla el archivo maestro: " + ret);
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al insertar archivos maestro: "
					+ e.getMessage());
		}
		return ret;
	}

	// obtiene id ultimo id Maestro de carga

	private String CONSULTA_ID_ARCHIVO = "SELECT CB_ARCHIVOS_INSERTADOS_SQ.NEXTVAL FROM DUAL";

	public String obtieneUltimoIDMaestroIns() {
		String resultado = "";
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				ScalarHandler sh = new ScalarHandler();
				resultado = qr.query(con, CONSULTA_ID_ARCHIVO, sh,
						new Object[] {}).toString();

			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("errror al obtener ultimo id maestro");
		}
		return resultado;
	}

	//Metodo para borrar un archivo cargado
	private String BORRAR_ARCHIVO = "DELETE CB_ARCHIVOS_INSERTADOS "
			+ " WHERE ID_ARCHIVOS_INSERTADOS = ?";

	public void borrarArchivo(String idMaestroCarga) {
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				qr.update(con, BORRAR_ARCHIVO, idMaestroCarga);
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al eliminar el archivo: "
					+ e.getMessage());
		}

	}
}
