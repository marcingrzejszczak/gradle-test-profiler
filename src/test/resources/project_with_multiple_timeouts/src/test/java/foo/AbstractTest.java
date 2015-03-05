package foo;

import org.junit.Rule;
import org.junit.rules.Timeout;

public abstract class AbstractTest {

    public @Rule Timeout timeout = new Timeout(10000);
}