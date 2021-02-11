package example.ljava.entity;

import java.util.ArrayList;

import lombok.Data;

@Data
public class AnalysisBallForm {
    Long id;
    ArrayList<Integer> superheat;
    ArrayList<Integer> heat;
    ArrayList<Integer> warm;
    ArrayList<Integer> cold;
    ArrayList<Integer> supercold;
    String hundred[];
}
