package ru.otus.java.pro.homeworks.homework15;

public class Main {
    private final Object monitor = new Object();
    private int turn = 1;
    private int number = 1;
    private int direction = 1;
    private boolean stopped;

    public static void main(String[] args) {
        Main sequence = new Main();
        Thread first = new Thread(() -> sequence.printNumbers(1), "Поток 1");
        Thread second = new Thread(() -> sequence.printNumbers(2), "Поток 2");

        first.start();
        second.start();

        try {
            first.join();
            second.join();
        } catch (InterruptedException e) {
            first.interrupt();
            second.interrupt();
            Thread.currentThread().interrupt();
        }
    }

    private void printNumbers(int threadNumber) {
        synchronized (monitor) {
            try {
                while (!stopped && !Thread.currentThread().isInterrupted()) {
                    while (!stopped && turn != threadNumber) {
                        monitor.wait();
                    }

                    if (stopped || Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    System.out.println(Thread.currentThread().getName() + ": " + number);

                    if (threadNumber == 2) {
                        if (number == 10) {
                            direction = -1;
                        } else if (number == 1) {
                            direction = 1;
                        }

                        number += direction;
                    }

                    turn = 3 - threadNumber;
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                stopped = true;
                monitor.notifyAll();
            }
        }
    }
}