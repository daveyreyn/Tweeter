package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter implements ServiceUserObserver<User> {


    @Override
    public void GetUserSucceeded(User item) {
        view.navigateToUser(item);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello:" + item.getName());
    }

    @Override
    public void handleSucceeded(String message) {
        view.displayInfoMessage(message);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException(String exception) {
        view.displayErrorMessage(exception);
    }

    public interface View extends PresenterView {
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    private View view;

    public LoginPresenter(View view){
        super(view);
        this.view = view;
    }
    public void login(String alias, String password){
        view.clearErrorMessage();
        view.clearInfoMessage();

        String message = validateLogin(alias, password);
        if(message == null){
            view.displayInfoMessage("Logging In...");
            new UserService().login(alias, password, this);
        }
        else {
            view.displayErrorMessage(message);
        }
    }

    public String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if(alias.length() == 0){
            return "Alias cannot be empty";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }
}
