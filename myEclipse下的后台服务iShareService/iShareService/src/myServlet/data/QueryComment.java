package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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

import com.sun.rowset.CachedRowSetImpl;

import net.sf.json.JSONArray;

/**
 * Servlet implementation class QueryComment
 */
@WebServlet("/QueryComment")
public class QueryComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	CachedRowSetImpl rowSet = null;    //�洢����ȫ����¼���м�����
	int pageSize=10; //ÿҳ��������
    int pageNum=1;   //�ڼ�ҳ
    int totalRecord;  //�ܼ�¼��
    int totalPage;   //��ҳ��
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryComment() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
    
        String infoId = request.getParameter("infoId").trim();
		
        String condition = "select * from comment where comment_info='"+infoId+"' order by comment_id desc";   
	
		Connection connection = null;
		Statement sql = null;
		ResultSet rs = null;

		try {
			connection = JdbcUtils.getConnection();
			sql = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = sql.executeQuery(condition);
			rowSet = new CachedRowSetImpl();  //�����м�����
			rowSet.populate(rs);
			//����ѯҳ�����ؽ��
			returnByPage(request,response,rowSet);
		} catch (Exception e) {	
			out.println(condition+"�쳣��"+e.toString());
			e.printStackTrace();
		}finally {
			//5.�ͷ���Դ connection prepareStatement
			JdbcUtils.statementClose(connection, sql, rs);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void returnByPage(HttpServletRequest request, HttpServletResponse response,CachedRowSetImpl rowSet)throws ServletException, IOException{
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
			JSONArray jsonArray = new JSONArray();//��ŷ��ص�jsonOjbect����
			JSONArray TotaljsonArray = new JSONArray();//��ŷ��ص�jsonOjbect����
			//��rowSet��������ȡ��Map
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			
			//��rowSet��������ȡ��Map
			List<Map<String,Object>> Totaldata = new ArrayList<Map<String,Object>>();
			try {
				PrintWriter out = response.getWriter();
				try {
					rowSet.last();    //�Ƶ����һ��
		    		totalRecord = rowSet.getRow();	    		
		    		if(totalRecord%pageSize==0){
		    			totalPage = totalRecord/pageSize;  //��ҳ��
		    		}else{
		    			totalPage = totalRecord/pageSize+1; 
		    		}
		    		
		    		int index = (pageNum-1)*pageSize+1;
	    			rowSet.absolute(index);   //��ѯλ���ƶ�����ѯҳ����ʼ��¼λ��
	    			boolean boo = true;
	    			
	    			for(int i=1; i<=pageSize&&boo;i++){	
	    			
	    				int commentId = rowSet.getInt(1);    //��������ID				
	    				String commentUser = rowSet.getString(2);   //������   	
	    				int commentInfo =  rowSet.getInt(3); 
	    				String commentDetail = rowSet.getString(4);   //��������

	    				Map<String,Object> map = new HashMap<String,Object>();
	    				map.put("commentId", commentId);
	    				map.put("commentUser", commentUser);
	    				map.put("commentInfo", commentInfo);
	    				map.put("commentDetail", commentDetail);

	    				data.add(map);
						boo = rowSet.next();
	    			}
	    			jsonArray = JsonUtils.formatRsToJsonArray(data);
	    			
	    			Map<String,Object> map = new HashMap<String,Object>();
    				map.put("totalRecord", totalRecord);
    				map.put("RecordDetail", jsonArray);
    				Totaldata.add(map);
    				TotaljsonArray = JsonUtils.formatRsToJsonArray(Totaldata);
	    			
	    			out.println(TotaljsonArray.toString());  //����json
	    			
				}catch(Exception e) {
					out.println("null"); 
				}
				
			}catch(IOException e) {
				
			}
		
	}

}
