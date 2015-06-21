/*
 * @author Rick Mugridge on Nov 7, 2004
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */
package rps.exception;


/**
 *
 */
public class DuplicateException extends RpsException {
	private static final long serialVersionUID = 3256444702902530870L;

	public DuplicateException(String s) {
        super(s);
    }
}
