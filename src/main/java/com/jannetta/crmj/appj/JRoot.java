package com.jannetta.crmj.appj;
import com.google.gson.Gson;
import com.jannetta.crmj.appj.controllers.IndexController;
import com.jannetta.crmj.appj.nonhibernate.NonHibernateQueries;
import com.jannetta.crmj.appj.util.JsonTransformer;
import spark.Spark;


public class JRoot {
    JPropertiesManager propertiesManager = new JPropertiesManager();
    private Gson gson = new Gson();

    public JRoot() {
        Spark.port(Integer.valueOf(propertiesManager.getProperty("server.port")));
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
        Spark.staticFiles.location("/static");

        Spark.get("/", IndexController.serveIndexPage);
        Spark.post("/getPeople", NonHibernateQueries::getPeople);
        Spark.post("/getPerson", NonHibernateQueries::getPerson, new JsonTransformer());
        Spark.post("/addPerson", NonHibernateQueries::addPerson);
        Spark.post("/removePerson", NonHibernateQueries::removePerson);
        Spark.post("/updatePerson", NonHibernateQueries::updatePerson);
        Spark.post("/getPersonCount", NonHibernateQueries::getPersonCount);
        Spark.post("/getAddresses", NonHibernateQueries::getAddresses);
        Spark.post("/getAddress", NonHibernateQueries::getAddress);
        Spark.post("/addAddress", NonHibernateQueries::addAddress);
        Spark.post("/removeLinkedAddress", NonHibernateQueries::removeLinkedAddress);
        Spark.post("/updateAddress", NonHibernateQueries::updateAddress);
    }


}
