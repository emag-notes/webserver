package webserver.henacat.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Yoshimasa Tanabe
 */
public interface ResponseHeaderGenerator {

  void generate(OutputStream output) throws IOException;

}
