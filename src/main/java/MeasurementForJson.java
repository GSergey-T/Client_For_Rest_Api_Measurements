import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class MeasurementForJson {
    //@JsonSetter("Value")
    private float value;
    //@JsonSetter("Raining")
    private boolean raining;
    //@JsonSetter("Time")
    private LocalDateTime time;
    //@JsonSetter("Sensor")
    private Sensor sensor;
}
