package user_model;

/**
 * Created by VladVin on 18.12.2015.
 */
public class UserModelFactory {
    public enum UserModelType { BASE_USER_MODEL }

    public IUserModel createUserModel(UserModelType type) {
        switch (type) {
            case BASE_USER_MODEL:
                return new BaseUserModel();
            default:
                return null;
        }
    }
}
