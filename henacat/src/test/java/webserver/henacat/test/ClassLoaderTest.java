package webserver.henacat.test;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author Yoshimasa Tanabe
 */
public class ClassLoaderTest {
  
  @Test
  public void test() throws Exception {
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath("classloadertest");
    URLClassLoader loader = URLClassLoader.newInstance(new URL[]{path.toUri().toURL()});

    Class<?> clazz = loader.loadClass("HelloWorld");
    Object helloWorld = clazz.newInstance();
    Method hello = clazz.getMethod("hello");
    Assert.assertThat(hello.invoke(helloWorld), is("Hello, World!"));
  }

}
