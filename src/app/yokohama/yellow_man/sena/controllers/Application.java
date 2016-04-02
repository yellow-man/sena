package yokohama.yellow_man.sena.controllers;

import play.mvc.Controller;
import play.mvc.Result;
import yokohama.yellow_man.sena.views.html.index;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

}
