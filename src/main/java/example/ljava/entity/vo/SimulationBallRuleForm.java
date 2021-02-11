package example.ljava.entity.vo;

import lombok.Data;

/**
 * 页面传递的生成模拟号码的参数
 */
@Data
public class SimulationBallRuleForm {
    // 每个数的取值范围
    Range m1;
    Range m2;
    Range m3;
    Range m4;
    Range m5;
    Range m6;

    // 奇数/偶数 数量
    Integer numEvenNumber;
    Integer numOddNumber;
    // 大数/小数 数量
    Integer numBiggerNumber;
    Integer numSmallerNumber;

    // 生成数量
    Integer number;

    // 某个号码表示的返回 [min, max], min > 0, max > 0
    @Data
    public static class Range {
        Integer min;
        Integer max;
    }
}
