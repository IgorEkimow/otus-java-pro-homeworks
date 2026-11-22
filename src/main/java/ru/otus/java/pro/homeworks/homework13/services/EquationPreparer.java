package ru.otus.java.pro.homeworks.homework13.services;

import java.util.List;
import ru.otus.model.Equation;

public interface EquationPreparer {
    List<Equation> prepareEquationsFor(int base);
}
