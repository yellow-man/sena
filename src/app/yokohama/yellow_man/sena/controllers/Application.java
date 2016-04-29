package yokohama.yellow_man.sena.controllers;

import play.mvc.Controller;
import play.mvc.Result;
import yokohama.yellow_man.sena.views.html.index;

/**
 * @author yellow-man
 * @since 1.0
 */
public class Application extends Controller {

	/**
	 * @return Result
	 * @since 1.0
	 */
	public static Result index() {
		return ok(index.render());
	}
}
