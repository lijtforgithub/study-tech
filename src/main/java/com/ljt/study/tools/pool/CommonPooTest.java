package com.ljt.study.tools.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author LiJingTang
 * @date 2025-08-01 15:07
 */
public class CommonPooTest {

    public static void main(String[] args) throws Exception {
        GenericObjectPool<MyClass> pool = new GenericObjectPool<>(new MyFactory());

        // Borrow an object from the pool
        MyClass obj = pool.borrowObject();
        try {
            // Use the pooled object
            obj.use();
        } finally {
            // Return the object to the pool
            pool.returnObject(obj);
        }
    }

    static class MyFactory extends BasePooledObjectFactory<MyClass> {
        @Override
        public MyClass create() throws Exception {
            return new MyClass();
        }

        @Override
        public PooledObject<MyClass> wrap(MyClass obj) {
            return new DefaultPooledObject<>(obj);
        }
    }
}

class MyClass {
    public void use() {
        System.out.println("Using MyClass instance");
    }
}
