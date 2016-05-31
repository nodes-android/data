package dk.nodes.exampleproject;

import dk.nodes.data.annotation.Data;
import dk.nodes.data.annotation.Mode;
import dk.nodes.data.annotation.Persistence;

/**
 * Created by joso on 30/05/16.
 */
@Data(
        persistence = Persistence.GSON,
        mode = Mode.SHAREDPREFERENCES
)
public class UserData {
    public String token = "";
    public String userName = "";
}
