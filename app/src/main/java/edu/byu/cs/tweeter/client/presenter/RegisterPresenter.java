package edu.byu.cs.tweeter.client.presenter;

import android.os.Build;
import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
//import android.util.Base64;
import java.util.Base64;
public class RegisterPresenter implements ServiceUserObserver<User> {

    @Override
    public void handleSucceeded(String message) {

    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException(String exception) {
        view.displayErrorMessage(exception);
    }

    @Override
    public void GetUserSucceeded(User item) {
        view.navigateToUser(item);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello, " + item.getName());
    }

    public interface View {
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    private View view;

    public RegisterPresenter(View view){
        this.view = view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload){
        view.clearErrorMessage();
        view.clearInfoMessage();
        String message = validateRegistration(firstName, lastName, alias, password, imageToUpload);

        if(message == null){
            view.displayInfoMessage("Registering...");

            Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();
            //String imageBytesBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

            new UserService().register(firstName, lastName, alias, password, imageBytesBase64, this);
        }
        else {
            view.displayErrorMessage(message);
        }
    }


    public String validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        if (imageToUpload.getDrawable() == null) {
            return "Profile image must be uploaded.";
        }
        return null;
    }
}
