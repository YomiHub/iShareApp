package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
 * Servlet implementation class AddComment
 */
@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddComment() {
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
        
        Integer commentId =0;    //内容ID,默认设置为0
        String infoIdStr = request.getParameter("infoId");   //内容标题
        String commentUser =request.getParameter("commentUser");   //内容简述
        String commentDetail = request.getParameter("commentDetail");   //内容详情

        if(infoIdStr == null||infoIdStr == "") {
        	infoIdStr = "";
		}
        
        if(commentUser == null||commentUser == "") {
        	commentUser = "";
		}
        
        if(commentDetail == null||commentDetail == "") {
        	commentDetail = "";
		}
      
        
        Boolean m = infoIdStr.length()>0&&commentUser.length()>0&&commentDetail.length()>0;
        
        int infoId = 0;
        try {
        	infoId = Integer.parseInt(infoIdStr);    //内容类型
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }       
		 
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = JdbcUtils.getConnection();
			//3.获取statement
			String sql ="INSERT INTO comment VALUES(?,?,?,?)";
			prepareStatement = connection.prepareStatement(sql);
			
			if(m) {
				prepareStatement.setInt(1, commentId);  //自增
				prepareStatement.setString(2, commentUser);
				prepareStatement.setInt(3, infoId);
				prepareStatement.setString(4, commentDetail);
				
				//4.执行sql
				prepareStatement.execute();
				JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("addOk", true);
		
				data.add(map);
				jsonArray = JsonUtils.formatRsToJsonArray(data);
				out.println(jsonArray.toString());  //返回json
				
			}else {
				JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("addOk", false);
		
				data.add(map);
				jsonArray = JsonUtils.formatRsToJsonArray(data);
				out.println(jsonArray.toString());  //返回json
			}
			
  
		} catch (Exception e) {
			JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("addOk", false);
	
			data.add(map);
			jsonArray = JsonUtils.formatRsToJsonArray(data);
			out.println(jsonArray.toString());  //返回json
			
		}finally {
			//5.释放资源 connection prepareStatement
			JdbcUtils.close(connection, prepareStatement, null);
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
