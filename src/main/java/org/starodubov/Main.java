package org.starodubov;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.ToIntFunction;

record Promo(Integer a) {
}
public class Main {
    public static void main(String[] args) throws Exception {

        var a = List.of(new Promo(2), new Promo(10))
                .stream()
                .sorted(Comparator.comparingInt((ToIntFunction<Promo>) value -> {
                    if (value.a() == null) {
                        return 50;
                    }
                    return value.a();
                }).reversed())
                .toList();
        System.out.println(a);
    }
}