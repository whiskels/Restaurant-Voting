package com.whiskels.voting_system.web.user;

import com.whiskels.voting_system.HasId;
import com.whiskels.voting_system.model.User;
import com.whiskels.voting_system.service.UserService;
import com.whiskels.voting_system.to.UserTo;
import com.whiskels.voting_system.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.List;

import static com.whiskels.voting_system.util.ValidationUtil.assureIdConsistent;
import static com.whiskels.voting_system.util.ValidationUtil.checkNew;


public abstract class AbstractUserController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    // Validate manually cause UniqueMailValidator doesn't work for update with user.id==null
    private WebDataBinder binder;

    @Autowired
    protected UserService service;

    @Autowired
    private UniqueMailValidator emailValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && emailValidator.supports(binder.getTarget().getClass())) {
            binder.addValidators(emailValidator);
            this.binder = binder;
        }
    }

    public List<User> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    public User get(int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    public User create(User user) {
        log.info("create {}", user);
        checkNew(user);
        return service.create(user);
    }

    public User create(UserTo userTo) {
        log.info("create from to {}", userTo);
        return create(UserUtil.createNewFromTo(userTo));
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    protected void checkAndValidateForUpdate(HasId user, int id) throws BindException {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        binder.validate();
        if (binder.getBindingResult().hasErrors()) {
            throw new BindException(binder.getBindingResult());
        }
    }

    public void update(User user, int id) throws BindException {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        service.update(user);
    }

    public User getByMail(String email) {
        log.info("getByEmail {}", email);
        return service.getByEmail(email);
    }

    public void enable(int id, boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        service.enable(id, enabled);
    }

    public User getWithVotes(int id) {
        log.info("getWithVotes {}", id);
        return service.getWithVotes(id);
    }
}