package org.odinallfather.odinsworld.util;

import java.util.List;

public class Tripple<A, B, C> {

    private final A a;
    private final B b;
    private final C c;

    public Tripple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    public static <A, B, C> Tripple<A, B, C> containsAndGet(A a, B b, C c, List<Tripple<A, B, C>> list) {
        A a1 = null;
        B b1 = null;
        C c1 = null;

        for(Tripple<A, B, C> t : list) {
            if(t.getA().equals(a) && t.getB().equals(b) && t.getC().equals(c)) {
                a1 = t.getA();
                b1 = t.getB();
                c1 = t.getC();
                break;
            }
        }
        return new Tripple<>(a1, b1, c1);
    }

    public static <A, B, C> A findA(List<Tripple<A, B, C>> list, A a) {
        for(Tripple<A, B, C> t : list) {
            if(t.getA().equals(a))
                return t.getA();
        }
        return null;
    }

    public static <A, B, C> B findB(List<Tripple<A, B, C>> list, B b) {
        for(Tripple<A, B, C> t : list) {
            if(t.getB().equals(b))
                return t.getB();
        }
        return null;
    }

    public static <A, B, C> C findC(List<Tripple<A, B, C>> list, C c) {
        for(Tripple<A, B, C> t : list) {
            if(t.getC().equals(c))
                return t.getC();
        }
        return null;
    }

    public static <A, B, C> boolean isOneNull(Tripple<A, B, C> tripple) {
        return tripple.getA() == null || tripple.getB() == null || tripple.getC() == null;
    }
}
