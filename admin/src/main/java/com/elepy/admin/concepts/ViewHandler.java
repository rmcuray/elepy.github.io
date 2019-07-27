package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.annotations.View;
import com.elepy.admin.views.DefaultView;
import com.elepy.admin.views.FileView;
import com.elepy.describers.Model;
import com.elepy.http.HttpService;
import com.elepy.uploads.FileReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewHandler {


    private final HttpService http;
    private ElepyAdminPanel adminPanel;
    private Map<Model<?>, ModelView> models;

    public ViewHandler(ElepyAdminPanel adminPanel, HttpService http) {
        this.adminPanel = adminPanel;
        this.http = http;
    }


    public void setupModels(ElepyPostConfiguration elepyPostConfiguration) {
        this.models = mapModels(elepyPostConfiguration);
    }


    public void initializeRoutes(ElepyPostConfiguration elepyPostConfiguration) {

        for (Model<?> model : models.keySet()) {
            http.get("/admin/config" + model.getSlug(), (request, response) -> {
                response.type("application/json");
                response.result(elepyPostConfiguration.getObjectMapper().writeValueAsString(
                        model
                ));
            });
        }

        models.forEach((elepyModel, modelView) -> {

            http.get("/admin" + elepyModel.getSlug(), (request, response) -> {

                Map<String, Object> model = new HashMap<>();

                String content = modelView.renderView(request, elepyModel);

                Document document = Jsoup.parse(content);

                Elements styles = document.select("style");
                Elements stylesheets = document.select("stylesheet");

                stylesheets.remove();
                styles.remove();


                model.put("styles", styles);
                model.put("stylesheets", stylesheets.stream().map(sheet -> {
                    if (sheet.hasText()) {
                        return sheet.text();
                    } else if (sheet.hasAttr("src")) {
                        return sheet.attr("src");
                    }
                    return "";
                }).collect(Collectors.toSet()));
                model.put("content", document.body().html());
                model.put("model", elepyModel);
                response.result(adminPanel.renderWithDefaults(model, "admin-templates/model.peb"));
            });
        });
    }

    private Map<Model<?>, ModelView> mapModels(ElepyPostConfiguration elepyPostConfiguration) {
        Map<Model<?>, ModelView> modelsToReturn = new HashMap<>();

        elepyPostConfiguration.getModelDescriptions()
                .forEach(model -> modelsToReturn.put(model, getViewFromModel(model, elepyPostConfiguration)));

        return modelsToReturn;
    }

    private ModelView getViewFromModel(Model<?> model, ElepyPostConfiguration elepyPostConfiguration) {
        if (model.getJavaClass().equals(FileReference.class)) {
            return new FileView();
        } else if (model.getJavaClass().isAnnotationPresent(View.class)) {
            final View annotation = model.getJavaClass().getAnnotation(View.class);
            return elepyPostConfiguration.initializeElepyObject(annotation.value());
        } else {
            return elepyPostConfiguration.initializeElepyObject(DefaultView.class);
        }
    }

}
