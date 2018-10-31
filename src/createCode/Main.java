package createCode;

import com.bean.Column;
import com.bean.TableBean;
import com.sun.imageio.plugins.common.ImageUtil;
import com.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String name = "{{name}}";
    private static final String columnNameArr = "{{columnNameArr}}";
    private static final String columnTypeArr = "{{columnTypeArr}}";
    private static final String columnCommentArr = "{{columnCommentArr}}";
    private static final String tableName = "{{tableName}}";

    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3308/supply_chain?useUnicode=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static final String SQL = "SELECT * FROM ";// 数据库操作

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.error("can not load jdbc driver", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }

    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            LOGGER.error("getTableNames failure", e);
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }

    /**
     * 获取表中所有字段名称
     *
     * @param tableName 表名
     * @return
     */
    public static List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnNames failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnNames close pstem and connection failure", e);
                }
            }
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     *
     * @param tableName
     * @return
     */
    public static List<String> getColumnTypes(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnTypes failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnTypes close pstem and connection failure", e);
                }
            }
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     *
     * @param tableName
     * @return
     */
    public static List<String> getColumnComments(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        List<String> columnComments = new ArrayList<>();//列名注释集合
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }

    public static void main(String[] args) throws Exception{
//        List<String> tableNames = getTableNames();
//        System.out.println("tableNames:" + tableNames);
//        System.out.println("------------------------------------------------");
//        for (String tableName : tableNames) {
//            System.out.println("----------------------" + tableName + "--------------------------");
//            System.out.print("ColumnNames:" + getColumnNames(tableName) + ";");
//            System.out.print("ColumnTypes:" + getColumnTypes(tableName) + ";");
//            System.out.println("ColumnComments:" + getColumnComments(tableName));
//            System.out.println("----------------------" + tableName + "--------------------------");
//        }
        for(String tableName : getTableNames()){
//            createOneTable(tableName);
            createRoute(tableName);
        }

    }

    public static void createRoute(String tableName){
        List<String> columnNames = getColumnNames(tableName);
        List<String> columnTypes = getColumnTypes(tableName);
        List<String> columnComments = getColumnComments(tableName);
        TableBean tb = createTableBean(tableName, columnNames, columnTypes, columnComments);
        String string = "routes.add(\"/mgr/works/pc/" + StringUtil.toLowerCaseFirstOne(tb.getName()) + "Ctrl\", " + tb.getName() + "Ctrl.class);";
        System.out.println(string);
    }

    public static void createOneTable(String tableName) throws Exception{
        List<String> columnNames = getColumnNames(tableName);
        List<String> columnTypes = getColumnTypes(tableName);
        List<String> columnComments = getColumnComments(tableName);

        TableBean tb = createTableBean(tableName, columnNames, columnTypes, columnComments);
        String ctrlString = getTemplate("ctrl.html");
        String string = createCtrl(ctrlString, tb);
        createFile(string, tb.getName() + "Ctrl.java", "123/Ctrl");

        String servicesString = getTemplate("service.html");
        string = createService(servicesString, tb);
        createFile(string, tb.getName() + "Service.java", "123/Service");
    }

    public static String getTemplate(String path) throws Exception{
        String filePath = Main.class.getClassLoader().getResource("createCode/" + path).getFile();
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String ctrlString = "";
        String string = br.readLine();
        while (string != null){
            ctrlString += string + "\n";
            string = br.readLine();
        }
        return ctrlString;
    }

    public static String createCtrl(String string, TableBean tableBean){
        string = string.replace(name, tableBean.getName());
        return string;
    }

    public static String createService(String string, TableBean tableBean){
        /*
        private static final String name = "{{name}}";
    private static final String columnNameArr = "{{columnNameArr}}";
    private static final String columnTypeArr = "{{columnTypeArr}}";
    private static final String columnCommentArr = "{{columnCommentArr}}";
    private static final String tableName = "{{tableName}}";
         */
        string = string.replace(name, tableBean.getName());
        string = string.replace(tableName, tableBean.getTableName());
        String columnName = "";
        String columnType = "";
        String columnComment = "";
        List<Column> columnList = tableBean.getColumnList();
        for(Column c : columnList){
            columnName += "\"" + c.getName() + "\",";
            columnType += "\"" + c.getType() + "\",";
            columnComment += "\"" + c.getComment() + "\",";
        }
        columnName = columnName.substring(0, columnName.length() - 1);
        columnType = columnType.substring(0, columnType.length() - 1);
        columnComment = columnComment.substring(0, columnComment.length() - 1);
        string = string.replace(columnNameArr, columnName);
        string = string.replace(columnTypeArr, columnType);
        string = string.replace(columnCommentArr, columnComment);
        return string;
    }

    public static TableBean createTableBean(String tableName, List<String> columnNames, List<String> columnTypes, List<String> columnComments){
        return new TableBean(tableName, columnNames.toArray(new String[columnNames.size()]), columnTypes.toArray(new String[columnNames.size()]), columnComments.toArray(new String[columnNames.size()]));
    }

    public static void createFile(String string, String fileName, String dirName) throws Exception{
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File com = fsv.getHomeDirectory();
        String path = com.getPath() + "/" + dirName;
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File outFile = new File(path  + "/" + fileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        bw.write(string);
        bw.close();
    }

}
