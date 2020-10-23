package com.whiskels.voting_system.web.user;

import com.whiskels.voting_system.HasIdAndEmail;
import com.whiskels.voting_system.model.User;
import com.whiskels.voting_system.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class UniqueMailValidator implements org.springframework.validation.Validator {

    private final UserRepository repository;

    public UniqueMailValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return HasIdAndEmail.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HasIdAndEmail user = ((HasIdAndEmail) target);
        User dbUser = repository.getByEmail(user.getEmail().toLowerCase());
        if (dbUser != null && !dbUser.getId().equals(user.getId())) {
            errors.rejectValue("email", "User with this e-mail already exists!");
        }
    }
}