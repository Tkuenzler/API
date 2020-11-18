package Database.Query;

public class Query {
	public class Order {
		public static final String ASCENDING = "ASC";
		public static final String DESCENDING = "DESC";
	}
	private StringBuilder query;

    /**
     *
     * @param table
     * @return
     */
    public Query delete(String table){
        query = new StringBuilder();
        query.append("DELETE FROM ");
        query.append(table);
        return this;
    }

    /**
     * Adds the WHERE condition to the SQL query
     * @param requirement
     * @return
     */
    public Query where(String requirement){
    	query.append(" WHERE ");
        query.append(requirement);
        return this;
    }

   /**
     *
     * @param table
     * @return
     */
    public Query from(String table){
        query.append(" FROM ");
        query.append(table);
        return this;
    }
    
    public Query update(String table){
        query = new StringBuilder();
        query.append("UPDATE ");
        query.append(table);
        query.append(" SET ");
        return this;
    }

    /**
     * Adds columns
     * @param Columns
     */
    public Query set(String[] columns){
        int count = columns.length;
        if(count == 0)
            throw new IllegalArgumentException("Invalid argument count");

        for(String column : columns){
           query.append(column);
           query.append(" = ");
           query.append("?");
           query.append(",");
        }
        query.deleteCharAt(query.lastIndexOf(","));
        return this;
    }
    public Query insert(String table){
        query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(table);
        return this;
    }

    /**
     *
     * @param params
     * @return
     */
    public Query columns(String[] columns) {
    	int count = columns.length;

        if(count == 0)
            throw new IllegalArgumentException("Invalid parameter count");
        
        query.append(" (");
        for(String column: columns)
        	query.append(column+",");
        //removes the last comma
        query.deleteCharAt(query.lastIndexOf(","));
        query.append(")");
        return this;
    }
    
    /**
    *
    * @param params
    * @return
    */
    public Query orderby(String[] columns,String[] order) {
    	query.append(" ORDER BY ");
    	 int count_columns = columns.length;
    	 int count_order = order.length;

         if(count_columns == 0 || count_order==0 || count_columns!=count_order)
             throw new IllegalArgumentException("Invalid parameter count");
         
    	for(int i = 0;i<columns.length;i++) { 
    		String c = columns[i];
    		String o = order[i];
    		query.append(c+" "+o+",");
    	}
    	query.deleteCharAt(query.lastIndexOf(","));
    	return this;
    }
    public Query values(Object[] params){
        query.append(" VALUES(");

        int count = params.length;

        if(count == 0)
            throw new IllegalArgumentException("Invalid parameter count");

        for (int i = 0; i<count; i++) {
            query.append("?,");
        }
        //removes the last comma
        query.deleteCharAt(query.lastIndexOf(","));
        query.append(")");
        return this;
    }
    /**
    *
    * @param columns
    * @return
    */
    public Query select(Object[] columns){
        query = new StringBuilder();
        query.append("SELECT ");
        if(columns != null){
            for(Object column : columns){
                query.append(column);
                query.append(",");
            }
            //removes the last question mark
            query.deleteCharAt(query.lastIndexOf(","));
        }
        else
            query.append("*");

        return this;
    }
    /**
    *
    * @param columnName
    * @return
    */
    public Query as(String columnName) {
    	query.append(" AS "+columnName);
    	return this;
    }
    
    public String getQuery() {
    	return query.toString();
    }
}
