/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package science.raketen.voodoo.context.singleton;

import java.util.concurrent.locks.ReentrantLock;
import science.raketen.voodoo.context.ContextualType;

/**
 * Manages the singleton contextual instance.
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class SingletonContextualType<T> extends ContextualType {

    private final ReentrantLock reentrantLock = new ReentrantLock();
    private T singleton;

    public SingletonContextualType(Class type) {
        super(type);
    }

    @Override
    public T getContextualInstance() {
        if (singleton == null) {
            initalizeSingleton();
        }
        return singleton;
    }

    private void initalizeSingleton() {
        reentrantLock.lock();
        if (singleton == null) {
            singleton = (T) createInstance(getType());
        }
        reentrantLock.unlock();
    }

}
