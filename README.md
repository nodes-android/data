# Data

Persistence framework for Android with output methods for the time being Gson or Java Serialization to either files or `SharedPreferences`.

## Example

Consider the following class:
```
public class UserData {
    public String token = "";
    public String userName = "";
}
```
We can either serialize it to disk:
```
@Data(
        persistence = Persistence.SERIALIZATION,
        mode = Mode.FILE
)
public class UserData implements Serializable {
    public String token = "";
    public String userName = "";
}
```
Or save it to `SharedPreferences`:
```
@Data(
        persistence = Persistence.GSON,
        mode = Mode.SHAREDPREFERENCES
)
public class UserData {
    public String token = "";
    public String userName = "";
}
```
Both of these classes will generate a Manager class which can `load` and `save`:
```
UserDataManager userDataManager = new UserDataManager(this.getApplicationContext());
UserData userData = userDataManager.load();
...
userDataManager.save(userData);
```

Every `@Data` annoated class will generate a `<class name>Manager` through annotation processing.
