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
 * Servlet implementation class IfAddlike
 */
@WebServlet("/IfAddFocus")
public class IfAddFocus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IfAddFocus() {
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
	          
		String username = request.getParameter("username").trim();
		String infoId = request.getParameter("infoId").trim();
		int focusId = 0;

        
		if(username == null) {
			username = "";
		}
		
		if(infoId == null) {
			infoId = "";
		}
      
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		boolean boo = false;
		
		boo = username.length()>0&&infoId.length()>0;
		
		try {
			connection = JdbcUtils.getConnection();
			//3.获取statement
			String sql ="select * from focus where username=? and info_id=?";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, username);
			prepareStatement.setString(2, infoId);
			
			if(boo) {
    			//4.执行sql
				boolean m =false;
				
    			rs = prepareStatement.executeQuery();
    			while(rs.next()) {
    				m = true;
    				focusId = rs.getInt(1);
    			}
    			
    			if(m==true) {
    			
    				success(request,response,focusId);
    				
    			}else {
    				fail(request,response);
    			}
			}else {
				fail(request,response);
			}
			
  
		} catch (Exception e) {
			e.printStackTrace();
			fail(request,response);
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
	
	public void success(HttpServletRequest request, HttpServletResponse response, int focusId) {
		try {
			JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			
			PrintWriter out = response.getWriter();
	
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("isFocus", true);
			map.put("focusId", focusId);
	
			data.add(map);
			jsonArray = JsonUtils.formatRsToJsonArray(data);
			out.println(jsonArray.toString());  //返回json
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fail(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		try {
			JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			
			PrintWriter out = response.getWriter();
	
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("isFocus", false);
	
			data.add(map);
			jsonArray = JsonUtils.formatRsToJsonArray(data);
			out.println(jsonArray.toString());  //返回json
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
