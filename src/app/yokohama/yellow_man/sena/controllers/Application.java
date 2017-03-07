package yokohama.yellow_man.sena.controllers;

import play.mvc.Controller;
import play.mvc.Result;
import yokohama.yellow_man.sena.views.html.index;

/**
 * Play自動生成のコントローラ。
 * <p>いったん放置。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 */
public class Application extends Controller {

	/**
	 * Play自動生成のindexアクション。
	 * いったん放置。
	 *
	 * @return Result
	 * @since 1.0.0-1.0
	 */
	public static Result index() {
		return ok(index.render());
	}
}
