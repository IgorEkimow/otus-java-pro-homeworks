package ru.otus.java.pro.homeworks.homework1;

import java.util.List;
import com.google.common.collect.ImmutableList;

public class HelloOtus
{
    public List<String> unmodifiableList(List<String> strings) {
        return ImmutableList.copyOf(strings);
    }
}