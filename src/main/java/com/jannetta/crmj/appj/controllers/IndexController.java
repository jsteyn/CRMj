package com.jannetta.crmj.appj.controllers;

import com.jannetta.crmj.appj.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

public class IndexController {


    public static Route serveIndexPage = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "static/jtemplates/index.vm");

    };
}
