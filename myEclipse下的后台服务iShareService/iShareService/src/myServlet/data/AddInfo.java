package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.utils.JdbcUtils;

/**
 * Servlet implementation class AddInfo
 */
@WebServlet("/AddInfo")
public class AddInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	 //处理中文字符串
    public String handleString(String s) {
    	try {
    		byte bb[]=s.getBytes("ISO8859-1");
    		s=new String(bb,"UTF-8");
    	}catch(Exception e){}
    	
    	return s;
    }
    
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        
        Integer infoId =0;    //内容ID,默认设置为0
        String infoTitle = request.getParameter("infoTitle");   //内容标题
        String infoDescribe =request.getParameter("infoDescribe");   //内容简述
        String infoDetail = request.getParameter("infoDetail");   //内容详情

        String typeStr = request.getParameter("type");    //类型：0表示日记，1表示趣事
        Integer type = 0;
        if(typeStr == null||typeStr == "") {
        	typeStr = "0";
		}
        
        try {
        	type = Integer.parseInt(typeStr);    //内容类型
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
       
        Integer support = 0;   //点赞数，默认设置为0
        String infoAuthor = request.getParameter("infoAuthor");  //作者
	     
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = JdbcUtils.getConnection();
			//3.获取statement
			String sql ="INSERT INTO info VALUES(?,?,?,?,?,?,?)";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setInt(1, infoId);  //自增
			prepareStatement.setString(2, infoTitle);
			prepareStatement.setString(3, infoDescribe);
			prepareStatement.setString(4, infoDetail);
			prepareStatement.setInt(5, type);
			prepareStatement.setInt(6, support);
			prepareStatement.setString(7, infoAuthor);
			//4.执行sql
			prepareStatement.execute();
			out.print("发布成功!");
  
		} catch (Exception e) {
			e.printStackTrace();
			out.print("发布失败！"+e);
			
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
