package com.varian.oiscn.encounter.dynamicform;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by gbt1220 on 10/17/2017.
 */
@Slf4j
public class ClassValueMapper<T> {
//    public static void main(String[] args) {
//        ClassValueMapper<PatientDto> classValueMapper = new ClassValueMapper<>();
//        Map<String, Object> values = new HashMap();
//        values.put("ariaId", "1111");
//        values.put("asdfsd", "slkdf");
//        values.put("birthday", new Date());
//        PatientDto patientDto = classValueMapper.newClassInstanceWithValues(PatientDto.class, values);
//        System.out.println(patientDto);
//    }

    public T newClassInstanceWithValues(Class<T> tClass, Map<String, Object> fieldValueMap) {
        T classInstance = null;
        try {
            classInstance = tClass.newInstance();
            for (Map.Entry<String, Object> entry : fieldValueMap.entrySet()) {
                String methodName = "";
                try {
                    Field field = tClass.getDeclaredField(entry.getKey());
                    methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                    Method method = tClass.getDeclaredMethod(methodName, entry.getValue().getClass());
                    method.invoke(classInstance, entry.getValue());
                } catch (NoSuchFieldException e) {
                    log.error("Not such field[{}] of class[{}]", entry.getKey(), tClass);
                } catch (NoSuchMethodException e) {
                    log.error("Not such method[{}] of class[{}]", methodName, tClass);
                } catch (InvocationTargetException e) {
                    log.error("Can't invoke method[{}] of class[{}]", methodName, tClass);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException: {}", e.getMessage());
        } catch (InstantiationException e) {
            log.error("InstantiationException: {}", e.getMessage());
        }
        return classInstance;
    }
}
