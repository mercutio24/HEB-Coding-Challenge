import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static void createProductsTable() {
        String dropTable = "DROP TABLE products";

        StringBuilder createTable = new StringBuilder("CREATE TABLE products ");
        createTable.append("(id int primary key, ")
                .append("description varchar(20) not null, ")
                .append("last_sold date not null, ")
                .append("shelf_life int not null, ")
                .append("department varchar(12) not null, ")
                .append("price decimal(5,2) not null, ")
                .append("unit varchar(4) not null, ")
                .append("x_for int not null, ")
                .append("cost decimal(5,2) not null)");

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:derby:../../../../Users/Liam/IdeaProjects/products;create=true");
            stmt = conn.createStatement();

            try {
                stmt.execute(dropTable);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            stmt.execute(createTable.toString());
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

    private static List<String> getInserts() {
        BufferedReader dbInput = null;
        List<String> inserts = new ArrayList<>();

        try {
            dbInput = new BufferedReader(new FileReader("C:\\Users\\Liam\\IdeaProjects\\HEB coding exercise\\Product Search\\products.csv"));
            dbInput.readLine(); // Skip the header line

            String line;

            while ((line = dbInput.readLine()) != null) {
                String[] fields = line.split(",");
                StringBuilder insertQuery = new StringBuilder("INSERT INTO PRODUCTS VALUES(");

                /*
                for (String field : fields) {
                    insertQuery.append("'").append(field.trim()).append("'").append(", ");
                }
                */

                insertQuery.append(fields[0].trim()).append(", ");
                insertQuery.append("'").append(fields[1].trim()).append("'").append(", ");
                insertQuery.append("'").append(fields[2].trim()).append("'").append(", ");
                insertQuery.append(fields[3].trim().replaceAll("[^0-9]", "")).append(", ");
                insertQuery.append("'").append(fields[4].trim()).append("'").append(", ");
                insertQuery.append(fields[5].trim().replaceAll("[^0-9\\.]", "")).append(", ");
                insertQuery.append("'").append(fields[6].trim()).append("'").append(", ");
                insertQuery.append(fields[7].trim().replaceAll("[^0-9]", "")).append(", ");
                insertQuery.append(fields[8].trim().replaceAll("[^0-9\\.]", "")).append(")");
                //insertQuery.delete(insertQuery.length() - 2, insertQuery.length()); // Remove the trailing comma and space

                inserts.add(insertQuery.toString());
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

        return inserts;
    }

    private static void populateProductsTable() {
        List<String> inserts = getInserts();
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:derby:../../../../Users/Liam/IdeaProjects/products;create=true");
            stmt = conn.createStatement();

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

    public static void buildDB() {
        createProductsTable();
        populateProductsTable();
    }

    public static JSONArray getProducts(ByField byField, String queryValue, String queryValue2) {
        JSONArray results = new JSONArray();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products");

        Map<ByField, String> queryBodies = new HashMap<>();
        queryBodies.put(ByField.PRODUCT_ID, " WHERE id = ?");
        queryBodies.put(ByField.DESCRIPTION, " WHERE LOWER(description) LIKE ? ORDER BY LOWER(description)");
        queryBodies.put(ByField.DEPARTMENT, " WHERE LOWER(department) = ? ORDER BY id");
        queryBodies.put(ByField.LAST_SOLD_DATE, " WHERE last_sold BETWEEN ? AND ? ORDER BY last_sold");
        queryBodies.put(ByField.SHELF_LIFE, " WHERE shelf_life BETWEEN ? AND ? ORDER BY shelf_life");
        queryBodies.put(ByField.PRICE, " WHERE price BETWEEN ? AND ? ORDER BY price");
        queryBodies.put(ByField.COST, " WHERE cost BETWEEN ? AND ? ORDER BY cost");
        queryBodies.put(ByField.ALL, " ORDER BY id");

        queryBuilder.append(queryBodies.get(byField));

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
                    selectProducts.setInt(1, Integer.valueOf(queryValue));
                    break;
                case DESCRIPTION:
                    selectProducts.setString(1, "%" + queryValue + "%");
                    break;
                case DEPARTMENT:
                    selectProducts.setString(1, queryValue);
                    break;
                case LAST_SOLD_DATE:
                    selectProducts.setDate(1, Date.valueOf(queryValue));
                    selectProducts.setDate(2, Date.valueOf(queryValue2));
                    break;
                case SHELF_LIFE:
                    selectProducts.setInt(1, Integer.valueOf(queryValue));
                    selectProducts.setInt(2, Integer.valueOf(queryValue2));
                case PRICE:
                    selectProducts.setFloat(1, Float.valueOf(queryValue));
                    selectProducts.setFloat(2, Float.valueOf(queryValue2));
                case COST:
                    selectProducts.setFloat(1, Float.valueOf(queryValue));
                    selectProducts.setFloat(2, Float.valueOf(queryValue2));
                default:    // Don't need to set anything if we're getting all products
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

            products = selectProducts.executeQuery();

            while (products.next()) {
                JSONArray product = new JSONArray();

                product.put(products.getInt(1));
                product.put(products.getString(2));
                product.put(products.getDate(3));
                product.put(products.getInt(4));
                product.put(products.getString(5));
                product.put(products.getFloat(6));
                product.put(products.getString(7));
                product.put(products.getString(8));
                product.put(products.getFloat(9));

                /*
                for (int i = 1; i < 10; i++) {
                    product.put(products.getString(i));
                }
                */

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
