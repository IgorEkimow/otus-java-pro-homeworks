package ru.otus.java.pro.homeworks.homework3;

public class ExampleTests {
    private int value;

    @Before
    private void setUp() {
        value = 10;
        System.out.println("  before: value = " + value);
    }

    @Test
    public void addsNumbers() {
        if (value + 5 != 15) {
            throw new AssertionError("Ожидалось 15");
        }
    }

    @Test
    public void eachTestGetsFreshInstance() {
        if (value != 10) {
            throw new AssertionError("Before должен заново установить value = 10");
        }
    }

    @Test
    public void demonstratesFailureIsolation() {
        throw new IllegalStateException("Демонстрация упавшего теста");
    }

    @After
    private void tearDown() {
        System.out.println("  after: test finished");
    }
}