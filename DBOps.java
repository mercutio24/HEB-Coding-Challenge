import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBOps {
    /*
        //String[] result1s = {"753542", "banana", "9/5/2017", "4d", "Produce", "$2.99", "lb", "1", "$0.44"};
        //String[] result2s = {"321654", "apples", "9/6/2017", "7d", "Produce", "$1.49", "lb", "1", "$0.20"};

        //JSONArray result1 = new JSONArray(result1s);
        //JSONArray result2 = new JSONArray(result2s);

        //results.put(result1);
        //results.put(result2);

        //List<String> results = new ArrayList<String>();

        //String[] result = {"753542", "banana", "9/5/2017", "4d", "Produce", "$2.99", "lb", "1", "$0.44"};

        //for (String field : result) {
        //    results.add(field);
        //}

        //for (int i = 0; i < result.length; i++) {
        //    out.write(results.get(i));
        //   out.write(",");
        //}
    }
    */

    public static void buildDB() {
        BufferedReader dbInput = null;

        try {
            dbInput = new BufferedReader(new FileReader("D:\\OneDrive\\Documents\\Skills tests\\HEB\\products.csv"));

            String dropTable = "DROP TABLE products";

            StringBuilder createTable = new StringBuilder("CREATE TABLE products ");
            createTable.append("(id varchar(8) primary key, ")
                    .append("description varchar(20) not null, ")
                    .append("last_sold date not null, ")
                    .append("shelf_life varchar(4) not null, ")
                    .append("department varchar(12) not null, ")
                    .append("price varchar(8) not null, ")
                    .append("unit varchar(4) not null, ")
                    .append("x_for varchar(3) not null, ")
                    .append("cost varchar(8) not null)");

            List<String> inserts = new ArrayList<String>();
            String line;

            dbInput.readLine(); // Skip the header line

            while ((line = dbInput.readLine()) != null) {
                String[] fields = line.split(",");
                StringBuilder insertQuery = new StringBuilder("INSERT INTO PRODUCTS VALUES(");

                for (String field : fields) {
                    insertQuery.append("'").append(field.trim()).append("'").append(", ");
                }

                insertQuery.delete(insertQuery.length() - 2, insertQuery.length()); // Remove the trailing comma and space
                insertQuery.append(")");

                inserts.add(insertQuery.toString());
            }

            Connection conn = null;
            Statement stmt = null;

            try {
                conn = DriverManager.getConnection("jdbc:derby:../../../../Users/Liam/IdeaProjects/products;create=true");
                stmt = conn.createStatement();

                try {
                    stmt.execute(dropTable);
                }
                catch (SQLException e) {
                    System.out.println("Trying to drop a non-existent products table");
                }

                stmt.execute(createTable.toString());

                for (String insert : inserts) {
                    stmt.execute(insert);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    stmt.close();
                    conn.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                dbInput.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONArray getProducts(ByField byField, String queryValue) {
        JSONArray results = new JSONArray();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products");

        switch(byField) {
            case PRODUCT_ID:
                queryBuilder.append(" WHERE id = ?");
                break;
            case DESCRIPTION:
                queryBuilder.append(" WHERE LOWER(description) LIKE ?");
                break;
            case DEPARTMENT:
                queryBuilder.append(" WHERE LOWER(department) = ?");
                break;
            default:    // Do nothing if getting all products
        }

        /*
        boolean firstTerm = true;
        boolean hasId = (id != 0);
        boolean hasDescription = (!description.equals(""));
        boolean hasDepartment = (!department.equals(""));

        if (hasId || hasDescription || hasDepartment) {
            queryBuilder.append(" WHERE ");

            String conjunction = usesAllTerms? "AND" : "OR";

            if (hasId) {
                queryBuilder.append("id = ?");
                firstTerm = false;
            }

            if (hasDescription) {
                if (!firstTerm) {
                    queryBuilder.append(" " + conjunction + " ");
                    firstTerm = false;
                }

                queryBuilder.append("description like ?");
            }

            if (hasDepartment) {
                if (!firstTerm) {
                    queryBuilder.append(" " + conjunction + " ");
                }

                queryBuilder.append("department = ?");
            }
        }
        */

        Connection conn = null;
        PreparedStatement selectProducts = null;
        ResultSet products = null;

        try {
            conn = DriverManager.getConnection("jdbc:derby:../../../../Users/Liam/IdeaProjects/products;create=true");
            selectProducts = conn.prepareStatement(queryBuilder.toString());

            switch (byField) {
                case PRODUCT_ID:
                    selectProducts.setString(1, queryValue);
                    break;
                case DESCRIPTION:
                    selectProducts.setString(1, "%" + queryValue + "%");
                    break;
                case DEPARTMENT:
                    selectProducts.setString(1, queryValue);
                    break;
                default:
            }

            /*
            int queryIndex = 0;

            if (hasId) {
                selectProducts.setInt(queryIndex++, id);
            }

            if (hasDescription) {
                selectProducts.setString(queryIndex++, "%" + description + "%");
            }

            if (hasDepartment) {
                selectProducts.setString(queryIndex++, department);
            }
            */

            System.out.println("%" + queryValue + "%");
            products = selectProducts.executeQuery();

            while (products.next()) {
                JSONArray product = new JSONArray();

                for (int i = 1; i < 10; i++) {
                    product.put(products.getString(i));
                }

                results.put(product);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                products.close();
                selectProducts.close();
                conn.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
