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

	 //���������ַ���
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
        
        Integer infoId =0;    //����ID,Ĭ������Ϊ0
        String infoTitle = request.getParameter("infoTitle");   //���ݱ���
        String infoDescribe =request.getParameter("infoDescribe");   //���ݼ���
        String infoDetail = request.getParameter("infoDetail");   //��������

        String typeStr = request.getParameter("type");    //���ͣ�0��ʾ�ռǣ�1��ʾȤ��
        Integer type = 0;
        if(typeStr == null||typeStr == "") {
        	typeStr = "0";
		}
        
        try {
        	type = Integer.parseInt(typeStr);    //��������
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
       
        Integer support = 0;   //��������Ĭ������Ϊ0
        String infoAuthor = request.getParameter("infoAuthor");  //����
	     
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = JdbcUtils.getConnection();
			//3.��ȡstatement
			String sql ="INSERT INTO info VALUES(?,?,?,?,?,?,?)";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setInt(1, infoId);  //����
			prepareStatement.setString(2, infoTitle);
			prepareStatement.setString(3, infoDescribe);
			prepareStatement.setString(4, infoDetail);
			prepareStatement.setInt(5, type);
			prepareStatement.setInt(6, support);
			prepareStatement.setString(7, infoAuthor);
			//4.ִ��sql
			prepareStatement.execute();
			out.print("�����ɹ�!");
  
		} catch (Exception e) {
			e.printStackTrace();
			out.print("����ʧ�ܣ�"+e);
			
		}finally {
			//5.�ͷ���Դ connection prepareStatement
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
