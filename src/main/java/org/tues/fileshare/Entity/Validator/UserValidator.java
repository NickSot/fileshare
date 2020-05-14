package org.tues.fileshare.Entity.Validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.tues.fileshare.Entity.User;

public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User)o;

        if (user.getUsername().equals("")){
            errors.rejectValue("username", "");
        }

        if (user.getPassword().length() < 8 || user.getPassword().length() > 16){
            errors.rejectValue("password", "");
        }
    }
}
