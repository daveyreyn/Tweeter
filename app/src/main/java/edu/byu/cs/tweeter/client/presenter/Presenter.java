package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

public abstract class Presenter {

    PresenterView view;

    public Presenter(PresenterView view) {
        this.view = view;
    }

    public void getFailed(String message) {
        view.displayErrorMessage(message);
    }

    public void getException(String ex) {
        view.displayInfoMessage(ex);
    }


}
