package example.ljava.entity.vo;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import example.ljava.exception.RoutineException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class SimulationBallMeta {
    int numEvenNumber;
    int numOddNumber;
    int numBiggerNumber;
    int numSmallerNumber;
    Integer m1;
    Integer m2;
    Integer m3;
    Integer m4;
    Integer m5;
    Integer m6;

    public void setBallVal(String name, Integer val) throws RoutineException {
        try {
            Integer prevVal = (Integer) PropertyUtils.getProperty(this, name);
            PropertyUtils.setProperty(this, name, val);
            // 更新值, 改变原有计数
            if (prevVal != null) {
                // 奇偶计数更新
                if (prevVal.intValue() % 2 != 0) {
                    numOddNumber -= 1;
                } else {
                    numEvenNumber -= 1;
                }
                // 大小计数更新
                if (isBiggerNumber(prevVal)) {
                    numBiggerNumber -= 1;
                } else {
                    numSmallerNumber -= 1;
                }
            }
            // 增加计数
            if (val != null) {
                if (val.intValue() % 2 != 0) {
                    numOddNumber += 1;
                } else {
                    numEvenNumber += 1;
                }
                if (isBiggerNumber(val)) {
                    numBiggerNumber += 1;
                } else {
                    numSmallerNumber += 1;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage(), e);
            throw new RoutineException("设置失败");
        }
    }

    public static boolean isBiggerNumber(int val) {
        return val > 16;
    }

    /**
     * 大小比
     * 
     * @return
     */
    public double bsratio() {
        return this.numBiggerNumber / this.numSmallerNumber;
    }

    /**
     * 奇偶比
     * 
     * @return
     */
    public double oeratio() {
        return this.numOddNumber / this.numEvenNumber;
    }
}
