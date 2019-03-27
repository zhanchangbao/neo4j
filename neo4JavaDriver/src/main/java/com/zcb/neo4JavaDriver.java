package com.zcb;

import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * 使用官方驱动包：
 * 每个Neo4j驱动程序都有一个用于创建驱动程序的数据库对象。一般按照以下操作步骤：
 * 1)  向数据库对象请求一个新的驱动程序；
 * 2)  向驱动程序对象请求一个新会话；
 * 3)  请求会话对象创建事务；
 * 4)  使用事务对象运行语句。返回一个表示结果的对象；
 * 5)  处理结果；
 * 6)  关闭会话。
 */

public class neo4JavaDriver implements AutoCloseable{

    private final Driver driver;

    public neo4JavaDriver(String url, String user, String password) {
        driver = GraphDatabase.driver(url, AuthTokens.basic(user,password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void printGreeting(final String message){
        try (final Session session = driver.session()){
            String greeting = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction transaction) {
                    StatementResult result = transaction.run
                            ( "CREATE (a:test) " + "SET a.message = $message "
                                    + "RETURN a.message + ', from node ' + id(a)",
                                    parameters( "message", message ) );
                    return result.single().get(0).asString();

                    /*StatementResult result = transaction.run(
                            "CREATE (a:Person {name;{name},title:{title}})",parameters("name","Arthur","title","King")
                    );

                   StatementResult result1 = transaction.run(
                           "MATCH (a:Person) WHERE a.name = {name}" + "RETURN a.name AS name,a.title AS title",parameters("name","Arthur")
                   );

                   while (result1.hasNext()){
                       Record record = result1.next();
                       System.out.println(record.get("title").asString() + " " + record.get("name").asString());
                   }*/
                }
            });
            session.close();
            System.out.println(greeting);
        }
    }

    public static void main(String[] args) throws Exception {
        neo4JavaDriver greeter = new neo4JavaDriver("bolt://10.12.64.244:7687", "neo4j", "123456");

        greeter.printGreeting("test");
        greeter.close();
    }
}

