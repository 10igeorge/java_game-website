import java.util.*;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import java.lang.Thread;

import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Turn.delete();
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/next-turn", (request, response) -> {
      Turn.resetShownStatus();
      Turn.deleteUserGuess();
      Turn newTurn = new Turn();
      newTurn.save();
      response.redirect("/replay");
      return null;
      });

    get("/replay", (request, response) -> {
      if (Turn.allShown() == false) {
        Turn unshownTurn = Turn.getNextUnshownTurn();
        String color = unshownTurn.getGeneratedColor();
        unshownTurn.updateShownStatus();
        if(color.equals("red")) {
          response.redirect("/red");
          return null;
        } else if(color.equals("yellow")) {
          response.redirect("/yellow");
          return null;
        } else if(color.equals("green")) {
          response.redirect("/green");
          return null;
        } else if(color.equals("blue")) {
          response.redirect("/blue");
          return null;
        } else {
          response.redirect("/error-replay");
          return null;
        }
      } else {
        response.redirect("/play");
        return null;
        }
    });

    get("/red", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/red.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/yellow", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/yellow.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/green", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/green.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/blue", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/blue.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/play", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      if (Turn.isFull()){
        response.redirect("/next-turn");
        return null;
      }
      model.put("template", "templates/play.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    post("/play", (request, response) -> {
      if (!Turn.isFull()) {
        Turn currentTurn = Turn.getCurrentTurn();
        String userGuess = request.queryParams("color");
        currentTurn.update(userGuess);
        if (currentTurn.checkGuess()){
          response.redirect("/play");
          return null;
        }
        response.redirect("/gameover");
        return null;
      }
      response.redirect("/next-turn");
      return null;
    });

    get("/gameover", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/gameover.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

  } //end of main
} //end of app
