package org.utils;
 
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.util.List;
import java.util.Map;

public class JsonUtils {
	public static JSONArray formatRsToJsonArray(List<Map<String,Object>> data){
		JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
		for(Map<String,Object> rowItem:data) {
			JSONObject json = new JSONObject();
				for(Map.Entry<String, Object> entry:rowItem.entrySet()) {
					json.put(entry.getKey(), entry.getValue());
				}
			jsonArray.add(json);
		}
		return jsonArray;
	}
}
