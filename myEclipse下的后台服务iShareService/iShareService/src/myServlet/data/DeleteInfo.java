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
 * Servlet implementation class DeleteInfo
 */
@WebServlet("/DeleteInfo")
public class DeleteInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteInfo() {
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
        
      
		String infoId = request.getParameter("infoId").trim();
		
		if(infoId == null||infoId == "") {
			return;
		}
	
		
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		
		PreparedStatement prepareStatement2 = null;
		ResultSet rs2 =null;

		PreparedStatement prepareStatement3 = null;
		ResultSet rs3 =null;
		String backnews="";
		boolean boo = false;
		
		boo = infoId.length()>0;
		
		try {
			connection = JdbcUtils.getConnection();
		
			//3.获取statement
			String sql ="delete from focus where info_id=?";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, infoId);
			
			String sql2 ="delete from comment where comment_info=?";
			prepareStatement2 = connection.prepareStatement(sql2);
			prepareStatement2.setString(1, infoId);
			
			if(boo) {
    			//4.执行sql
    			prepareStatement.execute();
    			prepareStatement2.execute();
    			
    			String changeSql = "delete from info where info_id=?";
				prepareStatement3 = connection.prepareStatement(changeSql);
				prepareStatement3.setString(1, infoId);
				prepareStatement3.execute();
				
				JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("deleteOk", true);
		
				data.add(map);
				jsonArray = JsonUtils.formatRsToJsonArray(data);
				out.println(jsonArray.toString());  //返回json		
    		
			}else {
				JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

			
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("deleteOk", false);
		
				data.add(map);
				jsonArray = JsonUtils.formatRsToJsonArray(data);
				out.println(jsonArray.toString());  //返回json
			}
			out.print(backnews.toString());
		} catch (Exception e) {
			backnews="修改失败"+e.toString();
			out.print(backnews); 
			e.printStackTrace();
		
		}finally {
			//5.释放资源 connection prepareStatement
			JdbcUtils.close(connection, prepareStatement, rs);
			JdbcUtils.close(connection, prepareStatement2, rs2);
			JdbcUtils.close(connection, prepareStatement3, rs3);
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
