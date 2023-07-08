package se.edinjakupovic.testconfigs.classes;

import se.edinjakupovic.ApplicationContext;
import se.edinjakupovic.lifecycle.OnInit;
import se.edinjakupovic.testconfigs.LifeCycleException;

public class ClassWithInitLifeCycleAccessingContext {

    @OnInit
    public void helloWold2(ApplicationContext context) {
        String contextName = context.getClass().getSimpleName();
        ClassWithInitLifeCycle anotherClass = context.getInstance(ClassWithInitLifeCycle.class);
        throw new LifeCycleException(contextName);
    }

}
