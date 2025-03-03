package com.asiainfo.fsip.utils;

import cn.hutool.core.util.StrUtil;
import com.asiainfo.mcp.tmc.common.base.annotation.MustField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ValidateUtil {

    public static final String VALIDATE_ERROR_CODE = "9001";

    public static ValidateResult requestValidate(Object o){
        ValidateResult validateResult = new ValidateResult(true,"");
        if(o!=null){
            try {
                Field[] fields = o.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if(field.isAnnotationPresent(MustField.class)){
                        Annotation annotation = field.getAnnotation(MustField.class);
                        boolean mustValidate = (boolean)MustField.class.getMethod("mustValidate").invoke(annotation);
                        if(mustValidate){
                            String getMethodName = "get".concat(field.getName().substring(0,1).toUpperCase()).concat(field.getName().substring(1));
                            Object fieldValue = o.getClass().getMethod(getMethodName).invoke(o);
                            if (fieldValue==null || (String.class.isAssignableFrom(fieldValue.getClass()) && StrUtil.isEmpty(fieldValue.toString()))){
                                validateResult.setPass(false);
                                String message = (String)MustField.class.getMethod("validateDesc").invoke(annotation);
                                message = StrUtil.isEmpty(message)?"parameter [".concat(field.getName()).concat("] value required."):message;
                                validateResult.setMessage(StrUtil.isEmpty(validateResult.getMessage())?message: validateResult.getMessage().concat(StrUtil.COMMA).concat(message));
                            }
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                validateResult.setPass(false);
                validateResult.setMessage("Parameter validate fail:".concat(e.getMessage()));
            }

        }
        return validateResult;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidateResult{
        private boolean pass;
        private String message;
    }
}
