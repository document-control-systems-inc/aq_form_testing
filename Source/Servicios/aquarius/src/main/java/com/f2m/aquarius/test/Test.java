package com.f2m.aquarius.test;

import java.util.List;

import com.f2m.aquarius.service.DBService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Test {

	public static void main (String[] args) {
		DBService bd = new DBService();
		GeneralUtils gutils = new GeneralUtils();
		String select = "node.data->'id' as id, node.data->'properties'->'name' as name, node.data->'type' as type, node.data->'properties'->'createdBy' as createdBy, node.data->'properties'->'createdOn' as createdOn, jsonb_array_elements(document.data->'docVersion') as versions";
		String from = "node, document";
		String where = "node.data->>'id' = document.data->>'id' and node.data->'parent' ? 'b598e68c-eaa1-487b-b037-b98c5cf17365'";
		String orderBy = "node.data->'properties'->>'name'";
		long time_startTotal, time_endTotal;
		time_startTotal = System.currentTimeMillis();
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		System.out.println("Hoy: " + time_start);
		List<JsonNode> result = null;
		try {
			result = bd.selectColumnsJson(select, from, where, orderBy);
		} catch (AquariusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		time_end = System.currentTimeMillis();
	    System.out.println("---------------------------------------------------------------");
	    System.out.println("Tiempo Base de Datos: " + (time_end - time_start) + " milliseconds.");
	    
	    time_start = System.currentTimeMillis();
	    
	    ObjectMapper mapper = new ObjectMapper();
	    ObjectNode folderList = mapper.createObjectNode();
		// encabezado:
		ArrayNode headerRow = folderList.putArray("headerRow");
		headerRow.add("");
		headerRow.add("Nombre");
		headerRow.add("Tamaño");
		headerRow.add("Creador");
		headerRow.add("Fecha");
		headerRow.add("Version");
		// pie de página:
		ArrayNode footerRow = folderList.putArray("footerRow");
		footerRow.add("");
		footerRow.add("Nombre");
		footerRow.add("Tamaño");
		footerRow.add("Creador");
		footerRow.add("Fecha");
		footerRow.add("Version");
		
		ArrayNode dataRows = folderList.putArray("dataRows");
		
	    String txtName = "";
		String txtSize = "";
		String txtCreator = "";
		String txtDate = "";
		String txtVersion = "";
		String idObj = "";

		if (result != null) {
			System.out.println(result.get(0));
			for (JsonNode row:result) {
				txtName = row.findValue("name").asText();
				txtCreator = row.findValue("createdby").asText();
				idObj = row.findValue("id").asText();
				txtDate = gutils.getTimeString(row.findValue("createdon"));
				if (row.findValue("type").asText().equals("document"))
				{
					long sizeKb = row.findValue("versions").findValue("size").asLong();
					long size = (sizeKb/1024);
					txtSize = String.format("%,d", size) + " KB";
					txtVersion = row.findValue("versions").findValue("version").asText();
				}
				ObjectNode renglon = mapper.createObjectNode();
				ArrayNode dataRow = renglon.arrayNode();
				dataRow.add(txtName);
				dataRow.add(txtSize);
				dataRow.add(txtCreator);
				dataRow.add(txtDate);
				dataRow.add(txtVersion);
				dataRow.add(idObj);
				dataRows.add(dataRow);
			}
		}
		time_end = System.currentTimeMillis();
	    System.out.println("---------------------------------------------------------------");
	    System.out.println("Tiempo Procesamiento de Datos: " + (time_end - time_start) + " milliseconds.");
		
	    
	    System.out.println("Total Registros: " + folderList.findValue("dataRows").size());
	    time_endTotal = System.currentTimeMillis();
	    System.out.println("---------------------------------------------------------------");
	    System.out.println("Tiempo Total: " + (time_endTotal - time_startTotal) + " milliseconds.");
	}
}
