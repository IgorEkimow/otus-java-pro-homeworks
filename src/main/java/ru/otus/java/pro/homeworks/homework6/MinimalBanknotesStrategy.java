package ru.otus.java.pro.homeworks.homework6;

import java.util.EnumMap;
import java.util.Map;

public class MinimalBanknotesStrategy implements WithdrawalStrategy {
    @Override
    public Map<Denomination, Integer> plan(int amount, Map<Denomination, Integer> available) {
        if (amount <= 0) {
            throw new CannotWithdrawException("Сумма должна быть положительной");
        }

        Denomination[] desc = Denomination.values();
        Map<Denomination, Integer> result = new EnumMap<>(Denomination.class);

        int remaining = amount;

        for (int i = desc.length - 1; i >= 0; i--) {
            Denomination d = desc[i];
            int have = available.getOrDefault(d, 0);
            if (have == 0) continue;

            int need = remaining / d.value();
            int take = Math.min(need, have);

            if (take > 0) {
                result.put(d, take);
                remaining -= take * d.value();
            }
        }

        if (remaining != 0) {
            throw new CannotWithdrawException("Невозможно выдать сумму " + amount + " доступными номиналами");
        }

        return result;
    }
}