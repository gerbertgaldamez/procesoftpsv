package com.terium.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;

import com.terium.modelos.CBDataSinProcesarModel;
import com.terium.utils.DBUtils;

public class CBDataSinProcesarDAO {
	private String INSERTA_DATOS_NO_PROCESADOS = "INSERT "
			+ "INTO cb_data_sin_procesar " + "  ( "
			+ "  cbdatasinprocesarid,  nombre_archivo, " + "    data_archivo, "
			+ "causa,    estado, " + "    creado_por, "
			+ "    fecha_creacion, " + "    id_archivos_insertados " + "  ) "
			+ "  VALUES " + "  ( " + " cb_data_sin_procesar_sq.nextval,   ?, "
			+ "    ?, " + " ?,   ?, " + "    ?, " + "    sysdate, " + "    ? "
			+ "  )";

	// inserta masivos no procesados
	public int insertarMasivoNoProcesados(List<CBDataSinProcesarModel> registros) {
		int res = 0;
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				List<Object[]> dataList = new ArrayList<Object[]>(
						registros.size());
				Object[] param;
				Object[][] dataObj = new Object[registros.size()][6];
				for (CBDataSinProcesarModel registro : registros) {
					param = new Object[] { registro.getNombre_Archivo(),
							registro.getData_Archivo(), registro.getCausa(),
							registro.getEstado(), registro.getCreado_Por(),
							registro.getIdMaestroCarga() };
					dataList.add(param);
				}

				dataObj = dataList.toArray(dataObj);
				int[] objRet = qr.batch(con, INSERTA_DATOS_NO_PROCESADOS,
						dataObj);
				res = objRet.length;
				return res;

			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al insertat datos no procesados: "
					+ e.getMessage());
		}
		return res;
	}
}
