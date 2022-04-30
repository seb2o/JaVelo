package ch.epfl.javelo.gui;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ListInvalidationSample {

    public static void main(String[] args) {
        ObservableList<String> list = FXCollections.observableArrayList("one", "two");
        list.addListener((ListChangeListener<? super String>) c ->{
            c.next();
            System.out.println(c.getFrom());
                }
        );

        list.set(1,"to");
        list.set(0,"jefl");
        System.out.println(("Before replacing one with one"));
        list.add("kkf");
        list.remove("jefl");
        System.out.println(list);


    }
}
