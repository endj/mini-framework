package se.edinjakupovic.testconfigs.classes;

import se.edinjakupovic.lifecycle.OnInit;
import se.edinjakupovic.testconfigs.LifeCycleException;

public class ClassWithInitLifeCycle {

    @OnInit
    public void helloWorld() {
        throw new LifeCycleException("hello");
    }


}
