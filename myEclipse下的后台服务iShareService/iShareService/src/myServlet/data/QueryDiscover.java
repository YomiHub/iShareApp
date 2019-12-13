package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
/*import java.sql.ResultSetMetaData;*/
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.utils.JdbcUtils;
import org.utils.JsonUtils;
import com.sun.rowset.CachedRowSetImpl;
import net.sf.json.JSONArray;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


/**
 * Servlet implementation class QueryDiscover
 */
@WebServlet("/QueryDiscover")
public class QueryDiscover extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	CachedRowSetImpl rowSet = null;    //�洢����ȫ����¼���м�����
	int pageSize; //ÿҳ��������
    int pageNum;   //�ڼ�ҳ
    int totalRecord;  //�ܼ�¼��
    int totalPage;   //��ҳ��
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryDiscover() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
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
        
        String page = request.getParameter("page");	
        String count = request.getParameter("count");
        
        if(page == null||page == "") {
			page = "1";
		}
		
		if(count == null|count == "") {
			count = "10";
		}
        
        try {
            pageNum = Integer.parseInt(page);    //�ڼ�ҳ
            pageSize = Integer.parseInt(count);    //ÿҳ���ؼ���
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
		
		String condition = "select * from info order by info_id desc";
	
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
					//ResultSetMetaData metaData = rowSet.getMetaData();
					//int columnCount = metaData.getColumnCount();  //����������
					
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
	    				int infoId = rowSet.getInt(1);    //����ID	    				
	    				String infoTitle = rowSet.getString(2);   //���ݱ���    				
	    				String infoDescribe = rowSet.getString(3);   //���ݼ���
	    				String infoDetail = rowSet.getString("info_detail");   //��������
	    				
	    				String type = rowSet.getString(5);    //���ͣ�0��ʾ�ռǣ�1��ʾȤ��
	    				String support = rowSet.getString(6);   //������
	
	    				
	    				String infoAuthor = rowSet.getString(7);  //����     
	    				
	    				Map<String,Object> map = new HashMap<String,Object>();
	    				map.put("infoId", infoId);
	    				map.put("infoTitle", infoTitle);
	    				map.put("infoDescribe", infoDescribe);
	    				map.put("infoDetail", infoDetail);
	    				map.put("infoType", type);
	    				map.put("infoSupport", support);
	    				map.put("infoAuthor", infoAuthor);
	    				
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
