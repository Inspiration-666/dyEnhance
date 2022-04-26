package com.inspire.dyenhance.runagent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：inspire
 * @date ：Created in 2022/4/18 9:49
 * @description：
 * @modified By：inspire
 * @version:
 */
public class ClassAdapter implements ClassFileTransformer {
    private static final Logger logger = LoggerFactory.getLogger(ClassAdapter.class);
    private static ConcurrentHashMap<ClassLoader, ClassPool> CLASS_POOL_MAP = new ConcurrentHashMap<ClassLoader, ClassPool>();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if(loader==null||className==null){
            return classfileBuffer;
        }


        if("com.inspire.TomcatWebProjJdk8.HelloServlet".replace(".","/").equals(className)){
            className=className.replace("/",".");
//            logger.info("############# agent1 Can-Retransform-Classes --- level ---> second");
            CtClass ctclass;
            try {
                ClassPool pool = getClassPool(loader);
                ctclass=pool.get(className);
                for(CtMethod method : ctclass.getMethods()){
                    logger.info("********* MethodName:[ {} ] ,Sign:[ {} ]",method.getName(),method.getSignature());
                    if("finallyTest".equals(method.getName())){

                        String code="System.out.println(\"------------------------------------------------------------finallyTest\");";
                        method.insertBefore(code);
                        logger.info("class ******************** finallyTest insert modify successful!");

                    }
                }
                return ctclass.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return new byte[0];
    }

    //处理web情况下类加载器的情况
    public synchronized static ClassPool getClassPool(ClassLoader loader) {
        if (null == loader) {
            return ClassPool.getDefault();
        }

        ClassPool pool = CLASS_POOL_MAP.get(loader);
        if (null == pool) {
            pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(loader));
            CLASS_POOL_MAP.put(loader, pool);
        }
        return pool;
    }

    public static String outputClazz(byte[] bytes, String className, String pathName) {

        String[] split = className.split("\\.");
        FileOutputStream out = null;
        try {
            for (int i = 0; i < split.length - 1; i++) {
                pathName += split[i] + "/";
            }
            //创建文件夹
            File folders = new File(pathName);
            boolean exists = folders.exists();
            if (!exists) {
                folders.mkdirs();
            }
            pathName = pathName + split[split.length - 1] + ".class";

            File file=new File(pathName);
            if(!file.exists()){//保证只写一次文件
                out = new FileOutputStream(pathName);
                out.write(bytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) try {
                out.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return pathName;
    }
}
