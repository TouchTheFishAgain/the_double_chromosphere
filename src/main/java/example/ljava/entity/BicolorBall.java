package example.ljava.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "bicolor_ball")
@Data
public class BicolorBall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Integer num1;
    Integer num2;
    Integer num3;
    Integer num4;
    Integer num5;
    Integer num6;
    Integer bnum;
    String stage;
}
