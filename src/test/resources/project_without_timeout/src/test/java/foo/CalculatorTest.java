package foo;

import org.junit.Test;

public class CalculatorTest {

    @Test
    public void should_add_two_numbers() {
        assert 5 == new foo.Calculator().add(2, 3);
    }

    @Test
    public void should_subtract_a_number_from_another() {
        assert 2 == new foo.Calculator().subtract(3, 1);
    }
}