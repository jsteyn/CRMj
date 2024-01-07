package com.jannetta.crmj.appj;
import com.google.gson.Gson;
import com.jannetta.crmj.appj.controllers.IndexController;
import com.jannetta.crmj.appj.nonhibernate.AddressQueries;
import com.jannetta.crmj.appj.nonhibernate.NonHibernateQueries;
import com.jannetta.crmj.appj.nonhibernate.PersonQueries;
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
        Spark.post("/getPeople", PersonQueries::getPeople);
        Spark.post("/getPerson", PersonQueries::getPerson, new JsonTransformer());
        Spark.post("/addPerson", PersonQueries::addPerson);
        Spark.post("/removePerson", PersonQueries::removePerson);
        Spark.post("/updatePerson", PersonQueries::updatePerson);
        Spark.post("/getPersonCount", PersonQueries::getPersonCount);
        Spark.post("/getAddresses", AddressQueries::getAddresses);
        Spark.post("/getAddress", AddressQueries::getAddress);
        Spark.post("/addAddress", AddressQueries::addAddress);
        Spark.post("/removeLinkedAddress", AddressQueries::removeLinkedAddress);
        Spark.post("/updateAddress", AddressQueries::updateAddress);
    }


}
