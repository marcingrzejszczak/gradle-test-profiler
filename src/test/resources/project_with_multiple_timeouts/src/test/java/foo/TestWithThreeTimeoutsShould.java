package foo;

import org.junit.Test;

public class TestWithThreeTimeoutsShould extends AbstractTest {

    @Test
    public void should_fail_because_of_timeout() throws Exception {
        Thread.sleep(100);
    }
}