import org.json.JSONArray;

import java.io.*;
import javax.servlet.http.*;

enum ByField {
    PRODUCT_ID, DESCRIPTION, DEPARTMENT, ALL
}

public class ProductsWS extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        ByField byField = null;

        switch (request.getParameter("byField")) {
            case "productId":
                byField = ByField.PRODUCT_ID;
                break;
            case "description":
                byField = ByField.DESCRIPTION;
                break;
            case "department":
                byField = ByField.DEPARTMENT;
                break;
            default:
                byField = ByField.ALL;
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