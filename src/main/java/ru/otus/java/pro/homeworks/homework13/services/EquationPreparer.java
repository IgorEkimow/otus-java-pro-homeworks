package ru.otus.java.pro.homeworks.homework13.services;

import java.util.List;
import ru.otus.java.pro.homeworks.homework13.model.Equation;

public interface EquationPreparer {
    List<Equation> prepareEquationsFor(int base);
}