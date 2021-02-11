package example.ljava.entity.vo;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class BicolorBallForm {
    public static interface Create {
    }

    public static interface Update {
    }

    @NotNull(groups = { Update.class })
    Long id;

    @NotNull(groups = { Create.class })
    Integer num1;

    @NotNull(groups = { Create.class })
    Integer num2;

    @NotNull(groups = { Create.class })
    Integer num3;

    @NotNull(groups = { Create.class })
    Integer num4;

    @NotNull(groups = { Create.class })
    Integer num5;

    @NotNull(groups = { Create.class })
    Integer num6;

    @NotNull(groups = { Create.class })
    Integer bnum;

    @NotEmpty(groups = { Create.class })
    @Size(max = 10)
    String stage;
}
