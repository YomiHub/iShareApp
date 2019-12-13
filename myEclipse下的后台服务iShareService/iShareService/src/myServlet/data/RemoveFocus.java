package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.utils.JdbcUtils;
import org.utils.JsonUtils;

import net.sf.json.JSONArray;

/**
 * Servlet implementation class RemoveFocus
 */
@WebServlet("/RemoveFocus")
public class RemoveFocus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveFocus() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
		String focusId = request.getParameter("focusId").trim();
		
		if(focusId == null||focusId == "") {
			return;
		}
	
		
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;

		boolean boo = false;
		
		boo = focusId.length()>0;
		
		try {
			connection = JdbcUtils.getConnection();
		
			//3.获取statement
			String sql ="delete from focus where focus_id=?";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, focusId);
			
			if(boo) {
    			//4.执行sql
    			prepareStatement.execute();
    			   
				JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("removeOk", true);
		
				data.add(map);
				jsonArray = JsonUtils.formatRsToJsonArray(data);
				out.println(jsonArray.toString());  //返回json		
    		
			}else {
				JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

			
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("removeOk", false);
		
				data.add(map);
				jsonArray = JsonUtils.formatRsToJsonArray(data);
				out.println(jsonArray.toString());  //返回json
			}
			
		} catch (Exception e) {
			JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

		
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("removeOk", false);
	
			data.add(map);
			jsonArray = JsonUtils.formatRsToJsonArray(data);
			out.println(jsonArray.toString());  //返回json
		
		}finally {
			//5.释放资源 connection prepareStatement
			JdbcUtils.close(connection, prepareStatement, rs);
		}
	     out.flush();
	     out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
