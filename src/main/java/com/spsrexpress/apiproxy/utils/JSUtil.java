package com.spsrexpress.apiproxy.utils;

import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Component
public class JSUtil {
    public Object JSInvokeFunction(String filePath, String funName,Object... args) throws ScriptException, NoSuchMethodException, FileNotFoundException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(new FileReader(filePath));
        Invocable invocable = (Invocable) engine;
        Object result = invocable.invokeFunction(funName, args);
        return result;
    }
    public Invocable getInvocable(String filePath) throws ScriptException, FileNotFoundException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(new FileReader(filePath));
        Invocable invocable = (Invocable) engine;
        return invocable;
    }

    /**
     * 从给定的js文件中获取指定接口中的方法的实例
     * @param fileLoacation js文件路径
     * @param clazz 接口的class
     * @return 返回一个指定接口方法的实例
     */
    public <T> T getMethodInterface (String fileLoacation,Class<T> clazz) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        try {
            engine.eval(new FileReader(fileLoacation));
            if (engine instanceof Invocable) {
                Invocable invocable = (Invocable) engine;
                T executeMethod = invocable.getInterface(clazz);
                return executeMethod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
