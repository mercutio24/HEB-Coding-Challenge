import org.json.JSONArray;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.*;

enum ByField {
    PRODUCT_ID, DESCRIPTION, DEPARTMENT, LAST_SOLD_DATE, SHELF_LIFE, PRICE, COST, ALL
}

public class ProductsWS extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Map<String, ByField> byFields = new HashMap<>();
        byFields.put("productId", ByField.PRODUCT_ID);
        byFields.put("description", ByField.DESCRIPTION);
        byFields.put("department", ByField.DEPARTMENT);
        byFields.put("lastSoldDate", ByField.LAST_SOLD_DATE);
        byFields.put("shelfLife", ByField.SHELF_LIFE);
        byFields.put("price", ByField.PRICE);
        byFields.put("cost", ByField.COST);
        byFields.put("all", ByField.ALL);

        ByField byField = byFields.get(request.getParameter("byField"));
        String queryValue = request.getParameter("queryValue");
        String queryValue2 = request.getParameter("queryValue2");
        JSONArray results = DBOps.getProducts(byField, queryValue, queryValue2);

        out.write(results.toString());
        out.close();
    }

    public void init() {
        DBOps.buildDB();
    }
}