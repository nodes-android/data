package dk.nodes.exampleproject;

import java.io.Serializable;

import dk.nodes.data.annotation.Data;
import dk.nodes.data.annotation.Mode;
import dk.nodes.data.annotation.Persistence;

/**
 * Created by joso on 30/05/16.
 */
@Data(
        persistence = Persistence.GSON,
        mode = Mode.FILE
)
public class UserData implements Serializable {
}
