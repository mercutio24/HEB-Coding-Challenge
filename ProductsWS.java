import org.json.JSONArray;

import java.io.*;
import javax.servlet.http.*;

enum ByField {
    ID, DESCRIPTION, DEPARTMENT
}

public class ProductsWS extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        ByField byField = null;

        switch (request.getParameter("byField")) {
            case "id":
                byField = ByField.ID;
                break;
            case "description":
                byField = ByField.DESCRIPTION;
                break;
            case "department":
                byField = ByField.DEPARTMENT;
                break;
            default:
                ;
        }

        String queryValue = request.getParameter("queryValue");
        JSONArray results = DBOps.getProducts(byField, queryValue);

        out.write(results.toString());
        out.close();
    }

    public void init() {
        DBOps.buildDB();
    }
}